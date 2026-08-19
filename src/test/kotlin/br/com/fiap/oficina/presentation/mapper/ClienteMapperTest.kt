package br.com.fiap.oficina.presentation.mapper

import br.com.fiap.oficina.domain.entity.Cliente
import br.com.fiap.oficina.domain.entity.Contato
import br.com.fiap.oficina.domain.entity.Endereco
import br.com.fiap.oficina.domain.valueobject.Documento
import br.com.fiap.oficina.domain.valueobject.TipoPessoa
import br.com.fiap.oficina.presentation.dto.ClienteDto
import br.com.fiap.oficina.presentation.dto.ContatoDto
import br.com.fiap.oficina.presentation.dto.EnderecoDto
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class ClienteMapperTest {
    private val mapper = ClienteMapper()

    @Test
    fun `deve mapear Cliente para ClienteResponse`() {
        val cliente =
            Cliente.criar(
                nome = "João Silva",
                documento = Documento.cpf("39053344705"),
                email = "joao.silva@example.com",
                endereco =
                Endereco.criar(
                    logradouro = "Rua A",
                    numero = "100",
                    bairro = "Centro",
                    cidade = "São Paulo",
                    estado = "SP",
                    cep = "01000-000",
                ),
                contatos =
                listOf(
                    Contato.criar(tipo = "Pessoal", nome = "Contato 1", telefone = "123456789"),
                ),
            )

        val response = mapper.toResponse(cliente)

        assertEquals(cliente.nome, response.nome)
        assertEquals(cliente.documento.numero, response.numeroDocumento)
        assertNotNull(response.endereco)
        assertEquals(cliente.endereco?.logradouro, response.endereco?.logradouro)
        assertEquals(1, response.contatos.size)
        assertEquals(cliente.contatos[0].nome, response.contatos[0].nome)
    }

    @Test
    fun `deve mapear EnderecoDto para Endereco entity`() {
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

        val entity = mapper.toEnderecoEntity(dto)

        assertNotNull(entity)
        assertEquals(dto.logradouro, entity.logradouro)
        assertEquals(dto.numero, entity.numero)
        assertEquals(dto.complemento, entity.complemento)
    }

    @Test
    fun `deve mapear ClienteDto para Cliente entity`() {
        val dto =
            ClienteDto(
                nome = "João Silva",
                numeroDocumento = "39053344705",
                tipoPessoa = "PESSOA_FISICA",
                email = "joao.silva@example.com",
                endereco = EnderecoDto("Rua A", "100", null, "Centro", "São Paulo", "SP", "01000-000"),
                contatos = listOf(ContatoDto("Pessoal", "Contato 1", "123456789")),
            )

        val cliente = mapper.toEntity(dto)

        assertEquals("João Silva", cliente.nome)
        assertEquals("39053344705", cliente.documento.numero)
        assertEquals(TipoPessoa.PESSOA_FISICA, cliente.documento.tipoPessoa)
        assertNotNull(cliente.endereco)
        assertEquals("Rua A", cliente.endereco?.logradouro)
        assertEquals(1, cliente.contatos.size)
        assertEquals("Contato 1", cliente.contatos[0].nome)
    }

    @Test
    fun `deve mapear ContatoDto para Contato entity`() {
        val dto =
            ContatoDto(
                tipo = "Trabalho",
                nome = "Chefe",
                telefone = "987654321",
            )

        val entity = mapper.toContatoEntity(dto)

        assertNotNull(entity)
        assertEquals(dto.tipo, entity.tipo)
        assertEquals(dto.nome, entity.nome)
        assertEquals(dto.telefone, entity.telefone)
    }
}
