package br.com.fiap.oficina.servico.domain.usecases

import br.com.fiap.oficina.servico.domain.entities.Servico
import br.com.fiap.oficina.servico.domain.repositories.ServicoRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.math.BigDecimal

@ExtendWith(MockitoExtension::class)
class ListarTodosServicosUseCaseTest {
    @Mock
    private lateinit var repository: ServicoRepository

    @Test
    fun `deve listar servicos ativos e inativos`() {
        val ativo =
            Servico.criar(
                descricao = "Troca de óleo",
                valor = BigDecimal("150.00"),
            )

        val inativo =
            Servico.criar(
                descricao = "Alinhamento",
                valor = BigDecimal("100.00"),
            ).desativar()

        `when`(repository.listarTodos())
            .thenReturn(listOf(ativo, inativo))

        val useCase = ListarTodosServicosUseCase(repository)

        val resultado = useCase.executar()

        assertEquals(2, resultado.size)
        assertTrue(resultado.first().ativo)
        assertFalse(resultado.last().ativo)

        verify(repository).listarTodos()
    }
}
