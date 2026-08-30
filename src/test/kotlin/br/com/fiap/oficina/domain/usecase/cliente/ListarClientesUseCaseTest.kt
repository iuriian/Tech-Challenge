package br.com.fiap.oficina.domain.usecase.cliente

import br.com.fiap.oficina.domain.entity.Cliente
import br.com.fiap.oficina.domain.repository.ClienteRepository
import br.com.fiap.oficina.domain.usecase.cliente.ListarClientesUseCase
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

@ExtendWith(MockitoExtension::class)
class ListarClientesUseCaseTest {

    @Mock
    lateinit var clienteRepository: ClienteRepository

    @InjectMocks
    lateinit var useCase: ListarClientesUseCase

    private lateinit var cliente: Cliente

    @BeforeEach
    fun setUp() {
        cliente = Cliente(
            id = Id.generate(),
            nome = "Joao",
            documento = Documento.cpf("12345678909"),
            email = "joao@email.com",
        )
    }

    @Test
    fun `deve listar todos os clientes`() {
        `when`(clienteRepository.listarTodos()).thenReturn(listOf(cliente))

        val resultado = useCase.executar()

        assertEquals(listOf(cliente), resultado)
        verify(clienteRepository, times(1)).listarTodos()
    }
}
