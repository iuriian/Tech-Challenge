package br.com.fiap.oficina.infrastructure.persistence.mapper

import br.com.fiap.oficina.domain.entity.Veiculo
import br.com.fiap.oficina.domain.valueobject.Id
import br.com.fiap.oficina.infrastructure.persistence.entity.VeiculoJpaEntity
import org.springframework.stereotype.Component

@Component
class VeiculoPersistenceMapper(
    private val clienteMapper: ClientePersistenceMapper
) {

    fun toDomain(entity: VeiculoJpaEntity): Veiculo =
        Veiculo(
            id = Id.from(entity.idVeiculo),
            marca = entity.marca,
            nome = entity.nome,
            modelo = entity.modelo,
            ano = entity.ano,
            placa = entity.placa,
            motorista = clienteMapper.toDomain(entity.motorista)
        )

    fun toJpa(domain: Veiculo): VeiculoJpaEntity =
        VeiculoJpaEntity().apply {
            idVeiculo = domain.id.valor
            marca = domain.marca
            nome = domain.nome
            modelo = domain.modelo
            ano = domain.ano
            placa = domain.placa
            motorista = clienteMapper.toJpa(domain.motorista)
        }
}
