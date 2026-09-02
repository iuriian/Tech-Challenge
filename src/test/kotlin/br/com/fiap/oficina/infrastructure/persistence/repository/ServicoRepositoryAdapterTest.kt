package br.com.fiap.oficina.infrastructure.persistence.repository

import br.com.fiap.oficina.anyObject
import br.com.fiap.oficina.domain.entity.Servico
import br.com.fiap.oficina.domain.valueobject.Id
import br.com.fiap.oficina.infrastructure.persistence.jpa.entity.ServicoJpaEntity
import br.com.fiap.oficina.infrastructure.persistence.jpa.repository.ServicoJpaRepository
import br.com.fiap.oficina.infrastructure.persistence.mapper.ServicoPersistenceMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.math.BigDecimal
import java.util.Optional

@ExtendWith(MockitoExtension::class)
class ServicoRepositoryAdapterTest {
    @Mock
    private lateinit var jpaRepository: ServicoJpaRepository

    private val mapper = ServicoPersistenceMapper()

    @Test
    fun `deve salvar servico`() {
        val servico =
            Servico.criar(
                descricao = "Troca de óleo",
                valor = BigDecimal("150.00"),
            )

        `when`(jpaRepository.save(anyObject()))
            .thenAnswer { it.getArgument<ServicoJpaEntity>(0) }

        val adapter =
            ServicoRepositoryAdapter(
                jpaRepository = jpaRepository,
                mapper = mapper,
            )

        val resultado = adapter.salvar(servico)

        assertEquals(servico, resultado)

        verify(jpaRepository).save(anyObject())
    }

    @Test
    fun `deve buscar servico por id`() {
        val id = Id.generate()

        val entity =
            ServicoJpaEntity(
                id = id.valor,
                descricao = "Alinhamento",
                valor = BigDecimal("100.00"),
                ativo = true,
            )

        `when`(jpaRepository.findById(id.valor))
            .thenReturn(Optional.of(entity))

        val adapter =
            ServicoRepositoryAdapter(
                jpaRepository = jpaRepository,
                mapper = mapper,
            )

        val resultado = adapter.buscarPorId(id)

        assertEquals(id, resultado?.id)
        assertEquals("Alinhamento", resultado?.descricao)
        assertEquals(BigDecimal("100.00"), resultado?.valor)
        assertTrue(resultado?.ativo == true)

        verify(jpaRepository).findById(id.valor)
    }

    @Test
    fun `deve retornar nulo quando servico nao existir`() {
        val id = Id.generate()

        `when`(jpaRepository.findById(id.valor))
            .thenReturn(Optional.empty())

        val adapter =
            ServicoRepositoryAdapter(
                jpaRepository = jpaRepository,
                mapper = mapper,
            )

        val resultado = adapter.buscarPorId(id)

        assertNull(resultado)

        verify(jpaRepository).findById(id.valor)
    }

    @Test
    fun `deve listar somente servicos ativos`() {
        val entity =
            ServicoJpaEntity(
                descricao = "Balanceamento",
                valor = BigDecimal("80.00"),
                ativo = true,
            )

        `when`(jpaRepository.findAllByAtivoTrue())
            .thenReturn(listOf(entity))

        val adapter =
            ServicoRepositoryAdapter(
                jpaRepository = jpaRepository,
                mapper = mapper,
            )

        val resultado = adapter.listarAtivos()

        assertEquals(1, resultado.size)
        assertEquals("Balanceamento", resultado.first().descricao)
        assertTrue(resultado.first().ativo)

        verify(jpaRepository).findAllByAtivoTrue()
    }

    @Test
    fun `deve listar servicos ativos e inativos`() {
        val ativo =
            ServicoJpaEntity(
                descricao = "Troca de óleo",
                valor = BigDecimal("150.00"),
                ativo = true,
            )

        val inativo =
            ServicoJpaEntity(
                descricao = "Alinhamento",
                valor = BigDecimal("100.00"),
                ativo = false,
            )

        `when`(jpaRepository.findAll())
            .thenReturn(listOf(ativo, inativo))

        val adapter =
            ServicoRepositoryAdapter(
                jpaRepository = jpaRepository,
                mapper = mapper,
            )

        val resultado = adapter.listarTodos()

        assertEquals(2, resultado.size)
        assertTrue(resultado.first().ativo)
        assertFalse(resultado.last().ativo)

        verify(jpaRepository).findAll()
    }
}
