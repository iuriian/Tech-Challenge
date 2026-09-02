# ---------------------------------------------------------------------------
# APIs necessarias. Declaradas aqui para que `terraform apply` funcione em um
# projeto novo sem passos manuais previos.
#
# disable_on_destroy = false: desabilitar a API do GKE ao destruir o cluster
# afetaria qualquer outro recurso do projeto que dependa dela.
# ---------------------------------------------------------------------------
resource "google_project_service" "container" {
  project            = var.project_id
  service            = "container.googleapis.com"
  disable_on_destroy = false
}

resource "google_project_service" "compute" {
  project            = var.project_id
  service            = "compute.googleapis.com"
  disable_on_destroy = false
}
