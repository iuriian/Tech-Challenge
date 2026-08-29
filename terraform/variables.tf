variable "app_image" {
  description = "Imagem Docker da aplicacao (ja carregada no Kind via kind load docker-image)"
  type        = string
  default     = "oficina-app:0.1.0"
}

variable "db_user" {
  type    = string
  default = "user"
}

variable "db_password" {
  type      = string
  default   = "password"
  sensitive = true
}

variable "keycloak_admin_password" {
  type      = string
  default   = "admin"
  sensitive = true
}
