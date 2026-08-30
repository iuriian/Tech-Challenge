package br.com.fiap.oficina.domain.usecase.cliente

import br.com.fiap.oficina.domain.entity.Cliente
import br.com.fiap.oficina.domain.exception.ClienteNaoEncontradoException
import br.com.fiap.oficina.domain.repository.ClienteRepository
import br.com.fiap.oficina.domain.valueobject.Documento
import br.com.fiap.oficina.domain.valueobject.Id
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
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
        val cliente =
            Cliente(
                id = clienteId,
                nome = "Joao",
                documento = Documento.cpf("12345678909"),
                email = "joao@email.com",
            )
        `when`(clienteRepository.buscarPorId(clienteId)).thenReturn(cliente)

        useCase.executar(clienteId)

        verify(clienteRepository, times(1)).buscarPorId(clienteId)
        verify(clienteRepository, times(1)).remover(clienteId)
    }

    @Test
    fun `deve lancar excecao quando cliente nao encontrado`() {
        val clienteId = Id.generate()
        `when`(clienteRepository.buscarPorId(clienteId)).thenReturn(null)

        assertThrows<ClienteNaoEncontradoException> {
            useCase.executar(clienteId)
        }
    }
}
