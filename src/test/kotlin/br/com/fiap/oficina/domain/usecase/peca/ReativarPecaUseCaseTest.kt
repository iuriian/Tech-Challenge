package br.com.fiap.oficina.domain.usecase.peca

import br.com.fiap.oficina.anyObject
import br.com.fiap.oficina.domain.entity.Peca
import br.com.fiap.oficina.domain.exception.PecaNaoEncontradoException
import br.com.fiap.oficina.domain.repository.PecaRepository
import br.com.fiap.oficina.domain.valueobject.Id
import org.junit.jupiter.api.Assertions.assertEquals
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
class ReativarPecaUseCaseTest {
    @Mock
    lateinit var repository: PecaRepository

    @InjectMocks
    lateinit var useCase: ReativarPecaUseCase

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
    fun `deve reativar peca existente`() {
        `when`(repository.buscarPorCodigo("PEC001")).thenReturn(peca.copy(ativo = false))
        `when`(repository.salvar(anyObject())).thenAnswer { it.getArgument<Peca>(0) }

        assertTrue(useCase.executar("PEC001"))
    }

    @Test
    fun `deve lancar excecao ao reativar peca inexistente`() {
        `when`(repository.buscarPorCodigo("XPTO")).thenReturn(null)

        val exception =
            assertThrows(PecaNaoEncontradoException::class.java) {
                useCase.executar("XPTO")
            }

        assertEquals("Peça não encontrada com o código: XPTO", exception.message)
        verify(repository, never()).salvar(peca)
    }
}
