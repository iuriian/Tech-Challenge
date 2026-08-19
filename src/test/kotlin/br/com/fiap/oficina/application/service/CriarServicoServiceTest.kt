package br.com.fiap.oficina.application.service

import br.com.fiap.oficina.anyObject
import br.com.fiap.oficina.application.port.`in`.CriarServicoCommand
import br.com.fiap.oficina.application.port.out.ServicoRepository
import br.com.fiap.oficina.domain.entity.Servico
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.math.BigDecimal

@ExtendWith(MockitoExtension::class)
class CriarServicoServiceTest {
    @Mock
    lateinit var servicoRepository: ServicoRepository

    private lateinit var service: CriarServicoService

    @BeforeEach
    fun setup() {
        service = CriarServicoService(servicoRepository)
    }

    @Test
    fun `deve criar e persistir servico de catalogo`() {
        `when`(servicoRepository.salvar(anyObject()))
            .thenAnswer { invocation ->
                invocation.getArgument<Servico>(0)
            }

        val resultado =
            service.executar(
                CriarServicoCommand(
                    descricao = "Troca de óleo",
                    valor = BigDecimal("150.00"),
                ),
            )

        assertEquals("Troca de óleo", resultado.descricao)
        assertEquals(BigDecimal("150.00"), resultado.valor)
        verify(servicoRepository).salvar(resultado)
    }
}