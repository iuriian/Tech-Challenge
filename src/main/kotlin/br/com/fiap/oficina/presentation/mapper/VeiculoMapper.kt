package br.com.fiap.oficina.presentation.mapper

import br.com.fiap.oficina.domain.entity.Cliente
import br.com.fiap.oficina.domain.entity.Veiculo
import br.com.fiap.oficina.domain.valueobject.Documento
import br.com.fiap.oficina.domain.valueobject.Id
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

    fun toEntity(dto: VeiculoDTO): Veiculo =
        Veiculo.criar(
            marca = dto.marca,
            nome = dto.nome,
            modelo = dto.modelo,
            ano = dto.ano,
            placa = dto.placa,
            motorista = motoristaReferencia(dto.motoristaId),
        )

    fun toEntityComId(
        id: String,
        dto: VeiculoDTO,
    ): Veiculo =
        Veiculo(
            id = Id.fromString(id),
            marca = dto.marca,
            nome = dto.nome,
            modelo = dto.modelo,
            ano = dto.ano,
            placa = dto.placa,
            motorista = motoristaReferencia(dto.motoristaId),
        )

    private fun motoristaReferencia(motoristaId: String): Cliente =
        Cliente(
            id = Id.fromString(motoristaId),
            nome = "-",
            documento = Documento.cpf("39053344705"),
            email = "referencia@local",
        )
}
