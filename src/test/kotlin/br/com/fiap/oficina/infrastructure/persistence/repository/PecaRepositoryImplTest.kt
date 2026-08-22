package br.com.fiap.oficina.infrastructure.persistence.repository

import br.com.fiap.oficina.anyObject
import br.com.fiap.oficina.domain.entity.Peca
import br.com.fiap.oficina.domain.valueobject.Id
import br.com.fiap.oficina.infrastructure.persistence.entity.PecaJpaEntity
import br.com.fiap.oficina.infrastructure.persistence.jpa.PecaJpaRepository
import br.com.fiap.oficina.infrastructure.persistence.mapper.PecaPersistenceMapper
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.math.BigDecimal
import java.util.Optional
import kotlin.test.assertEquals
import kotlin.test.assertNull

@ExtendWith(MockitoExtension::class)
class PecaRepositoryImplTest {
    @Mock
    lateinit var jpaRepository: PecaJpaRepository

    private val mapper = PecaPersistenceMapper()
    private lateinit var repository: PecaRepositoryImpl
    private lateinit var peca: Peca
    private lateinit var jpa: PecaJpaEntity

    @BeforeEach
    fun setup() {
        repository = PecaRepositoryImpl(jpaRepository, mapper)
        peca =
            Peca(
                id = Id.generate(),
                codigo = "PEC001",
                nome = "Filtro",
                precoDeVenda = BigDecimal.TEN,
                qtdEstoque = 5,
            )
        jpa = mapper.toJpa(peca)
    }

    @Test
    fun `salvar deve persistir e mapear de volta`() {
        `when`(jpaRepository.save(anyObject())).thenAnswer { it.getArgument<PecaJpaEntity>(0) }

        assertEquals(peca, repository.salvar(peca))
    }

    @Test
    fun `listarAtivos deve mapear lista`() {
        `when`(jpaRepository.findAllByAtivoTrue()).thenReturn(listOf(jpa))

        assertEquals(listOf(peca), repository.listarAtivos())
    }

    @Test
    fun `buscarAtivoPorCodigo deve mapear resultado`() {
        `when`(jpaRepository.findByCodigoAndAtivoTrue("PEC001")).thenReturn(jpa)

        assertEquals(peca, repository.buscarAtivoPorCodigo("PEC001"))
    }

    @Test
    fun `buscarAtivoPorNome deve mapear resultado`() {
        `when`(jpaRepository.findByNomeIgnoreCaseAndAtivoTrue("Filtro")).thenReturn(jpa)

        assertEquals(peca, repository.buscarAtivoPorNome("Filtro"))
    }

    @Test
    fun `existeAtivoPorCodigo deve delegar ao jpa`() {
        `when`(jpaRepository.existsByCodigoAndAtivoTrue("PEC001")).thenReturn(true)

        assertTrue(repository.existeAtivoPorCodigo("PEC001"))
    }

    @Test
    fun `buscarPorCodigo deve mapear resultado`() {
        `when`(jpaRepository.findByCodigo("PEC001")).thenReturn(jpa)

        assertEquals(peca, repository.buscarPorCodigo("PEC001"))
    }

    @Test
    fun `existePorCodigo deve delegar ao jpa`() {
        `when`(jpaRepository.existsByCodigo("PEC001")).thenReturn(true)

        assertTrue(repository.existePorCodigo("PEC001"))
    }

    @Test
    fun `buscarPorId deve mapear quando presente`() {
        `when`(jpaRepository.findById(peca.id.valor)).thenReturn(Optional.of(jpa))

        assertEquals(peca, repository.buscarPorId(peca.id))
    }

    @Test
    fun `buscarPorId deve retornar null quando ausente`() {
        val id = Id.generate()
        `when`(jpaRepository.findById(id.valor)).thenReturn(Optional.empty())

        assertNull(repository.buscarPorId(id))
    }
}
