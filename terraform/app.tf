resource "kubernetes_config_map" "app_config" {
  metadata {
    name = "app-config"
  }
  data = {
    DB_URL                = "jdbc:postgresql://db:5432/oficina"
    KEYCLOAK_URL           = "http://localhost:8081"
    KEYCLOAK_REALM         = "Fiap"
    KEYCLOAK_CLIENT_ID     = "oficina"
    KEYCLOAK_JWK_SET_URI   = "http://keycloak:8080/realms/Fiap/protocol/openid-connect/certs"
  }
}

resource "kubernetes_deployment" "app" {
  metadata {
    name   = "app"
    labels = { app = "app" }
  }
  spec {
    replicas = 1
    selector {
      match_labels = { app = "app" }
    }
    template {
      metadata {
        labels = { app = "app" }
      }
      spec {
        init_container {
          name    = "wait-for-db"
          image   = "postgres:16-alpine"
          command = ["sh", "-c", "until pg_isready -h db -U ${var.db_user}; do sleep 2; done"]
        }
        container {
          name              = "app"
          image             = var.app_image
          image_pull_policy = "IfNotPresent"
          port {
            container_port = 8080
          }
          env_from {
            config_map_ref { name = kubernetes_config_map.app_config.metadata[0].name }
          }
          env {
            name = "DB_USERNAME"
            value_from {
              secret_key_ref {
                name = kubernetes_secret.db_credentials.metadata[0].name
                key  = "POSTGRES_USER"
              }
            }
          }
          env {
            name = "DB_PASSWORD"
            value_from {
              secret_key_ref {
                name = kubernetes_secret.db_credentials.metadata[0].name
                key  = "POSTGRES_PASSWORD"
              }
            }
          }
        }
      }
    }
  }
}

resource "kubernetes_service" "app" {
  metadata {
    name = "app"
  }
  spec {
    # Mantido ClusterIP: o acesso externo real e feito via nginx (nginx.tf).
    # Para acesso direto sem nginx use: kubectl port-forward service/app 8080:8080
    #
    # Alternativa, caso queira localhost:8080 fixo sem port-forward, troque por:
    # type = "NodePort"
    # e adicione node_port = 30083 na porta abaixo (mais o mapeamento correspondente
    # em extraPortMappings do kind-cluster.yaml).
    selector = { app = "app" }
    port {
      port        = 8080
      target_port = 8080
    }
  }
}
