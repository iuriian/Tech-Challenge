package br.com.fiap.oficina.presentation.mapper

import br.com.fiap.oficina.application.dto.ClienteResponse
import br.com.fiap.oficina.application.dto.ContatoResponse
import br.com.fiap.oficina.application.dto.EnderecoResponse
import br.com.fiap.oficina.presentation.dto.ClienteDto
import br.com.fiap.oficina.presentation.dto.ContatoDto
import br.com.fiap.oficina.presentation.dto.EnderecoDto
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import java.util.UUID

class ClienteMapperTest {
    private val mapper = ClienteMapper()

    @Test
    fun `deve mapear ClienteResponse para ClienteDto`() {
        val response =
            ClienteResponse(
                id = UUID.randomUUID().toString(),
                nome = "João Silva",
                numeroDocumento = "39053344705",
                tipoPessoa = "PESSOA_FISICA",
                email = "joao.silva@example.com",
                endereco =
                EnderecoResponse(
                    logradouro = "Rua A",
                    numero = "100",
                    complemento = null,
                    bairro = "Centro",
                    cidade = "São Paulo",
                    estado = "SP",
                    cep = "01000-000",
                ),
                contatos = listOf(ContatoResponse("Pessoal", "Contato 1", "123456789")),
            )

        val dto = mapper.toDto(response)

        assertEquals(response.nome, dto.nome)
        assertEquals(response.numeroDocumento, dto.numeroDocumento)
        assertNotNull(dto.endereco)
        assertEquals(1, dto.contatos.size)
    }

    @Test
    fun `deve mapear EnderecoDto para EnderecoRequest`() {
        val dto =
            EnderecoDto(
                logradouro = "Rua B",
                numero = "200",
                complemento = "Sala 1",
                bairro = "Jardins",
                cidade = "São Paulo",
                estado = "SP",
                cep = "02000-000",
            )

        val request = mapper.toEnderecoRequest(dto)

        assertEquals(dto.logradouro, request.logradouro)
        assertEquals(dto.numero, request.numero)
    }

    @Test
    fun `deve mapear ClienteDto para CriarClienteRequest`() {
        val dto =
            ClienteDto(
                nome = "João Silva",
                numeroDocumento = "39053344705",
                tipoPessoa = "PESSOA_FISICA",
                email = "joao.silva@example.com",
                endereco = EnderecoDto("Rua A", "100", null, "Centro", "São Paulo", "SP", "01000-000"),
                contatos = listOf(ContatoDto("Pessoal", "Contato 1", "123456789")),
            )

        val request = mapper.toCriarRequest(dto)

        assertEquals("João Silva", request.nome)
        assertEquals("39053344705", request.numeroDocumento)
        assertNotNull(request.endereco)
        assertEquals(1, request.contatos.size)
    }

    @Test
    fun `deve mapear ContatoDto para ContatoRequest`() {
        val dto = ContatoDto(tipo = "Trabalho", nome = "Chefe", telefone = "987654321")

        val request = mapper.toContatoRequest(dto)

        assertEquals(dto.tipo, request.tipo)
        assertEquals(dto.nome, request.nome)
        assertEquals(dto.telefone, request.telefone)
    }
}
