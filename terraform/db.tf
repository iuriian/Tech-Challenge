resource "kubernetes_secret" "db_credentials" {
  metadata {
    name = "db-credentials"
  }
  type = "Opaque"
  data = {
    POSTGRES_USER     = var.db_user
    POSTGRES_PASSWORD = var.db_password
  }
}

resource "kubernetes_config_map" "db_config" {
  metadata {
    name = "db-config"
  }
  data = {
    POSTGRES_DB = "oficina"
  }
}

# Equivalente declarativo ao "kubectl create configmap --from-file=conf/init-db"
# Caminho relativo ao diretorio terraform/ -> ajuste se sua estrutura for diferente.
resource "kubernetes_config_map" "db_init_scripts" {
  metadata {
    name = "db-init-scripts"
  }
  data = {
    for f in fileset("${path.module}/../conf/init-db", "*") :
    f => file("${path.module}/../conf/init-db/${f}")
  }
}

resource "kubernetes_persistent_volume_claim" "postgres_data" {
  metadata {
    name = "postgres-data"
  }
  spec {
    access_modes = ["ReadWriteOnce"]
    resources {
      requests = { storage = "1Gi" }
    }
  }
  wait_until_bound = false
}

resource "kubernetes_deployment" "db" {
  metadata {
    name   = "db"
    labels = { app = "db" }
  }
  spec {
    replicas = 1
    selector {
      match_labels = { app = "db" }
    }
    template {
      metadata {
        labels = { app = "db" }
      }
      spec {
        container {
          name              = "db"
          image             = "postgres:16-alpine"
          image_pull_policy = "IfNotPresent"
          port {
            container_port = 5432
          }
          env_from {
            config_map_ref { name = kubernetes_config_map.db_config.metadata[0].name }
          }
          env_from {
            secret_ref { name = kubernetes_secret.db_credentials.metadata[0].name }
          }
          volume_mount {
            name       = "data"
            mount_path = "/var/lib/postgresql/data"
          }
          volume_mount {
            name       = "init-scripts"
            mount_path = "/docker-entrypoint-initdb.d"
            read_only  = true
          }
          readiness_probe {
            exec {
              command = ["pg_isready", "-U", var.db_user, "-d", "oficina"]
            }
            period_seconds   = 10
            timeout_seconds  = 5
            failure_threshold = 5
          }
          liveness_probe {
            exec {
              command = ["pg_isready", "-U", var.db_user, "-d", "oficina"]
            }
            period_seconds = 10
          }
        }
        volume {
          name = "data"
          persistent_volume_claim {
            claim_name = kubernetes_persistent_volume_claim.postgres_data.metadata[0].name
          }
        }
        volume {
          name = "init-scripts"
          config_map {
            name = kubernetes_config_map.db_init_scripts.metadata[0].name
          }
        }
      }
    }
  }
}

resource "kubernetes_service" "db" {
  metadata {
    name = "db"
  }
  spec {
    selector = { app = "db" }
    port {
      port        = 5432
      target_port = 5432
    }
  }
}
