package br.com.fiap.oficina.servico.domain.usecases

import br.com.fiap.oficina.anyObject
import br.com.fiap.oficina.domain.valueobject.Id
import br.com.fiap.oficina.servico.domain.entities.Servico
import br.com.fiap.oficina.servico.domain.exceptions.ServicoNaoEncontradoException
import br.com.fiap.oficina.servico.domain.repositories.ServicoRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.math.BigDecimal

@ExtendWith(MockitoExtension::class)
class AtualizarServicoUseCaseTest {
    @Mock
    private lateinit var repository: ServicoRepository

    @Test
    fun `deve atualizar descricao e valor preservando id e disponibilidade`() {
        val servico =
            Servico.criar(
                descricao = "Troca de óleo",
                valor = BigDecimal("150.00"),
            ).desativar()

        `when`(repository.buscarPorId(servico.id)).thenReturn(servico)
        `when`(repository.salvar(anyObject()))
            .thenAnswer { it.getArgument<Servico>(0) }

        val useCase = AtualizarServicoUseCase(repository)

        val resultado =
            useCase.executar(
                servico.id,
                AtualizarServicoInput(
                    descricao = "Troca de óleo premium",
                    valor = BigDecimal("200.00"),
                ),
            )

        assertEquals(servico.id, resultado.id)
        assertEquals("Troca de óleo premium", resultado.descricao)
        assertEquals(BigDecimal("200.00"), resultado.valor)
        assertFalse(resultado.ativo)

        verify(repository).salvar(resultado)
    }

    @Test
    fun `deve lancar excecao quando servico nao existir`() {
        val id = Id.generate()

        `when`(repository.buscarPorId(id)).thenReturn(null)

        val useCase = AtualizarServicoUseCase(repository)

        assertThrows(ServicoNaoEncontradoException::class.java) {
            useCase.executar(
                id,
                AtualizarServicoInput(
                    descricao = "Alinhamento",
                    valor = BigDecimal("100.00"),
                ),
            )
        }
    }
}
