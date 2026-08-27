package br.com.fiap.oficina.domain.usecase.peca

import br.com.fiap.oficina.anyObject
import br.com.fiap.oficina.domain.entity.Peca
import br.com.fiap.oficina.domain.exception.PecaNaoEncontradoException
import br.com.fiap.oficina.domain.repository.PecaRepository
import br.com.fiap.oficina.domain.valueobject.Id
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.math.BigDecimal

@ExtendWith(MockitoExtension::class)
class DesativarPecaUseCaseTest {
    @Mock
    lateinit var repository: PecaRepository

    @InjectMocks
    lateinit var useCase: DesativarPecaUseCase

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
    fun `deve desativar peca`() {
        `when`(repository.buscarAtivoPorCodigo("PEC001")).thenReturn(peca)
        `when`(repository.salvar(anyObject())).thenAnswer { it.getArgument<Peca>(0) }

        assertTrue(useCase.executar("PEC001"))
    }

    @Test
    fun `deve lancar excecao quando peca nao encontrada`() {
        `when`(repository.buscarAtivoPorCodigo("XPTO")).thenReturn(null)

        org.junit.jupiter.api.assertThrows<PecaNaoEncontradoException> {
            useCase.executar("XPTO")
        }
    }
}
