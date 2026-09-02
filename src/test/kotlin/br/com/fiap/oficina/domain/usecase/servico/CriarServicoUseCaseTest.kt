package br.com.fiap.oficina.domain.usecase.servico

import br.com.fiap.oficina.anyObject
import br.com.fiap.oficina.domain.entity.Servico
import br.com.fiap.oficina.domain.repository.ServicoRepository
import br.com.fiap.oficina.domain.usecase.servico.CriarServicoUseCase
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.math.BigDecimal

@ExtendWith(MockitoExtension::class)
class CriarServicoUseCaseTest {
    @Mock
    private lateinit var repository: ServicoRepository

    @Test
    fun `deve salvar servico ativo`() {
        `when`(repository.salvar(anyObject()))
            .thenAnswer { it.getArgument<Servico>(0) }

        val useCase = CriarServicoUseCase(repository)

        val servico =
            Servico.criar(
                descricao = "Troca de óleo",
                valor = BigDecimal("150.00"),
            )

        val resultado = useCase.executar(servico)

        assertEquals("Troca de óleo", resultado.descricao)
        assertEquals(BigDecimal("150.00"), resultado.valor)
        assertTrue(resultado.ativo)

        verify(repository).salvar(servico)
    }
}
