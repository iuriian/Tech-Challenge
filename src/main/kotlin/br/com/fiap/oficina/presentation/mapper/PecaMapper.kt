package br.com.fiap.oficina.presentation.mapper

import br.com.fiap.oficina.domain.entity.Peca
import br.com.fiap.oficina.presentation.dto.PecaAtualizacaoDto
import br.com.fiap.oficina.presentation.dto.PecaDto
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper(componentModel = "spring")
interface PecaMapper {

    fun toDto(peca: Peca): PecaDto

    @Mapping(target = "id", ignore = true)
    fun toEntity(dto: PecaDto): Peca

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "codigo", ignore = true)
    @Mapping(target = "qtdEstoque", ignore = true)
    @Mapping(target = "ativo", ignore = true)
    fun toEntity(dto: PecaAtualizacaoDto): Peca

}
