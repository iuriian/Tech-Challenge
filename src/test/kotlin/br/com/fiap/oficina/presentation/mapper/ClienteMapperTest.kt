package br.com.fiap.oficina.presentation.mapper

import br.com.fiap.oficina.domain.entity.Cliente
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.UUID

class ClienteMapperTest {

    private val mapper = ClienteMapper.INSTANCE

    @Test
    fun `deve mapear Cliente para ClienteResponse`() {
        val cliente = Cliente().apply {
            id = UUID.randomUUID()
            nome = "João Silva"
            cpf = "123.456.789-00"
        }

        val response = mapper.toResponse(cliente)

        assertEquals(cliente.nome, response.nome)
        assertEquals(cliente.cpf, response.cpf)
    }
}
