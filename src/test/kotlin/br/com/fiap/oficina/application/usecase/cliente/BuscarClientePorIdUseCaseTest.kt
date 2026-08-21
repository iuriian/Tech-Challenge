package br.com.fiap.oficina.application.usecase.cliente

import br.com.fiap.oficina.domain.repository.ClienteRepository
import br.com.fiap.oficina.domain.usecase.cliente.BuscarClientePorIdUseCase
import br.com.fiap.oficina.domain.entity.Cliente
import br.com.fiap.oficina.domain.valueobject.Documento
import br.com.fiap.oficina.domain.valueobject.Id
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@ExtendWith(MockitoExtension::class)
class BuscarClientePorIdUseCaseTest {

    @Mock
    lateinit var clienteRepository: ClienteRepository

    @InjectMocks
    lateinit var useCase: BuscarClientePorIdUseCase

    private lateinit var cliente: Cliente
    private lateinit var clienteId: Id

    @BeforeEach
    fun setUp() {
        clienteId = Id.generate()
        cliente = Cliente(
            id = clienteId,
            nome = "Joao",
            documento = Documento.cpf("12345678909"),
            email = "joao@email.com",
        )
    }

    @Test
    fun `deve buscar cliente por id com sucesso`() {
        `when`(clienteRepository.buscarPorId(clienteId)).thenReturn(cliente)

        val resultado = useCase.executar(clienteId)

        assertNotNull(resultado)
        assertEquals(cliente.id, resultado.id)
        verify(clienteRepository, times(1)).buscarPorId(clienteId)
    }
}
