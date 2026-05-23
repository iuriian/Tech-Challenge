package br.com.fiap.oficina.domain.repository

import br.com.fiap.oficina.domain.entity.Cliente
import br.com.fiap.oficina.domain.entity.Documento
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ClienteRepository : JpaRepository<Cliente, Long> {

    fun findByDocumentoNumero(numeroDocumento: String): Cliente?

    fun findByNome(nome: String): Cliente?
}
