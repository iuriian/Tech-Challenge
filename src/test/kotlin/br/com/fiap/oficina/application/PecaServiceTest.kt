package br.com.fiap.oficina.application

import br.com.fiap.oficina.anyObject
import br.com.fiap.oficina.application.service.PecaService
import br.com.fiap.oficina.domain.entity.Peca
import br.com.fiap.oficina.domain.repository.PecaRepository
import br.com.fiap.oficina.domain.valueobject.Id
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.math.BigDecimal

@ExtendWith(MockitoExtension::class)
class PecaServiceTest {
    @Mock
    lateinit var repository: PecaRepository

    @InjectMocks
    lateinit var service: PecaService

    private lateinit var peca: Peca

    @BeforeEach
    fun setup() {
        peca =
            Peca(
                id = Id.generate(),
                codigo = "PEC001",
                nome = "Filtro de Óleo",
                precoDeVenda = BigDecimal("45.00"),
                qtdEstoque = 10,
            )
    }

    @Test
    fun `deve salvar peca quando codigo nao existe`() {
        `when`(repository.existePorCodigo("PEC001")).thenReturn(false)
        `when`(repository.salvar(peca)).thenReturn(peca)

        assertEquals(peca, service.salvarPeca(peca))
        verify(repository).salvar(peca)
    }

    @Test
    fun `deve rejeitar peca com codigo ja cadastrado`() {
        `when`(repository.existePorCodigo("PEC001")).thenReturn(true)

        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                service.salvarPeca(peca)
            }

        assertEquals("Peça já cadastrada", exception.message)
        verify(repository, never()).salvar(peca)
    }

    @Test
    fun `deve atualizar dados da peca`() {
        `when`(repository.buscarAtivoPorCodigo("PEC001")).thenReturn(peca)
        `when`(repository.salvar(anyObject())).thenAnswer { it.getArgument<Peca>(0) }

        val dados = peca.copy(nome = "Filtro Novo", precoDeVenda = BigDecimal("60.00"))
        val resultado = service.atualizarPeca("PEC001", dados)

        assertEquals("Filtro Novo", resultado.nome)
        assertEquals(BigDecimal("60.00"), resultado.precoDeVenda)
    }

    @Test
    fun `deve retirar pecas do estoque`() {
        `when`(repository.buscarAtivoPorCodigo("PEC001")).thenReturn(peca)
        `when`(repository.salvar(anyObject())).thenAnswer { it.getArgument<Peca>(0) }

        assertEquals(7, service.retirarPecas("PEC001", 3)?.qtdEstoque)
    }

    @Test
    fun `deve repor pecas no estoque`() {
        `when`(repository.buscarAtivoPorCodigo("PEC001")).thenReturn(peca)
        `when`(repository.salvar(anyObject())).thenAnswer { it.getArgument<Peca>(0) }

        assertEquals(15, service.reporPecas("PEC001", 5)?.qtdEstoque)
    }

    @Test
    fun `deve desativar peca`() {
        `when`(repository.buscarAtivoPorCodigo("PEC001")).thenReturn(peca)
        `when`(repository.salvar(anyObject())).thenAnswer { it.getArgument<Peca>(0) }

        assertTrue(service.desativarPeca("PEC001"))
        assertTrue(service.deletarPeca("PEC001"))
    }

    @Test
    fun `deve reativar peca existente`() {
        `when`(repository.buscarPorCodigo("PEC001")).thenReturn(peca.copy(ativo = false))
        `when`(repository.salvar(anyObject())).thenAnswer { it.getArgument<Peca>(0) }

        assertTrue(service.reativarPeca("PEC001"))
    }

    @Test
    fun `deve retornar false ao reativar peca inexistente`() {
        `when`(repository.buscarPorCodigo("XPTO")).thenReturn(null)

        assertFalse(service.reativarPeca("XPTO"))
        verify(repository, never()).salvar(peca)
    }

    @Test
    fun `deve buscar peca ativa por codigo`() {
        `when`(repository.buscarAtivoPorCodigo("PEC001")).thenReturn(peca)

        assertEquals(peca, service.buscarPorCodigo("PEC001"))
    }

    @Test
    fun `deve lancar excecao ao buscar peca inexistente por codigo`() {
        `when`(repository.buscarAtivoPorCodigo("XPTO")).thenReturn(null)

        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                service.buscarPorCodigo("XPTO")
            }

        assertEquals("Peça não encontrada", exception.message)
    }

    @Test
    fun `deve delegar buscas e listagens ao repositorio`() {
        val id = Id.generate()
        `when`(repository.buscarAtivoPorNome("Filtro de Óleo")).thenReturn(peca)
        `when`(repository.existeAtivoPorCodigo("PEC001")).thenReturn(true)
        `when`(repository.buscarPorId(id)).thenReturn(peca)
        `when`(repository.buscarPorCodigo("PEC001")).thenReturn(peca)
        `when`(repository.existePorCodigo("PEC001")).thenReturn(true)
        `when`(repository.listarAtivos()).thenReturn(listOf(peca))

        assertEquals(peca, service.buscarPorNome("Filtro de Óleo"))
        assertTrue(service.existePorCodigo("PEC001"))
        assertEquals(peca, service.buscarGerencialPorId(id))
        assertEquals(peca, service.buscarEntreTodosPorCodigo("PEC001"))
        assertTrue(service.existeEntreTodosPorCodigo("PEC001"))
        assertEquals(listOf(peca), service.listarPecas())
    }
}
