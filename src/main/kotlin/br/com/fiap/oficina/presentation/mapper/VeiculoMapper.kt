package br.com.fiap.oficina.presentation.mapper

import br.com.fiap.oficina.application.dto.AtualizarVeiculoRequest
import br.com.fiap.oficina.application.dto.CriarVeiculoRequest
import br.com.fiap.oficina.application.dto.VeiculoResponse
import br.com.fiap.oficina.presentation.dto.VeiculoDTO
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class VeiculoMapper {
    fun toCriarRequest(dto: VeiculoDTO): CriarVeiculoRequest = CriarVeiculoRequest(
        nome = dto.nome,
        marca = dto.marca,
        modelo = dto.modelo,
        ano = dto.ano,
        placa = dto.placa,
        motoristaId = dto.motoristaId,
    )

    fun toAtualizarRequest(dto: VeiculoDTO): AtualizarVeiculoRequest = AtualizarVeiculoRequest(
        nome = dto.nome,
        marca = dto.marca,
        modelo = dto.modelo,
        ano = dto.ano,
        placa = dto.placa,
        motoristaId = dto.motoristaId,
    )

    fun toDto(response: VeiculoResponse): VeiculoDTO = VeiculoDTO(
        id = UUID.fromString(response.id),
        nome = response.nome,
        marca = response.marca,
        modelo = response.modelo,
        ano = response.ano,
        placa = response.placa,
        motoristaId = response.motoristaId,
    )
}
