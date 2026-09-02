variable "project_id" {
  description = "ID do projeto GCP. Deve casar com a variable GCP_PROJECT_ID do Environment 'staging'."
  type        = string
}

variable "region" {
  description = "Regiao padrao dos recursos"
  type        = string
  default     = "us-central1"
}

variable "zone" {
  description = "Zona do cluster. Cluster zonal e mais barato que regional e suficiente para staging. Deve casar com a variable GKE_ZONE."
  type        = string
  default     = "us-central1-a"
}

variable "cluster_name" {
  description = "Nome do cluster. Deve casar com a variable GKE_CLUSTER."
  type        = string
  default     = "tech-challenge-staging"
}

# DIMENSIONAMENTO
#
# Requests declarados em k8s/: app 200m/512Mi + keycloak 200m/512Mi +
# postgres 100m/256Mi = 500m de CPU e 1,25Gi de memoria (app com 1 replica).
#
# Um no de 4GB nao oferece 4GB: o GKE reserva ~1,3GB para o sistema e ainda
# roda kube-dns, fluentbit e metrics-agent, sobrando ~2,2Gi uteis e ~690m de
# CPU. Um unico e2-medium comporta os 500m/1,25Gi com folga de memoria e
# margem apertada de CPU - por isso o app/deployment.yaml usa maxSurge: 0, para
# nao exigir capacidade de dois pods durante o rollout.
#
# e2-small (2GB) NAO funciona: sobram ~600Mi por no e a aplicacao nao chega a
# ser agendada.
variable "machine_type" {
  description = "Tipo de maquina dos nos"
  type        = string
  default     = "e2-medium"
}

variable "node_count" {
  description = "Numero de nos no pool"
  type        = number
  default     = 1
}

# Spot VMs custam 60-91% menos. A VM pode ser reivindicada pelo Google com 25s
# de aviso e os pods sao reagendados - risco aceitavel em um staging que ja e
# descartavel. Em GKE Standard, node pools spot recebem apenas labels, sem taint,
# entao os workloads sao agendados sem precisar de tolerations.
variable "use_spot_vms" {
  description = "Usar Spot VMs no node pool (muito mais barato, pode ser reivindicada)"
  type        = bool
  default     = true
}

variable "disk_size_gb" {
  description = "Disco por no. O padrao do GKE e 100GB; 30GB basta para staging e reduz custo."
  type        = number
  default     = 30
}

variable "ci_service_account_id" {
  description = "ID da service account usada pelo GitHub Actions"
  type        = string
  default     = "github-actions-cd"
}
