package br.com.fiap.oficina.application.usecase.peca

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class DeletarPecaUseCaseTest {
    @Mock
    lateinit var desativarPecaUseCase: DesativarPecaUseCase

    @InjectMocks
    lateinit var useCase: DeletarPecaUseCase

    @Test
    fun `deve delegar desativacao ao use case`() {
        `when`(desativarPecaUseCase.executar("PEC001")).thenReturn(true)

        assertTrue(useCase.executar("PEC001"))
        verify(desativarPecaUseCase).executar("PEC001")
    }
}
