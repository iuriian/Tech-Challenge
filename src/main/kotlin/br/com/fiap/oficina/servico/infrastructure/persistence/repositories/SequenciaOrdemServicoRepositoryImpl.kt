package br.com.fiap.oficina.servico.infrastructure.persistence.repositories

import br.com.fiap.oficina.servico.domain.repositories.SequenciaOrdemServicoRepository
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository

@Repository
class SequenciaOrdemServicoRepositoryImpl(private val entityManager: EntityManager) : SequenciaOrdemServicoRepository {
    override fun obterProximoValor(): Long = (
        entityManager
            .createNativeQuery("SELECT nextval('ordem_servico_numero_seq')")
            .singleResult as Number
        ).toLong()
}
