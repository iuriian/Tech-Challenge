package br.com.fiap.oficina.presentation.mapper

import br.com.fiap.oficina.domain.entity.Veiculo
import br.com.fiap.oficina.presentation.dto.VeiculoDTO
import org.springframework.stereotype.Component

@Component
class VeiculoMapper {
    fun toResponse(veiculo: Veiculo): VeiculoDTO =
        VeiculoDTO(
            id = veiculo.id.valor,
            nome = veiculo.nome,
            marca = veiculo.marca,
            modelo = veiculo.modelo,
            ano = veiculo.ano,
            placa = veiculo.placa,
            motoristaId =
                veiculo.motorista.id.valor
                    .toString(),
        )
}
