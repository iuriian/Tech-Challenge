# Equivalente declarativo ao "kubectl create configmap --from-file=nginx.conf=conf/nginx.conf"
resource "kubernetes_config_map" "nginx_config" {
  metadata {
    name = "nginx-config"
  }
  data = {
    "nginx.conf" = file("${path.module}/../conf/nginx.conf")
  }
}

resource "kubernetes_deployment" "nginx" {
  metadata {
    name   = "nginx"
    labels = { app = "nginx" }
  }
  spec {
    replicas = 1
    selector {
      match_labels = { app = "nginx" }
    }
    template {
      metadata {
        labels = { app = "nginx" }
      }
      spec {
        container {
          name              = "nginx"
          image             = "nginx:alpine"
          image_pull_policy = "IfNotPresent"
          port {
            container_port = 80
          }
          volume_mount {
            name       = "config"
            mount_path = "/etc/nginx/nginx.conf"
            sub_path   = "nginx.conf"
            read_only  = true
          }
        }
        volume {
          name = "config"
          config_map {
            name = kubernetes_config_map.nginx_config.metadata[0].name
          }
        }
      }
    }
  }
}

resource "kubernetes_service" "nginx" {
  metadata {
    name = "nginx"
  }
  spec {
    type     = "NodePort"
    selector = { app = "nginx" }
    port {
      port        = 80
      target_port = 80
      node_port   = 30080
    }
  }
}
