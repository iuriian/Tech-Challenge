# Valores para conferir contra o Environment 'staging' do GitHub.
output "github_environment_variables" {
  description = "Variables que o cd-staging.yml le via vars.*"
  value = {
    GCP_PROJECT_ID = var.project_id
    GKE_CLUSTER    = google_container_cluster.staging.name
    GKE_ZONE       = google_container_cluster.staging.location
  }
}

output "ci_service_account_email" {
  description = "Service account do CI - use para gerar a chave do secret GCP_SA_KEY"
  value       = google_service_account.ci.email
}

output "get_credentials_command" {
  description = "Aponta o kubectl local para o cluster"
  value = join(" ", [
    "gcloud container clusters get-credentials",
    google_container_cluster.staging.name,
    "--zone", google_container_cluster.staging.location,
    "--project", var.project_id,
  ])
}

output "allocatable_note" {
  description = "Capacidade util aproximada, para comparar com os requests de k8s/"
  value       = "${var.node_count}x ${var.machine_type} -> ~${var.node_count * 2.2}Gi utilizaveis; a stack pede 1,75Gi"
}
