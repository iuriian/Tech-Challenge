package br.com.fiap.oficina.infrastructure.persistence.adapter

import br.com.fiap.oficina.domain.entity.Cliente
import br.com.fiap.oficina.domain.entity.Veiculo
import br.com.fiap.oficina.domain.repository.VeiculoRepository
import br.com.fiap.oficina.domain.valueobject.Id
import br.com.fiap.oficina.infrastructure.persistence.mapper.ClientePersistenceMapper
import br.com.fiap.oficina.infrastructure.persistence.mapper.VeiculoPersistenceMapper
import br.com.fiap.oficina.infrastructure.persistence.repository.VeiculoJpaRepository
import org.springframework.stereotype.Component

@Component
class VeiculoRepositoryAdapter(
    private val jpaRepository: VeiculoJpaRepository,
    private val mapper: VeiculoPersistenceMapper,
    private val clienteMapper: ClientePersistenceMapper
) : VeiculoRepository {

    override fun salvar(veiculo: Veiculo): Veiculo =
        mapper.toDomain(jpaRepository.save(mapper.toJpa(veiculo)))

    override fun buscarPorId(id: Id): Veiculo? =
        jpaRepository.findByIdVeiculo(id.valor)?.let(mapper::toDomain)

    override fun buscarPorPlaca(placa: String): Veiculo? =
        jpaRepository.findByPlaca(placa)?.let(mapper::toDomain)

    override fun buscarPorMotorista(motorista: Cliente): List<Veiculo> =
        jpaRepository.findByMotorista(clienteMapper.toJpa(motorista)).map(mapper::toDomain)

    override fun existePorPlaca(placa: String): Boolean =
        jpaRepository.existsByPlaca(placa)
}
