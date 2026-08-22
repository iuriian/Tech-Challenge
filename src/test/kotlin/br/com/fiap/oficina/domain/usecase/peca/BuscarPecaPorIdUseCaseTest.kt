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
class BuscarPecaPorIdUseCaseTest {
    @Mock
    lateinit var repository: PecaRepository

    @InjectMocks
    lateinit var useCase: BuscarPecaPorIdUseCase

    private lateinit var peca: Peca
    private lateinit var id: Id

    @BeforeEach
    fun setup() {
        id = Id.generate()
        peca =
            Peca(
                id = id,
                codigo = "PEC001",
                nome = "Filtro de Óleo",
                precoDeVenda = BigDecimal("45.00"),
                qtdEstoque = 10,
            )
    }

    @Test
    fun `deve buscar peca por id`() {
        `when`(repository.buscarPorId(id)).thenReturn(peca)

        assertEquals(peca, useCase.executar(id))
    }
}
