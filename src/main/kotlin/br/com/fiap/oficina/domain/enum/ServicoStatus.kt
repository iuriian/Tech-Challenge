package br.com.fiap.oficina.domain.enum

enum class ServicoStatus(val descricao: String) {
    RECEBIDA("Recebida"),
    EM_DIAGNOSTICO("Em Diagnostico"),
    AGUARDANDO_APROVACAO("Aguardando aprovação"),
    EM_EXECUCAO("Em Execução"),
    FINALIZADA("Finalizada"),
    ENTREGUE("Entregue"),
}