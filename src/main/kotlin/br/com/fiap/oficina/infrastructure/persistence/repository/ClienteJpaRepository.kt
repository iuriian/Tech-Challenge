package br.com.fiap.oficina.infrastructure.persistence.repository

import br.com.fiap.oficina.infrastructure.persistence.entity.ClienteJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ClienteJpaRepository : JpaRepository<ClienteJpaEntity, Long> {

    fun findByDocumentoNumero(numeroDocumento: String): ClienteJpaEntity?

    fun findByNome(nome: String): ClienteJpaEntity?
}
