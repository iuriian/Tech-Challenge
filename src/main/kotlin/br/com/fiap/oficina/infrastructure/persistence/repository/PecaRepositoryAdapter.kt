package br.com.fiap.oficina.infrastructure.persistence.repository

import br.com.fiap.oficina.domain.entity.Peca
import br.com.fiap.oficina.domain.repository.PecaRepository
import br.com.fiap.oficina.domain.valueobject.Id
import br.com.fiap.oficina.infrastructure.persistence.mapper.PecaPersistenceMapper
import br.com.fiap.oficina.infrastructure.persistence.jpa.PecaJpaRepository
import org.springframework.stereotype.Component

@Component
class PecaRepositoryAdapter(private val jpaRepository: PecaJpaRepository, private val mapper: PecaPersistenceMapper) :
    PecaRepository {
    override fun salvar(peca: Peca): Peca = mapper.toDomain(jpaRepository.save(mapper.toJpa(peca)))

    override fun listarAtivos(): List<Peca> = jpaRepository.findAllByAtivoTrue().map(mapper::toDomain)

    override fun buscarAtivoPorCodigo(codigo: String): Peca? =
        jpaRepository.findByCodigoAndAtivoTrue(codigo)?.let(mapper::toDomain)

    override fun buscarAtivoPorNome(nome: String): Peca? =
        jpaRepository.findByNomeIgnoreCaseAndAtivoTrue(nome)?.let(mapper::toDomain)

    override fun existeAtivoPorCodigo(codigo: String): Boolean = jpaRepository.existsByCodigoAndAtivoTrue(codigo)

    override fun buscarPorCodigo(codigo: String): Peca? = jpaRepository.findByCodigo(codigo)?.let(mapper::toDomain)

    override fun existePorCodigo(codigo: String): Boolean = jpaRepository.existsByCodigo(codigo)

    override fun buscarPorId(id: Id): Peca? = jpaRepository.findById(id.valor).map(mapper::toDomain).orElse(null)
}
