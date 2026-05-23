package br.com.fiap.oficina.presentation.mapper

import br.com.fiap.oficina.domain.entity.*
import br.com.fiap.oficina.presentation.dto.ClienteDto
import br.com.fiap.oficina.presentation.dto.ContatoDto
import br.com.fiap.oficina.presentation.dto.EnderecoDto
import org.mapstruct.AfterMapping
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingTarget
import org.mapstruct.factory.Mappers

@Mapper(componentModel = "spring")
abstract class ClienteMapper {
    companion object {
        val INSTANCE: ClienteMapper = Mappers.getMapper(ClienteMapper::class.java)
    }

    @Mapping(source = "documento.numero", target = "numeroDocumento")
    @Mapping(source = "documento.tipoPessoa", target = "tipoPessoa")
    abstract fun toResponse(cliente: Cliente): ClienteDto

    abstract fun toEnderecoDto(endereco: Endereco?): EnderecoDto?

    abstract fun toContatoDto(contato: Contato): ContatoDto

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "documento", ignore = true)
    @Mapping(target = "contatos", ignore = true)
    abstract fun toEntity(dto: ClienteDto): Cliente

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cliente", ignore = true)
    abstract fun toContatoEntity(dto: ContatoDto): Contato

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cliente", ignore = true)
    abstract fun toEnderecoEntity(dto: EnderecoDto?): Endereco?

    @AfterMapping
    fun setDocumento(dto: ClienteDto, @MappingTarget cliente: Cliente) {
        cliente.documento = Documento(dto.numeroDocumento, TipoPessoa.valueOf(dto.tipoPessoa))
        cliente.endereco?.cliente = cliente
        cliente.contatos.addAll(dto.contatos.map {
            val contato = toContatoEntity(it)
            contato.cliente = cliente
            contato
        })
    }
}
