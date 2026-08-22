package br.com.fiap.oficina.application.usecase.peca

import br.com.fiap.oficina.application.port.out.PecaRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class ExistePecaPorCodigoUseCaseTest {
    @Mock
    lateinit var repository: PecaRepository

    @InjectMocks
    lateinit var useCase: ExistePecaPorCodigoUseCase

    @Test
    fun `deve verificar existencia de peca ativa por codigo`() {
        `when`(repository.existeAtivoPorCodigo("PEC001")).thenReturn(true)

        assertTrue(useCase.executar("PEC001"))
    }
}
