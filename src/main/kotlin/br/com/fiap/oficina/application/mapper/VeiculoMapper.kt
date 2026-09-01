package br.com.fiap.oficina.application.mapper

import br.com.fiap.oficina.application.dto.VeiculoRequest
import br.com.fiap.oficina.application.dto.VeiculoResponse
import br.com.fiap.oficina.domain.entity.Cliente
import br.com.fiap.oficina.domain.entity.Veiculo
import br.com.fiap.oficina.domain.valueobject.Documento
import br.com.fiap.oficina.domain.valueobject.Id
import org.springframework.stereotype.Component

@Component
class VeiculoMapper {
    fun toDomain(request: VeiculoRequest): Veiculo = if (request.id == null) {
        Veiculo.criar(
            marca = request.marca,
            nome = request.nome,
            modelo = request.modelo,
            ano = request.ano,
            placa = request.placa,
            motorista = motoristaReferencia(request.motoristaId),
        )
    } else {
        Veiculo.reconstruir(
            id = request.id,
            marca = request.marca,
            nome = request.nome,
            modelo = request.modelo,
            ano = request.ano,
            placa = request.placa,
            motorista = motoristaReferencia(request.motoristaId),
        )
    }

    fun toResponse(veiculo: Veiculo): VeiculoResponse = VeiculoResponse(
        id = veiculo.id.valor.toString(),
        nome = veiculo.nome,
        marca = veiculo.marca,
        modelo = veiculo.modelo,
        ano = veiculo.ano,
        placa = veiculo.placa,
        motoristaId = veiculo.motorista.id.valor.toString(),
    )

    private fun motoristaReferencia(motoristaId: String): Cliente = Cliente(
        id = Id.fromString(motoristaId),
        nome = "-",
        documento = Documento.cpf("39053344705"),
        email = "referencia@local",
    )
}
