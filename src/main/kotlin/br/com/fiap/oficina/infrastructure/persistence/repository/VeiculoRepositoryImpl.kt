package br.com.fiap.oficina.infrastructure.persistence.repository

import br.com.fiap.oficina.domain.entity.Veiculo
import br.com.fiap.oficina.domain.repository.VeiculoRepository
import br.com.fiap.oficina.domain.valueobject.Id
import br.com.fiap.oficina.infrastructure.persistence.jpa.repository.VeiculoJpaRepository
import br.com.fiap.oficina.infrastructure.persistence.mapper.VeiculoPersistenceMapper
import org.springframework.stereotype.Component

@Component
class VeiculoRepositoryImpl(
    private val jpaRepository: VeiculoJpaRepository,
    private val mapper: VeiculoPersistenceMapper,
) : VeiculoRepository {
    override fun salvar(veiculo: Veiculo): Veiculo = mapper.toDomain(jpaRepository.save(mapper.toJpa(veiculo)))

    override fun buscarPorId(id: Id): Veiculo? = jpaRepository.findByIdVeiculo(id.valor)?.let(mapper::toDomain)

    override fun buscarPorPlaca(placa: String): Veiculo? = jpaRepository.findByPlaca(placa)?.let(mapper::toDomain)

    override fun buscarPorMotorista(motoristaId: Id): List<Veiculo> =
        jpaRepository.findByMotoristaId(motoristaId.valor).map(mapper::toDomain)

    override fun listarTodos(): List<Veiculo> = jpaRepository.findAll().map(mapper::toDomain)

    override fun existePorPlaca(placa: String): Boolean = jpaRepository.existsByPlaca(placa)

    override fun remover(id: Id) = jpaRepository.deleteById(id.valor)
}
