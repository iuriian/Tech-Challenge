package br.com.fiap.oficina.domain.entity

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ClienteTest {

    @Test
    fun `deve criar um cliente com todos os campos preenchidos`() {
        val documento = Documento.cpf("123.456.789-00")
        val cliente = Cliente().apply {
            id = 1L
            nome = "João da Silva"
            email = "joao.silva@example.com"
            this.documento = documento
        }

        assertEquals(1L, cliente.id)
        assertEquals("João da Silva", cliente.nome)
        assertEquals("joao.silva@example.com", cliente.email)
        assertEquals(documento, cliente.documento)
    }

    @Test
    fun `deve associar endereco ao cliente`() {
        val cliente = Cliente()
        val endereco = Endereco().apply {
            logradouro = "Rua Teste"
            numero = "123"
        }
        
        cliente.endereco = endereco

        assertEquals(endereco, cliente.endereco)
    }

    @Test
    fun `deve associar lista de contatos ao cliente`() {
        val cliente = Cliente()
        val contato1 = Contato().apply { nome = "Contato 1"; tipo = "Celular"; telefone = "11999999999" }
        val contato2 = Contato().apply { nome = "Contato 2"; tipo = "Fixo"; telefone = "1133333333" }
        
        val contatos = mutableListOf(contato1, contato2)
        cliente.contatos = contatos

        assertEquals(2, cliente.contatos.size)
        assertTrue(cliente.contatos.contains(contato1))
        assertTrue(cliente.contatos.contains(contato2))
    }
}
