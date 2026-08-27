resource "kubernetes_config_map" "keycloak_config" {
  metadata {
    name = "keycloak-config"
  }
  data = {
    KC_BOOTSTRAP_ADMIN_USERNAME = "admin"
    KC_HOSTNAME                 = "localhost"
    KC_DB                       = "postgres"
    KC_DB_URL                   = "jdbc:postgresql://db:5432/keycloak"
  }
}

resource "kubernetes_secret" "keycloak_secret" {
  metadata {
    name = "keycloak-secret"
  }
  type = "Opaque"
  data = {
    KC_BOOTSTRAP_ADMIN_PASSWORD = var.keycloak_admin_password
  }
}

# Equivalente declarativo ao "kubectl create configmap --from-file=realm.json=conf/realm-export.json"
resource "kubernetes_config_map" "keycloak_realm" {
  metadata {
    name = "keycloak-realm"
  }
  data = {
    "realm.json" = file("${path.module}/../conf/realm-export.json")
  }
}

resource "kubernetes_deployment" "keycloak" {
  metadata {
    name   = "keycloak"
    labels = { app = "keycloak" }
  }
  spec {
    replicas = 1
    selector {
      match_labels = { app = "keycloak" }
    }
    template {
      metadata {
        labels = { app = "keycloak" }
      }
      spec {
        init_container {
          name    = "wait-for-db"
          image   = "postgres:16-alpine"
          command = ["sh", "-c", "until pg_isready -h db -U ${var.db_user}; do sleep 2; done"]
        }
        container {
          name              = "keycloak"
          image             = "quay.io/keycloak/keycloak:26.2.1"
          image_pull_policy = "IfNotPresent"
          args              = ["start-dev", "--import-realm"]
          port {
            container_port = 8080
          }
          env_from {
            config_map_ref { name = kubernetes_config_map.keycloak_config.metadata[0].name }
          }
          env_from {
            secret_ref { name = kubernetes_secret.keycloak_secret.metadata[0].name }
          }
          env {
            name = "KC_DB_USERNAME"
            value_from {
              secret_key_ref {
                name = kubernetes_secret.db_credentials.metadata[0].name
                key  = "POSTGRES_USER"
              }
            }
          }
          env {
            name = "KC_DB_PASSWORD"
            value_from {
              secret_key_ref {
                name = kubernetes_secret.db_credentials.metadata[0].name
                key  = "POSTGRES_PASSWORD"
              }
            }
          }
          volume_mount {
            name       = "realm"
            mount_path = "/opt/keycloak/data/import/realm.json"
            sub_path   = "realm.json"
            read_only  = true
          }
        }
        volume {
          name = "realm"
          config_map {
            name = kubernetes_config_map.keycloak_realm.metadata[0].name
          }
        }
      }
    }
  }
}

resource "kubernetes_service" "keycloak" {
  metadata {
    name = "keycloak"
  }
  spec {
    type     = "NodePort"
    selector = { app = "keycloak" }
    port {
      port        = 8080
      target_port = 8080
      node_port   = 30081
    }
  }
}
