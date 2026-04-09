variable "project_id" {
  description = "ID del proyecto GCP donde se desplegará la infraestructura"
  type        = string
}

variable "region" {
  description = "Región de GCP para todos los recursos"
  type        = string
  default     = "us-central1"
}

variable "app_image" {
  description = "URI completa de la imagen Docker en Artifact Registry"
  type        = string
  # Ejemplo: us-central1-docker.pkg.dev/my-project/hiberus-payment/hiberus-payment-api:latest
}

variable "tf_state_bucket" {
  description = "Nombre del bucket GCS para almacenar el estado de Terraform"
  type        = string
}
