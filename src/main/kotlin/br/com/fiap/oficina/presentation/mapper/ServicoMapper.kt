package br.com.fiap.oficina.presentation.mapper

import br.com.fiap.oficina.domain.entity.Servico
import br.com.fiap.oficina.presentation.dto.ServicoDto
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.factory.Mappers

@Mapper(componentModel = "spring")
abstract class ServicoMapper {
    companion object {
        val INSTANCE: ServicoMapper = Mappers.getMapper(ServicoMapper::class.java)
    }

    @Mapping(source = "cliente.id", target = "cliente")
    abstract fun toResponse(servico: Servico): ServicoDto

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cliente", ignore = true)
    abstract fun toEntity(dto: ServicoDto): Servico

}