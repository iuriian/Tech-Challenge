package br.com.fiap.oficina.domain.enum

enum class OrdemServicoStatus(val descricao: String) {
    RECEBIDA("Recebida"),
    EM_DIAGNOSTICO("Em Diagnostico"),
    AGUARDANDO_APROVACAO("Aguardando aprovação"),
    EM_EXECUCAO("Em Execução"),
    FINALIZADA("Finalizada"),
    ENTREGUE("Entregue"),
    CANCELADA("Cancelada pelo cliente");

    /**
     * Retorna o próximo status do fluxo principal.
     *
     * CANCELADA é uma transição alternativa e explícita,
     * portanto não parte do fluxo principal.
     */
    fun proximoStatus(): OrdemServicoStatus? =
        when (this) {
            RECEBIDA -> EM_DIAGNOSTICO
            EM_DIAGNOSTICO -> AGUARDANDO_APROVACAO
            AGUARDANDO_APROVACAO -> EM_EXECUCAO
            EM_EXECUCAO -> FINALIZADA
            FINALIZADA -> ENTREGUE
            ENTREGUE,
            CANCELADA,
            -> null
        }

    fun transicoesPermitidas(): Set<OrdemServicoStatus> =
        when (this) {
            AGUARDANDO_APROVACAO -> setOf(EM_EXECUCAO, CANCELADA)
            else -> setOfNotNull(proximoStatus())
        }
}
