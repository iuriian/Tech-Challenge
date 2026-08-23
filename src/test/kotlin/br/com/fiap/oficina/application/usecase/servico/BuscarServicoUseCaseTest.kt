package br.com.fiap.oficina.application.usecase.servico

import br.com.fiap.oficina.application.exception.servico.ServicoNaoEncontradoException
import br.com.fiap.oficina.application.repository.servico.ServicoRepository
import br.com.fiap.oficina.domain.entity.servico.Servico
import br.com.fiap.oficina.domain.valueobject.Id
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.math.BigDecimal

@ExtendWith(MockitoExtension::class)
class BuscarServicoUseCaseTest {
    @Mock
    private lateinit var repository: ServicoRepository

    @Test
    fun `deve buscar servico por id`() {
        val servico =
            Servico.criar(
                descricao = "Alinhamento",
                valor = BigDecimal("100.00"),
            )

        `when`(repository.buscarPorId(servico.id)).thenReturn(servico)

        val useCase = BuscarServicoUseCase(repository)

        val resultado = useCase.executar(servico.id)

        assertEquals(servico, resultado)

        verify(repository).buscarPorId(servico.id)
    }

    @Test
    fun `deve lancar excecao quando servico nao existir`() {
        val id = Id.generate()

        `when`(repository.buscarPorId(id)).thenReturn(null)

        val useCase = BuscarServicoUseCase(repository)

        val exception =
            assertThrows(ServicoNaoEncontradoException::class.java) {
                useCase.executar(id)
            }

        assertEquals(
            "Serviço não encontrado com o ID: $id",
            exception.message,
        )
    }
}