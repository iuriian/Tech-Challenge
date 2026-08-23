package br.com.fiap.oficina.application.usecase.servico

import br.com.fiap.oficina.anyObject
import br.com.fiap.oficina.application.exception.servico.ServicoNaoEncontradoException
import br.com.fiap.oficina.application.repository.servico.ServicoRepository
import br.com.fiap.oficina.domain.entity.servico.Servico
import br.com.fiap.oficina.domain.valueobject.Id
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.math.BigDecimal

@ExtendWith(MockitoExtension::class)
class ReativarServicoUseCaseTest {
    @Mock
    private lateinit var repository: ServicoRepository

    @Test
    fun `deve reativar servico`() {
        val servico =
            Servico.criar(
                descricao = "Balanceamento",
                valor = BigDecimal("80.00"),
            ).desativar()

        `when`(repository.buscarPorId(servico.id)).thenReturn(servico)
        `when`(repository.salvar(anyObject()))
            .thenAnswer { it.getArgument<Servico>(0) }

        val useCase = ReativarServicoUseCase(repository)

        val resultado = useCase.executar(servico.id)

        assertFalse(servico.ativo)
        assertTrue(resultado.ativo)

        verify(repository).salvar(resultado)
    }

    @Test
    fun `deve lancar excecao quando servico nao existir`() {
        val id = Id.generate()

        `when`(repository.buscarPorId(id)).thenReturn(null)

        val useCase = ReativarServicoUseCase(repository)

        assertThrows(ServicoNaoEncontradoException::class.java) {
            useCase.executar(id)
        }
    }
}