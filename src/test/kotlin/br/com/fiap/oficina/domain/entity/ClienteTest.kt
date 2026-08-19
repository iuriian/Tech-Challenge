package br.com.fiap.oficina.domain.entity

import br.com.fiap.oficina.domain.valueobject.Documento
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ClienteTest {
    @Test
    fun `deve criar um cliente com todos os campos preenchidos`() {
        val documento = Documento.cpf("39053344705")
        val cliente =
            Cliente.criar(
                nome = "João da Silva",
                documento = documento,
                email = "joao.silva@example.com",
            )

        assertNotNull(cliente.id)
        assertEquals("João da Silva", cliente.nome)
        assertEquals("joao.silva@example.com", cliente.email)
        assertEquals(documento, cliente.documento)
    }

    @Test
    fun `deve associar endereco ao cliente`() {
        val endereco =
            Endereco.criar(
                logradouro = "Rua Teste",
                numero = "123",
                bairro = "Centro",
                cidade = "São Paulo",
                estado = "SP",
                cep = "01001000",
            )

        val cliente =
            Cliente.criar(
                nome = "João da Silva",
                documento = Documento.cpf("39053344705"),
                email = "joao.silva@example.com",
                endereco = endereco,
            )

        assertEquals(endereco, cliente.endereco)
    }

    @Test
    fun `deve associar lista de contatos ao cliente`() {
        val contato1 = Contato.criar(tipo = "Celular", nome = "Contato 1", telefone = "11999999999")
        val contato2 = Contato.criar(tipo = "Fixo", nome = "Contato 2", telefone = "1133333333")

        val cliente =
            Cliente.criar(
                nome = "João da Silva",
                documento = Documento.cpf("39053344705"),
                email = "joao.silva@example.com",
                contatos = listOf(contato1, contato2),
            )

        assertEquals(2, cliente.contatos.size)
        assertTrue(cliente.contatos.contains(contato1))
        assertTrue(cliente.contatos.contains(contato2))
    }

    @Test
    fun `deve rejeitar documento invalido na criacao`() {
        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                Cliente.criar(
                    nome = "João da Silva",
                    documento = Documento.cpf("00000000000"),
                    email = "joao.silva@example.com",
                )
            }

        assertEquals("Documento inválido", exception.message)
    }

    @Test
    fun `deve rejeitar nome em branco`() {
        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                Cliente.criar(
                    nome = "",
                    documento = Documento.cpf("39053344705"),
                    email = "joao.silva@example.com",
                )
            }

        assertEquals("Nome do cliente é obrigatório", exception.message)
    }

    @Test
    fun `deve rejeitar email em branco`() {
        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                Cliente.criar(
                    nome = "João da Silva",
                    documento = Documento.cpf("39053344705"),
                    email = "",
                )
            }

        assertEquals("E-mail do cliente é obrigatório", exception.message)
    }
}
