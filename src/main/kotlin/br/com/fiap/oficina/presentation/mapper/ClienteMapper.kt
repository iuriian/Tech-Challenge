package br.com.fiap.oficina.presentation.mapper

import br.com.fiap.oficina.domain.entity.Cliente
import br.com.fiap.oficina.presentation.dto.ClienteDto
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers

@Mapper(componentModel = "spring")
interface ClienteMapper {
    companion object {
        val INSTANCE: ClienteMapper = Mappers.getMapper(ClienteMapper::class.java)
    }

    fun toResponse(cliente: Cliente): ClienteDto

    fun toEntity(cliente: ClienteDto): Cliente
}
