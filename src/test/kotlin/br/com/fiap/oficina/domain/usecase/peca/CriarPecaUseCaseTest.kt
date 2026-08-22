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
class CriarPecaUseCaseTest {
    @Mock
    lateinit var repository: PecaRepository

    @InjectMocks
    lateinit var useCase: CriarPecaUseCase

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

        assertEquals(peca, useCase.executar(peca))
        verify(repository).salvar(peca)
    }

    @Test
    fun `deve rejeitar peca com codigo ja cadastrado`() {
        `when`(repository.existePorCodigo("PEC001")).thenReturn(true)

        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                useCase.executar(peca)
            }

        assertEquals("Peça já cadastrada", exception.message)
        verify(repository, never()).salvar(peca)
    }
}
