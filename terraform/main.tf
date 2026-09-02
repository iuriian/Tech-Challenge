# ---------------------------------------------------------------------------
# Cluster GKE de staging.
#
# O nome e a zona precisam casar com as variables GKE_CLUSTER e GKE_ZONE do
# Environment 'staging' no GitHub, usadas pelo cd-staging.yml no get-credentials.
# ---------------------------------------------------------------------------
resource "google_container_cluster" "staging" {
  name     = var.cluster_name
  location = var.zone

  # O pool default e descartado e substituido pelo declarado abaixo. Assim,
  # trocar machine_type recria apenas o node pool, e nao o cluster inteiro.
  remove_default_node_pool = true
  initial_node_count       = 1

  # Staging descartavel: permite `terraform destroy`. Em producao, deixe o
  # padrao (true) para nao apagar o cluster por acidente.
  deletion_protection = false

  networking_mode = "VPC_NATIVE"
  ip_allocation_policy {}

  release_channel {
    channel = "REGULAR"
  }

  depends_on = [google_project_service.container]
}

resource "google_container_node_pool" "primary" {
  name       = "primary"
  cluster    = google_container_cluster.staging.name
  location   = var.zone
  node_count = var.node_count

  management {
    auto_repair  = true
    auto_upgrade = true
  }

  # Sobe um no novo antes de drenar o antigo durante upgrades, evitando que a
  # stack fique sem capacidade no meio de uma manutencao do GKE.
  upgrade_settings {
    max_surge       = 1
    max_unavailable = 0
  }

  node_config {
    machine_type = var.machine_type
    disk_size_gb = var.disk_size_gb
    disk_type    = "pd-balanced"
    spot         = var.use_spot_vms

    oauth_scopes = [
      "https://www.googleapis.com/auth/cloud-platform",
    ]

    labels = {
      environment = "staging"
      managed-by  = "terraform"
    }
  }

  # Um `gcloud container clusters resize` feito as pressas nao vira drift.
  lifecycle {
    ignore_changes = [node_count]
  }
}
