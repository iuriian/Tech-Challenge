package br.com.fiap.oficina.presentation.mapper

import br.com.fiap.oficina.application.dto.AtualizarClienteRequest
import br.com.fiap.oficina.application.dto.ClienteResponse
import br.com.fiap.oficina.application.dto.ContatoRequest
import br.com.fiap.oficina.application.dto.ContatoResponse
import br.com.fiap.oficina.application.dto.CriarClienteRequest
import br.com.fiap.oficina.application.dto.EnderecoRequest
import br.com.fiap.oficina.application.dto.EnderecoResponse
import br.com.fiap.oficina.presentation.dto.ClienteDto
import br.com.fiap.oficina.presentation.dto.ContatoDto
import br.com.fiap.oficina.presentation.dto.EnderecoDto
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class ClienteMapper {
    fun toCriarRequest(dto: ClienteDto): CriarClienteRequest = CriarClienteRequest(
        nome = dto.nome,
        numeroDocumento = dto.numeroDocumento,
        tipoPessoa = dto.tipoPessoa,
        email = dto.email,
        endereco = dto.endereco?.let(::toEnderecoRequest),
        contatos = dto.contatos.map(::toContatoRequest),
    )

    fun toAtualizarRequest(dto: ClienteDto): AtualizarClienteRequest = AtualizarClienteRequest(
        nome = dto.nome,
        numeroDocumento = dto.numeroDocumento,
        tipoPessoa = dto.tipoPessoa,
        email = dto.email,
        endereco = dto.endereco?.let(::toEnderecoRequest),
        contatos = dto.contatos.map(::toContatoRequest),
    )

    fun toDto(response: ClienteResponse): ClienteDto = ClienteDto(
        id = UUID.fromString(response.id),
        nome = response.nome,
        numeroDocumento = response.numeroDocumento,
        tipoPessoa = response.tipoPessoa,
        email = response.email,
        endereco = response.endereco?.let(::toEnderecoDto),
        contatos = response.contatos.map(::toContatoDto),
    )

    fun toEnderecoDto(endereco: EnderecoResponse): EnderecoDto = EnderecoDto(
        logradouro = endereco.logradouro,
        numero = endereco.numero,
        complemento = endereco.complemento,
        bairro = endereco.bairro,
        cidade = endereco.cidade,
        estado = endereco.estado,
        cep = endereco.cep,
    )

    fun toContatoDto(contato: ContatoResponse): ContatoDto = ContatoDto(
        tipo = contato.tipo,
        nome = contato.nome,
        telefone = contato.telefone,
    )

    fun toEnderecoRequest(dto: EnderecoDto): EnderecoRequest = EnderecoRequest(
        logradouro = dto.logradouro,
        numero = dto.numero,
        complemento = dto.complemento,
        bairro = dto.bairro,
        cidade = dto.cidade,
        estado = dto.estado,
        cep = dto.cep,
    )

    fun toContatoRequest(dto: ContatoDto): ContatoRequest = ContatoRequest(
        tipo = dto.tipo,
        nome = dto.nome,
        telefone = dto.telefone,
    )
}
