package br.com.fiap.oficina.infrastructure.persistence.jpa.repository

import br.com.fiap.oficina.infrastructure.persistence.jpa.entity.ClienteJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ClienteJpaRepository : JpaRepository<ClienteJpaEntity, UUID> {

    fun findByDocumentoNumero(numeroDocumento: String): ClienteJpaEntity?

    fun findByNome(nome: String): ClienteJpaEntity?
}
