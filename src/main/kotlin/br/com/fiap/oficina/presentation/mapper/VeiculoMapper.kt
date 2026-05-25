package br.com.fiap.oficina.presentation.mapper

import org.mapstruct.AfterMapping
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingTarget
import org.mapstruct.factory.Mappers

import br.com.fiap.oficina.domain.entity.Veiculo
import br.com.fiap.oficina.domain.entity.Cliente
import br.com.fiap.oficina.presentation.dto.VeiculoDTO

@Mapper(componentModel = "spring")
interface VeiculoMapper {
    companion object{
        val INSTANCE : VeiculoMapper = Mappers.getMapper(VeiculoMapper::class.java)
    }

    // Validar se está correto
    fun toEntity(dto: VeiculoDTO): Veiculo

    fun toResponse(veiculo: Veiculo): VeiculoDTO

}