output "cloud_run_url" {
  description = "URL pública del microservicio desplegado en Cloud Run"
  value       = google_cloud_run_v2_service.payment_api.uri
}

output "artifact_registry_url" {
  description = "URL del repositorio de Artifact Registry para push de imágenes"
  value       = "${var.region}-docker.pkg.dev/${var.project_id}/hiberus-payment"
}

output "database_private_ip" {
  description = "IP privada de la instancia Cloud SQL"
  value       = google_sql_database_instance.postgres.private_ip_address
  sensitive   = true
}

output "db_password_secret" {
  description = "Nombre del Secret en Secret Manager con la contraseña de la DB"
  value       = google_secret_manager_secret.db_password.secret_id
}
