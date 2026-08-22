package br.com.fiap.oficina.domain.entity

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class EnderecoTest {

    @Test
    fun `deve criar um endereco com todos os campos preenchidos`() {
        val endereco = Endereco.criar(
            logradouro = "Rua das Flores",
            numero = "123",
            complemento = "Apto 1",
            bairro = "Centro",
            cidade = "São Paulo",
            estado = "SP",
            cep = "01001-000"
        )

        assertNotNull(endereco.id)
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
        val endereco = Endereco.criar(
            logradouro = "Rua das Flores",
            numero = "123",
            complemento = null,
            bairro = "Centro",
            cidade = "São Paulo",
            estado = "SP",
            cep = "01001-000"
        )

        assertNull(endereco.complemento)
    }

    @Test
    fun `deve rejeitar logradouro vazio`() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            Endereco.criar(
                logradouro = "",
                numero = "123",
                bairro = "Centro",
                cidade = "São Paulo",
                estado = "SP",
                cep = "01001-000"
            )
        }

        assertEquals("Logradouro é obrigatório", exception.message)
    }

    @Test
    fun `deve rejeitar demais campos obrigatorios em branco`() {
        val base = mapOf(
            "numero" to "123",
            "bairro" to "Centro",
            "cidade" to "São Paulo",
            "estado" to "SP",
            "cep" to "01001-000"
        )

        fun criar(campoVazio: String) = Endereco.criar(
            logradouro = "Rua A",
            numero = if (campoVazio == "numero") "" else base["numero"]!!,
            bairro = if (campoVazio == "bairro") "" else base["bairro"]!!,
            cidade = if (campoVazio == "cidade") "" else base["cidade"]!!,
            estado = if (campoVazio == "estado") "" else base["estado"]!!,
            cep = if (campoVazio == "cep") "" else base["cep"]!!
        )

        assertEquals("Número é obrigatório", assertThrows(IllegalArgumentException::class.java) { criar("numero") }.message)
        assertEquals("Bairro é obrigatório", assertThrows(IllegalArgumentException::class.java) { criar("bairro") }.message)
        assertEquals("Cidade é obrigatória", assertThrows(IllegalArgumentException::class.java) { criar("cidade") }.message)
        assertEquals("Estado é obrigatório", assertThrows(IllegalArgumentException::class.java) { criar("estado") }.message)
        assertEquals("CEP é obrigatório", assertThrows(IllegalArgumentException::class.java) { criar("cep") }.message)
    }
}
