output "nginx_node_port" {
  value = kubernetes_service.nginx.spec[0].port[0].node_port
}

output "keycloak_node_port" {
  value = kubernetes_service.keycloak.spec[0].port[0].node_port
}

output "sonarqube_node_port" {
  value = kubernetes_service.sonarqube.spec[0].port[0].node_port
}
