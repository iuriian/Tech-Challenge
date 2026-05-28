package br.com.fiap.oficina.domain.entity

import br.com.fiap.oficina.infrastructure.persistence.entity.Cliente
import br.com.fiap.oficina.infrastructure.persistence.entity.Endereco
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class EnderecoTest {

    @Test
    fun `deve criar um endereco com todos os campos preenchidos`() {
        val endereco = Endereco().apply {
            id = 1L
            logradouro = "Rua das Flores"
            numero = "123"
            complemento = "Apto 1"
            bairro = "Centro"
            cidade = "São Paulo"
            estado = "SP"
            cep = "01001-000"
        }

        assertEquals(1L, endereco.id)
        assertEquals("Rua das Flores", endereco.logradouro)
        assertEquals("123", endereco.numero)
        assertEquals("Apto 1", endereco.complemento)
        assertEquals("Centro", endereco.bairro)
        assertEquals("São Paulo", endereco.cidade)
        assertEquals("SP", endereco.estado)
        assertEquals("01001-000", endereco.cep)
    }

    @Test
    fun `deve permitir complemento nulo`() {
        val endereco = Endereco().apply {
            logradouro = "Rua das Flores"
            numero = "123"
            complemento = null
            bairro = "Centro"
            cidade = "São Paulo"
            estado = "SP"
            cep = "01001-000"
        }

        assertNull(endereco.complemento)
    }

    @Test
    fun `deve associar um cliente ao endereco`() {
        val cliente = Cliente()
        val endereco = Endereco().apply {
            this.cliente = cliente
        }

        assertEquals(cliente, endereco.cliente)
    }
}
