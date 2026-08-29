resource "kubernetes_config_map" "sonarqube_config" {
  metadata {
    name = "sonarqube-config"
  }
  data = {
    SONAR_JDBC_URL                     = "jdbc:postgresql://db:5432/sonarqube"
    SONAR_ES_BOOTSTRAP_CHECKS_DISABLE  = "true"
    SONAR_SEARCH_JAVAADDITIONALOPTS    = "-Dnode.store.allow_mmap=false"
  }
}

resource "kubernetes_persistent_volume_claim" "sonarqube_data" {
  metadata { name = "sonarqube-data" }
  spec {
    access_modes = ["ReadWriteOnce"]
    resources { requests = { storage = "2Gi" } }
  }
  wait_until_bound = false
}

resource "kubernetes_persistent_volume_claim" "sonarqube_extensions" {
  metadata { name = "sonarqube-extensions" }
  spec {
    access_modes = ["ReadWriteOnce"]
    resources { requests = { storage = "500Mi" } }
  }
  wait_until_bound = false
}

resource "kubernetes_persistent_volume_claim" "sonarqube_logs" {
  metadata { name = "sonarqube-logs" }
  spec {
    access_modes = ["ReadWriteOnce"]
    resources { requests = { storage = "500Mi" } }
  }
  wait_until_bound = false
}

resource "kubernetes_deployment" "sonarqube" {
  metadata {
    name   = "sonarqube"
    labels = { app = "sonarqube" }
  }
  spec {
    replicas = 1
    selector {
      match_labels = { app = "sonarqube" }
    }
    template {
      metadata {
        labels = { app = "sonarqube" }
      }
      spec {
        init_container {
          name    = "wait-for-db"
          image   = "postgres:16-alpine"
          command = ["sh", "-c", "until pg_isready -h db -U ${var.db_user}; do sleep 2; done"]
        }
        container {
          name              = "sonarqube"
          image             = "sonarqube:lts-community"
          image_pull_policy = "IfNotPresent"
          port {
            container_port = 9000
          }
          env_from {
            config_map_ref { name = kubernetes_config_map.sonarqube_config.metadata[0].name }
          }
          env {
            name = "SONAR_JDBC_USERNAME"
            value_from {
              secret_key_ref {
                name = kubernetes_secret.db_credentials.metadata[0].name
                key  = "POSTGRES_USER"
              }
            }
          }
          env {
            name = "SONAR_JDBC_PASSWORD"
            value_from {
              secret_key_ref {
                name = kubernetes_secret.db_credentials.metadata[0].name
                key  = "POSTGRES_PASSWORD"
              }
            }
          }
          volume_mount {
            name       = "data"
            mount_path = "/opt/sonarqube/data"
          }
          volume_mount {
            name       = "extensions"
            mount_path = "/opt/sonarqube/extensions"
          }
          volume_mount {
            name       = "logs"
            mount_path = "/opt/sonarqube/logs"
          }
          startup_probe {
            http_get {
              path = "/api/system/status"
              port = 9000
            }
            failure_threshold = 30
            period_seconds    = 10
          }
          readiness_probe {
            http_get {
              path = "/api/system/status"
              port = 9000
            }
            period_seconds = 15
          }
        }
        volume {
          name = "data"
          persistent_volume_claim {
            claim_name = kubernetes_persistent_volume_claim.sonarqube_data.metadata[0].name
          }
        }
        volume {
          name = "extensions"
          persistent_volume_claim {
            claim_name = kubernetes_persistent_volume_claim.sonarqube_extensions.metadata[0].name
          }
        }
        volume {
          name = "logs"
          persistent_volume_claim {
            claim_name = kubernetes_persistent_volume_claim.sonarqube_logs.metadata[0].name
          }
        }
      }
    }
  }
}

resource "kubernetes_service" "sonarqube" {
  metadata {
    name = "sonarqube"
  }
  spec {
    type     = "NodePort"
    selector = { app = "sonarqube" }
    port {
      port        = 9000
      target_port = 9000
      node_port   = 30090
    }
  }
}
