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

output "secret_names" {
  description = "Nomes dos segredos no Secret Manager que o cd-staging.yml consome"
  value = {
    db_password             = google_secret_manager_secret.db_password.secret_id
    keycloak_admin_password = google_secret_manager_secret.keycloak_admin_password.secret_id
  }
}

output "keycloak_admin_password_command" {
  description = "Como ler a senha do admin do Keycloak quando precisar dela"
  value       = "gcloud secrets versions access latest --secret=${google_secret_manager_secret.keycloak_admin_password.secret_id}"
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

output "capacity_note" {
  description = "Capacidade util aproximada, para comparar com os requests de k8s/"
  value = join(" ", [
    "${var.min_node_count} a ${var.max_node_count}x ${var.machine_type} ->",
    "~${format("%.1f", var.min_node_count * 2.2)}Gi a",
    "~${format("%.1f", var.max_node_count * 2.2)}Gi utilizaveis;",
    "a stack pede ~1,25Gi com 1 replica, e o HPA escala ate ${var.max_node_count}.",
  ])
}
