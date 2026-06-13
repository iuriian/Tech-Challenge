package br.com.fiap.oficina.presentation.mapper

import br.com.fiap.oficina.domain.entity.Veiculo
import br.com.fiap.oficina.presentation.dto.VeiculoDTO
import org.springframework.stereotype.Component

@Component
class VeiculoMapper {

    fun toEntity(dto: VeiculoDTO): Veiculo {
        val motorista = requireNotNull(dto.motorista) { "Motorista é obrigatório" }
        return Veiculo.criar(
            marca = dto.marca,
            nome = dto.nome,
            modelo = dto.modelo,
            ano = dto.ano,
            placa = dto.placa,
            motorista = motorista
        )
    }

    fun toResponse(veiculo: Veiculo): VeiculoDTO =
        VeiculoDTO(
            nome = veiculo.nome,
            marca = veiculo.marca,
            modelo = veiculo.modelo,
            ano = veiculo.ano,
            placa = veiculo.placa,
            motorista = veiculo.motorista
        )
}
