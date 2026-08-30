package br.com.fiap.oficina.application.mapper

import br.com.fiap.oficina.application.dto.AtualizarClienteRequest
import br.com.fiap.oficina.application.dto.ClienteResponse
import br.com.fiap.oficina.application.dto.ContatoRequest
import br.com.fiap.oficina.application.dto.ContatoResponse
import br.com.fiap.oficina.application.dto.CriarClienteRequest
import br.com.fiap.oficina.application.dto.EnderecoRequest
import br.com.fiap.oficina.application.dto.EnderecoResponse
import br.com.fiap.oficina.domain.entity.Cliente
import br.com.fiap.oficina.domain.entity.Contato
import br.com.fiap.oficina.domain.entity.Endereco
import br.com.fiap.oficina.domain.valueobject.Documento
import br.com.fiap.oficina.domain.valueobject.TipoPessoa
import org.springframework.stereotype.Component

@Component
class ClienteApplicationMapper {
    fun toDomain(request: CriarClienteRequest): Cliente = Cliente.criar(
        nome = request.nome,
        documento = Documento(request.numeroDocumento, TipoPessoa.valueOf(request.tipoPessoa)),
        email = request.email,
        endereco = request.endereco?.let(::toEnderecoDomain),
        contatos = request.contatos.map(::toContatoDomain),
    )

    fun toDomain(id: String, request: AtualizarClienteRequest): Cliente = Cliente.reconstruir(
        id = id,
        nome = request.nome,
        documento = Documento(request.numeroDocumento, TipoPessoa.valueOf(request.tipoPessoa)),
        email = request.email,
        endereco = request.endereco?.let(::toEnderecoDomain),
        contatos = request.contatos.map(::toContatoDomain),
    )

    fun toResponse(cliente: Cliente): ClienteResponse = ClienteResponse(
        id = cliente.id.valor.toString(),
        nome = cliente.nome,
        numeroDocumento = cliente.documento.numero,
        tipoPessoa = cliente.documento.tipoPessoa.name,
        email = cliente.email,
        endereco = cliente.endereco?.let(::toEnderecoResponse),
        contatos = cliente.contatos.map(::toContatoResponse),
    )

    private fun toEnderecoDomain(request: EnderecoRequest): Endereco = Endereco.criar(
        logradouro = request.logradouro,
        numero = request.numero,
        complemento = request.complemento,
        bairro = request.bairro,
        cidade = request.cidade,
        estado = request.estado,
        cep = request.cep,
    )

    private fun toContatoDomain(request: ContatoRequest): Contato = Contato.criar(
        tipo = request.tipo,
        nome = request.nome,
        telefone = request.telefone,
    )

    private fun toEnderecoResponse(endereco: Endereco): EnderecoResponse = EnderecoResponse(
        logradouro = endereco.logradouro,
        numero = endereco.numero,
        complemento = endereco.complemento,
        bairro = endereco.bairro,
        cidade = endereco.cidade,
        estado = endereco.estado,
        cep = endereco.cep,
    )

    private fun toContatoResponse(contato: Contato): ContatoResponse = ContatoResponse(
        tipo = contato.tipo,
        nome = contato.nome,
        telefone = contato.telefone,
    )
}
