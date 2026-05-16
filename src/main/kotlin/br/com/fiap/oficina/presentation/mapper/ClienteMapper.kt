package br.com.fiap.oficina.presentation.mapper

import br.com.fiap.oficina.domain.entity.Cliente
import br.com.fiap.oficina.presentation.dto.ClienteResponse
import org.mapstruct.Mapper
import org.mapstruct.factory.Mappers

@Mapper
interface ClienteMapper {
    companion object {
        val INSTANCE: ClienteMapper = Mappers.getMapper(ClienteMapper::class.java)
    }

    fun toResponse(cliente: Cliente): ClienteResponse
}
