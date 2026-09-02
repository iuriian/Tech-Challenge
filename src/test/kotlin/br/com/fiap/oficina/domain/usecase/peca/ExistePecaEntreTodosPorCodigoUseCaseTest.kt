package br.com.fiap.oficina.domain.usecase.peca

import br.com.fiap.oficina.domain.repository.PecaRepository
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class ExistePecaEntreTodosPorCodigoUseCaseTest {
    @Mock
    lateinit var repository: PecaRepository

    @InjectMocks
    lateinit var useCase: ExistePecaEntreTodosPorCodigoUseCase

    @Test
    fun `deve verificar existencia de peca por codigo incluindo inativas`() {
        `when`(repository.existePorCodigo("PEC001")).thenReturn(true)

        assertTrue(useCase.executar("PEC001"))
    }
}
