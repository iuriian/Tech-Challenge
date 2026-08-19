package br.com.fiap.oficina.presentation.mapper

import br.com.fiap.oficina.domain.entity.Cliente
import br.com.fiap.oficina.domain.entity.Contato
import br.com.fiap.oficina.domain.entity.Endereco
import br.com.fiap.oficina.domain.valueobject.Documento
import br.com.fiap.oficina.domain.valueobject.TipoPessoa
import br.com.fiap.oficina.presentation.dto.ClienteDto
import br.com.fiap.oficina.presentation.dto.ContatoDto
import br.com.fiap.oficina.presentation.dto.EnderecoDto
import org.springframework.stereotype.Component

@Component
class ClienteMapper {
    fun toResponse(cliente: Cliente): ClienteDto =
        ClienteDto(
            id = cliente.id.valor,
            nome = cliente.nome,
            numeroDocumento = cliente.documento.numero,
            tipoPessoa = cliente.documento.tipoPessoa.name,
            email = cliente.email,
            endereco = cliente.endereco?.let(::toEnderecoDto),
            contatos = cliente.contatos.map(::toContatoDto),
        )

    fun toEntity(dto: ClienteDto): Cliente =
        Cliente.criar(
            nome = dto.nome,
            documento = Documento(dto.numeroDocumento, TipoPessoa.valueOf(dto.tipoPessoa)),
            email = dto.email,
            endereco = dto.endereco?.let(::toEnderecoEntity),
            contatos = dto.contatos.map(::toContatoEntity),
        )

    fun toEnderecoDto(endereco: Endereco): EnderecoDto =
        EnderecoDto(
            logradouro = endereco.logradouro,
            numero = endereco.numero,
            complemento = endereco.complemento,
            bairro = endereco.bairro,
            cidade = endereco.cidade,
            estado = endereco.estado,
            cep = endereco.cep,
        )

    fun toContatoDto(contato: Contato): ContatoDto =
        ContatoDto(
            tipo = contato.tipo,
            nome = contato.nome,
            telefone = contato.telefone,
        )

    fun toEnderecoEntity(dto: EnderecoDto): Endereco =
        Endereco.criar(
            logradouro = dto.logradouro,
            numero = dto.numero,
            complemento = dto.complemento,
            bairro = dto.bairro,
            cidade = dto.cidade,
            estado = dto.estado,
            cep = dto.cep,
        )

    fun toContatoEntity(dto: ContatoDto): Contato =
        Contato.criar(
            tipo = dto.tipo,
            nome = dto.nome,
            telefone = dto.telefone,
        )
}
