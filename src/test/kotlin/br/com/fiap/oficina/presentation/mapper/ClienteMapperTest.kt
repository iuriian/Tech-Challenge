package br.com.fiap.oficina.presentation.mapper

import br.com.fiap.oficina.infrastructure.persistence.entity.Cliente
import br.com.fiap.oficina.infrastructure.persistence.entity.Contato
import br.com.fiap.oficina.infrastructure.persistence.entity.Documento
import br.com.fiap.oficina.infrastructure.persistence.entity.Endereco
import br.com.fiap.oficina.presentation.dto.ContatoDto
import br.com.fiap.oficina.presentation.dto.EnderecoDto
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test


class ClienteMapperTest {

    private val mapper = ClienteMapper.INSTANCE

    @Test
    fun `deve mapear Cliente para ClienteResponse`() {
        val cliente = Cliente().apply {
            id = 1L
            nome = "João Silva"
            email = "joao.silva@example.com"
            documento = Documento.cpf( "123.456.789-00")
            endereco = Endereco().apply {
                logradouro = "Rua A"
                numero = "100"
                bairro = "Centro"
                cidade = "São Paulo"
                estado = "SP"
                cep = "01000-000"
            }
            contatos = mutableListOf(Contato().apply {
                tipo = "Pessoal"
                nome = "Contato 1"
                telefone = "123456789"
            })
        }

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
        val dto = EnderecoDto(
            logradouro = "Rua B",
            numero = "200",
            complemento = "Sala 1",
            bairro = "Jardins",
            cidade = "São Paulo",
            estado = "SP",
            cep = "02000-000"
        )

        val entity = mapper.toEnderecoEntity(dto)

        assertNotNull(entity)
        assertEquals(dto.logradouro, entity?.logradouro)
        assertEquals(dto.numero, entity?.numero)
        assertEquals(dto.complemento, entity?.complemento)
    }

    @Test
    fun `deve mapear ContatoDto para Contato entity`() {
        val dto = ContatoDto(
            tipo = "Trabalho",
            nome = "Chefe",
            telefone = "987654321"
        )

        val entity = mapper.toContatoEntity(dto)

        assertNotNull(entity)
        assertEquals(dto.tipo, entity.tipo)
        assertEquals(dto.nome, entity.nome)
        assertEquals(dto.telefone, entity.telefone)
    }
}
