package br.com.fiap.oficina.infrastructure.persistence.repository

import br.com.fiap.oficina.domain.repository.SequenciaOrdemServicoRepository
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository

@Repository
class SequenciaOrdemServicoRepositoryAdapter(private val entityManager: EntityManager) :
    SequenciaOrdemServicoRepository {
    override fun obterProximoValor(): Long = (
        entityManager
            .createNativeQuery("SELECT nextval('ordem_servico_numero_seq')")
            .singleResult as Number
        ).toLong()
}
