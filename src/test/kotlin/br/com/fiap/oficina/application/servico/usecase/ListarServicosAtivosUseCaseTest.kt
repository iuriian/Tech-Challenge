package br.com.fiap.oficina.application.servico.usecase

import br.com.fiap.oficina.application.servico.repository.ServicoRepository
import br.com.fiap.oficina.domain.servico.Servico
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.math.BigDecimal

@ExtendWith(MockitoExtension::class)
class ListarServicosAtivosUseCaseTest {
    @Mock
    private lateinit var repository: ServicoRepository

    @Test
    fun `deve listar servicos ativos`() {
        val servicos =
            listOf(
                Servico.criar(
                    descricao = "Troca de óleo",
                    valor = BigDecimal("150.00"),
                ),
                Servico.criar(
                    descricao = "Alinhamento",
                    valor = BigDecimal("100.00"),
                ),
            )

        `when`(repository.listarAtivos()).thenReturn(servicos)

        val useCase = ListarServicosAtivosUseCase(repository)

        val resultado = useCase.executar()

        assertEquals(servicos, resultado)

        verify(repository).listarAtivos()
    }
}
