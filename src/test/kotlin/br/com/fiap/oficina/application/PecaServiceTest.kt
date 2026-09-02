package br.com.fiap.oficina.application

import br.com.fiap.oficina.anyObject
import br.com.fiap.oficina.application.dto.PecaRequest
import br.com.fiap.oficina.application.mapper.PecaMapper
import br.com.fiap.oficina.application.service.PecaService
import br.com.fiap.oficina.domain.entity.Peca
import br.com.fiap.oficina.domain.exception.PecaNaoEncontradoException
import br.com.fiap.oficina.domain.usecase.peca.AtualizarPecaUseCase
import br.com.fiap.oficina.domain.usecase.peca.BuscarPecaPorCodigoUseCase
import br.com.fiap.oficina.domain.usecase.peca.BuscarPecaPorNomeUseCase
import br.com.fiap.oficina.domain.usecase.peca.CriarPecaUseCase
import br.com.fiap.oficina.domain.usecase.peca.DeletarPecaUseCase
import br.com.fiap.oficina.domain.usecase.peca.ListarPecasUseCase
import br.com.fiap.oficina.domain.usecase.peca.ReativarPecaUseCase
import br.com.fiap.oficina.domain.usecase.peca.ReporPecasUseCase
import br.com.fiap.oficina.domain.usecase.peca.RetirarPecasUseCase
import br.com.fiap.oficina.domain.valueobject.Id
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.doThrow
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.math.BigDecimal

@ExtendWith(MockitoExtension::class)
class PecaServiceTest {
    @Mock
    lateinit var criarPecaUseCase: CriarPecaUseCase

    @Mock
    lateinit var listarPecasUseCase: ListarPecasUseCase

    @Mock
    lateinit var buscarPecaPorCodigoUseCase: BuscarPecaPorCodigoUseCase

    @Mock
    lateinit var buscarPecaPorNomeUseCase: BuscarPecaPorNomeUseCase

    @Mock
    lateinit var atualizarPecaUseCase: AtualizarPecaUseCase

    @Mock
    lateinit var retirarPecasUseCase: RetirarPecasUseCase

    @Mock
    lateinit var reporPecasUseCase: ReporPecasUseCase

    @Mock
    lateinit var reativarPecaUseCase: ReativarPecaUseCase

    @Mock
    lateinit var deletarPecaUseCase: DeletarPecaUseCase

    private val mapper = PecaMapper()
    private lateinit var service: PecaService
    private lateinit var peca: Peca

    @BeforeEach
    fun setup() {
        service =
            PecaService(
                criarPecaUseCase,
                listarPecasUseCase,
                buscarPecaPorCodigoUseCase,
                buscarPecaPorNomeUseCase,
                atualizarPecaUseCase,
                retirarPecasUseCase,
                reporPecasUseCase,
                reativarPecaUseCase,
                deletarPecaUseCase,
                mapper,
            )
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
    fun `deve criar peca`() {
        val request =
            PecaRequest(
                codigo = "PEC001",
                nome = "Filtro de Óleo",
                precoDeVenda = 45.00,
                qtdEstoque = 10,
            )
        `when`(criarPecaUseCase.executar(anyObject())).thenReturn(peca)

        val response = service.criar(request)

        assertEquals("PEC001", response.codigo)
        assertEquals("Filtro de Óleo", response.nome)
        verify(criarPecaUseCase).executar(anyObject())
    }

    @Test
    fun `deve listar pecas`() {
        `when`(listarPecasUseCase.executar()).thenReturn(listOf(peca))

        val response = service.listar()

        assertEquals(1, response.size)
        assertEquals("PEC001", response.first().codigo)
    }

    @Test
    fun `deve buscar peca por codigo`() {
        `when`(buscarPecaPorCodigoUseCase.executar("PEC001")).thenReturn(peca)

        val response = service.buscarPorCodigo("PEC001")

        assertEquals("PEC001", response.codigo)
    }

    @Test
    fun `deve propagar excecao ao buscar por codigo inexistente`() {
        `when`(buscarPecaPorCodigoUseCase.executar("XPTO"))
            .thenThrow(PecaNaoEncontradoException.porCodigo("XPTO"))

        assertThrows(PecaNaoEncontradoException::class.java) {
            service.buscarPorCodigo("XPTO")
        }
    }

    @Test
    fun `deve buscar peca por nome`() {
        `when`(buscarPecaPorNomeUseCase.executar("Filtro de Óleo")).thenReturn(peca)

        val response = service.buscarPorNome("Filtro de Óleo")

        assertEquals("Filtro de Óleo", response.nome)
    }

    @Test
    fun `deve propagar excecao ao buscar por nome inexistente`() {
        `when`(buscarPecaPorNomeUseCase.executar("Inexistente"))
            .thenThrow(PecaNaoEncontradoException.porNome("Inexistente"))

        assertThrows(PecaNaoEncontradoException::class.java) {
            service.buscarPorNome("Inexistente")
        }
    }

    @Test
    fun `deve atualizar peca`() {
        val request = PecaRequest(nome = "Filtro Novo", precoDeVenda = 60.00)
        val atualizada = peca.copy(nome = "Filtro Novo", precoDeVenda = BigDecimal("60.00"))
        `when`(atualizarPecaUseCase.executar(anyObject(), anyObject())).thenReturn(atualizada)

        val response = service.atualizar("PEC001", request)

        assertEquals("Filtro Novo", response.nome)
        assertEquals(60.00, response.precoDeVenda)
    }

    @Test
    fun `deve retirar pecas do estoque`() {
        val atualizada = peca.copy(qtdEstoque = 7)
        `when`(retirarPecasUseCase.executar("PEC001", 3)).thenReturn(atualizada)

        val response = service.retirar("PEC001", 3)

        assertEquals(7, response.qtdEstoque)
    }

    @Test
    fun `deve repor pecas no estoque`() {
        val atualizada = peca.copy(qtdEstoque = 15)
        `when`(reporPecasUseCase.executar("PEC001", 5)).thenReturn(atualizada)

        val response = service.repor("PEC001", 5)

        assertEquals(15, response.qtdEstoque)
    }

    @Test
    fun `deve reativar peca`() {
        `when`(reativarPecaUseCase.executar("PEC001")).thenReturn(true)

        assertEquals(true, service.reativar("PEC001"))
    }

    @Test
    fun `deve deletar peca`() {
        `when`(deletarPecaUseCase.executar("PEC001")).thenReturn(true)

        assertEquals(true, service.deletar("PEC001"))
    }

    @Test
    fun `deve propagar excecao ao deletar peca inexistente`() {
        doThrow(PecaNaoEncontradoException.porCodigo("XPTO"))
            .`when`(deletarPecaUseCase)
            .executar("XPTO")

        assertThrows(PecaNaoEncontradoException::class.java) {
            service.deletar("XPTO")
        }
    }
}
