terraform {
  required_version = ">= 1.7.0"

  required_providers {
    google = {
      source  = "hashicorp/google"
      version = "~> 5.0"
    }
  }

  backend "gcs" {
    # El bucket se rellena en terraform init -backend-config o en terraform.tfvars
    # terraform init -backend-config="bucket=${var.tf_state_bucket}"
  }
}

provider "google" {
  credentials = file("sa-key.json")
  project     = var.project_id
  region      = var.region
}

# =============================================================================
# Red (VPC + Serverless VPC Access Connector)
# Permite que Cloud Run se comunique con Cloud SQL de forma privada
# =============================================================================
resource "google_compute_network" "vpc" {
  name                    = "hiberus-payment-vpc"
  auto_create_subnetworks = false
}

resource "google_compute_subnetwork" "subnet" {
  name          = "hiberus-payment-subnet"
  ip_cidr_range = "10.0.0.0/24"
  network       = google_compute_network.vpc.id
  region        = var.region
}

resource "google_vpc_access_connector" "connector" {
  name          = "hiberus-payment-connector"
  region        = var.region
  network       = google_compute_network.vpc.name
  ip_cidr_range = "10.8.0.0/28"
}

# =============================================================================
# Artifact Registry - Registro privado de imágenes Docker
# =============================================================================
resource "google_artifact_registry_repository" "docker_repo" {
  repository_id = "hiberus-payment"
  format        = "DOCKER"
  location      = var.region
  description   = "Repositorio de imágenes Docker para Hiberus Payment API"
}

# =============================================================================
# Cloud SQL - PostgreSQL 15 gestionado
# =============================================================================
resource "google_sql_database_instance" "postgres" {
  name             = "hiberus-payment-db"
  database_version = "POSTGRES_15"
  region           = var.region

  settings {
    tier              = "db-f1-micro" # Tier gratuito para demo/prueba
    availability_type = "ZONAL"
    disk_type         = "PD_SSD"
    disk_size         = 10

    ip_configuration {
      ipv4_enabled    = false
      private_network = google_compute_network.vpc.id
    }

    backup_configuration {
      enabled = false # Deshabilitado para minimizar costes en prueba técnica
    }
  }

  deletion_protection = false # Facilita el cleanup en entorno de testing
}

resource "google_sql_database" "payment_db" {
  name     = "payment_orders"
  instance = google_sql_database_instance.postgres.name
}

resource "google_sql_user" "app_user" {
  name     = "payment_user"
  instance = google_sql_database_instance.postgres.name
  password = random_password.db_password.result
}

resource "random_password" "db_password" {
  length  = 24
  special = false
}

# =============================================================================
# Secret Manager - Almacenamiento seguro de credenciales
# =============================================================================
resource "google_secret_manager_secret" "db_password" {
  secret_id = "payment-db-password"

  replication {
    auto {}
  }
}

resource "google_secret_manager_secret_version" "db_password" {
  secret      = google_secret_manager_secret.db_password.id
  secret_data = random_password.db_password.result
}

resource "google_secret_manager_secret" "db_url" {
  secret_id = "payment-db-r2dbc-url"

  replication {
    auto {}
  }
}

resource "google_secret_manager_secret_version" "db_url" {
  secret      = google_secret_manager_secret.db_url.id
  secret_data = "r2dbc:postgresql://${google_sql_database_instance.postgres.private_ip_address}:5432/${google_sql_database.payment_db.name}"
}

# =============================================================================
# Service Account - Identidad de Cloud Run (mínimos privilegios)
# =============================================================================
resource "google_service_account" "cloud_run_sa" {
  account_id   = "hiberus-payment-run-sa"
  display_name = "Hiberus Payment API - Cloud Run Service Account"
}

resource "google_project_iam_member" "secret_accessor" {
  project = var.project_id
  role    = "roles/secretmanager.secretAccessor"
  member  = "serviceAccount:${google_service_account.cloud_run_sa.email}"
}

resource "google_project_iam_member" "sql_client" {
  project = var.project_id
  role    = "roles/cloudsql.client"
  member  = "serviceAccount:${google_service_account.cloud_run_sa.email}"
}

# =============================================================================
# Cloud Run - Microservicio de Órdenes de Pago
# =============================================================================
resource "google_cloud_run_v2_service" "payment_api" {
  name     = "hiberus-payment-api"
  location = var.region

  template {
    service_account = google_service_account.cloud_run_sa.email

    vpc_access {
      connector = google_vpc_access_connector.connector.id
      egress    = "PRIVATE_RANGES_ONLY"
    }

    containers {
      image = var.app_image

      ports {
        container_port = 8080
      }

      resources {
        limits = {
          cpu    = "1"
          memory = "512Mi"
        }
      }

      env {
        name = "SPRING_R2DBC_URL"
        value_source {
          secret_key_ref {
            secret  = google_secret_manager_secret.db_url.secret_id
            version = "latest"
          }
        }
      }

      env {
        name = "SPRING_R2DBC_USERNAME"
        value = google_sql_user.app_user.name
      }

      env {
        name = "SPRING_R2DBC_PASSWORD"
        value_source {
          secret_key_ref {
            secret  = google_secret_manager_secret.db_password.secret_id
            version = "latest"
          }
        }
      }

      env {
        name  = "SPRING_FLYWAY_URL"
        value = "jdbc:postgresql://${google_sql_database_instance.postgres.private_ip_address}:5432/${google_sql_database.payment_db.name}"
      }

      env {
        name  = "SPRING_FLYWAY_USER"
        value = google_sql_user.app_user.name
      }

      env {
        name = "SPRING_FLYWAY_PASSWORD"
        value_source {
          secret_key_ref {
            secret  = google_secret_manager_secret.db_password.secret_id
            version = "latest"
          }
        }
      }
    }
  }

  depends_on = [
    google_vpc_access_connector.connector,
    google_sql_database_instance.postgres,
    google_secret_manager_secret_version.db_password,
    google_secret_manager_secret_version.db_url,
  ]
}

# Permitir acceso público no autenticado (API REST pública)
resource "google_cloud_run_v2_service_iam_member" "public_access" {
  project  = var.project_id
  location = var.region
  name     = google_cloud_run_v2_service.payment_api.name
  role     = "roles/run.invoker"
  member   = "allUsers"
}
