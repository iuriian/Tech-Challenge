package br.com.fiap.oficina.infrastructure.persistence.repository

import br.com.fiap.oficina.anyObject
import br.com.fiap.oficina.domain.entity.OrdemServico
import br.com.fiap.oficina.domain.enum.OrdemServicoStatus
import br.com.fiap.oficina.domain.valueobject.Id
import br.com.fiap.oficina.infrastructure.persistence.jpa.OrdemServicoJpaRepository
import br.com.fiap.oficina.infrastructure.persistence.jpa.entity.OrdemServicoJpaEntity
import br.com.fiap.oficina.infrastructure.persistence.mapper.OrdemServicoPersistenceMapper
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.time.Instant
import java.util.Optional
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
class OrdemServicoRepositoryAdapterTest {
    @Mock
    lateinit var jpaRepository: OrdemServicoJpaRepository

    private val mapper = OrdemServicoPersistenceMapper()

    private lateinit var adapter: OrdemServicoRepositoryAdapter
    private lateinit var ordemServico: OrdemServico
    private lateinit var jpa: OrdemServicoJpaEntity

    @BeforeEach
    fun setup() {
        adapter =
            OrdemServicoRepositoryAdapter(
                jpaRepository,
                mapper,
            )

        ordemServico =
            OrdemServico(
                id = Id.generate(),
                descricao = "Troca de óleo",
                status = OrdemServicoStatus.RECEBIDA,
                funcionarioId = Id.generate(),
                clienteId = Id.generate(),
                veiculoId = Id.generate(),
            )

        jpa = mapper.toJpa(ordemServico)
    }

    @Test
    fun `salvar deve persistir e mapear de volta`() {
        `when`(
            jpaRepository.save(anyObject()),
        ).thenAnswer {
            it.getArgument<OrdemServicoJpaEntity>(0)
        }

        assertEquals(
            ordemServico,
            adapter.salvar(ordemServico),
        )
    }

    @Test
    fun `buscarPorId deve mapear quando presente`() {
        `when`(
            jpaRepository.findById(ordemServico.id.valor),
        ).thenReturn(
            Optional.of(jpa),
        )

        assertEquals(
            ordemServico,
            adapter.buscarPorId(ordemServico.id),
        )
    }

    @Test
    fun `listarTodos deve mapear lista`() {
        `when`(
            jpaRepository.findAll(),
        ).thenReturn(
            listOf(jpa),
        )

        assertEquals(
            listOf(ordemServico),
            adapter.listarTodos(),
        )
    }

    @Test
    fun `existePorId deve delegar ao jpa`() {
        `when`(
            jpaRepository.existsById(ordemServico.id.valor),
        ).thenReturn(true)

        assertTrue(
            adapter.existePorId(ordemServico.id),
        )
    }

    @Test
    fun `deletarPorId deve delegar deleteById`() {
        adapter.deletarPorId(ordemServico.id)

        verify(jpaRepository)
            .deleteById(ordemServico.id.valor)
    }

    @Test
    fun `listarPorStatus deve filtrar e mapear lista`() {
        val status = OrdemServicoStatus.RECEBIDA

        `when`(
            jpaRepository.findByStatus(status),
        ).thenReturn(
            listOf(jpa),
        )

        val resultado =
            adapter.listarPorStatus(status)

        assertEquals(
            listOf(ordemServico),
            resultado,
        )

        verify(jpaRepository)
            .findByStatus(status)
    }

    @Test
    fun `listarPorCliente deve filtrar e mapear lista`() {
        val clienteId = ordemServico.clienteId

        `when`(
            jpaRepository.findByClienteId(clienteId.valor),
        ).thenReturn(
            listOf(jpa),
        )

        val resultado =
            adapter.listarPorCliente(clienteId)

        assertEquals(
            listOf(ordemServico),
            resultado,
        )

        verify(jpaRepository)
            .findByClienteId(clienteId.valor)
    }

    @Test
    fun `listarPorVeiculo deve filtrar e mapear lista`() {
        val veiculoId = ordemServico.veiculoId

        `when`(
            jpaRepository.findByVeiculoId(veiculoId.valor),
        ).thenReturn(
            listOf(jpa),
        )

        val resultado =
            adapter.listarPorVeiculo(veiculoId)

        assertEquals(
            listOf(ordemServico),
            resultado,
        )

        verify(jpaRepository)
            .findByVeiculoId(veiculoId.valor)
    }

    @Test
    fun `listarPorDataAberturaEntre deve filtrar e mapear lista`() {
        val inicio =
            Instant.parse(
                "2026-08-16T20:00:00Z",
            )

        val fim =
            Instant.parse(
                "2026-08-16T23:59:59Z",
            )

        `when`(
            jpaRepository.findByDataAberturaBetween(
                inicio,
                fim,
            ),
        ).thenReturn(
            listOf(jpa),
        )

        val resultado =
            adapter.listarPorDataAberturaEntre(
                inicio,
                fim,
            )

        assertEquals(
            listOf(ordemServico),
            resultado,
        )

        verify(jpaRepository)
            .findByDataAberturaBetween(
                inicio,
                fim,
            )
    }
}
