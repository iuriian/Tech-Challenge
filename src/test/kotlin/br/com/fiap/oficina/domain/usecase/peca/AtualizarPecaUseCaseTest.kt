package br.com.fiap.oficina.domain.usecase.peca

import br.com.fiap.oficina.anyObject
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
class AtualizarPecaUseCaseTest {
    @Mock
    lateinit var repository: PecaRepository

    @InjectMocks
    lateinit var useCase: AtualizarPecaUseCase

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
    fun `deve atualizar dados da peca`() {
        `when`(repository.buscarAtivoPorCodigo("PEC001")).thenReturn(peca)
        `when`(repository.salvar(anyObject())).thenAnswer { it.getArgument<Peca>(0) }

        val dados = peca.copy(nome = "Filtro Novo", precoDeVenda = BigDecimal("60.00"))
        val resultado = useCase.executar("PEC001", dados)

        assertEquals("Filtro Novo", resultado.nome)
        assertEquals(BigDecimal("60.00"), resultado.precoDeVenda)
    }
}
