package br.com.fiap.oficina.domain.usecase.peca

import br.com.fiap.oficina.domain.repository.PecaRepository
import br.com.fiap.oficina.domain.entity.Peca
import br.com.fiap.oficina.domain.valueobject.Id
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import java.math.BigDecimal

@ExtendWith(MockitoExtension::class)
class BuscarPecaPorCodigoUseCaseTest {
    @Mock
    lateinit var repository: PecaRepository

    @InjectMocks
    lateinit var useCase: BuscarPecaPorCodigoUseCase

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
    fun `deve buscar peca ativa por codigo`() {
        `when`(repository.buscarAtivoPorCodigo("PEC001")).thenReturn(peca)

        assertEquals(peca, useCase.executar("PEC001"))
    }

    @Test
    fun `deve lancar excecao ao buscar peca inexistente por codigo`() {
        `when`(repository.buscarAtivoPorCodigo("XPTO")).thenReturn(null)

        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                useCase.executar("XPTO")
            }

        assertEquals("Peça não encontrada", exception.message)
    }
}
