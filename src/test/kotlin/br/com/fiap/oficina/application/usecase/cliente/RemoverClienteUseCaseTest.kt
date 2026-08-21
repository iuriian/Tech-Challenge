package br.com.fiap.oficina.application.usecase.cliente

import br.com.fiap.oficina.application.port.out.ClienteRepository
import br.com.fiap.oficina.domain.valueobject.Id
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class RemoverClienteUseCaseTest {

    @Mock
    lateinit var clienteRepository: ClienteRepository

    @InjectMocks
    lateinit var useCase: RemoverClienteUseCase

    @Test
    fun `deve remover cliente por id`() {
        val clienteId = Id.generate()

        useCase.executar(clienteId)

        verify(clienteRepository, times(1)).remover(clienteId)
    }
}
