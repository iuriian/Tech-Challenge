package br.com.fiap.oficina.domain.usecase.cliente

import br.com.fiap.oficina.domain.entity.Cliente
import br.com.fiap.oficina.domain.repository.ClienteRepository
import br.com.fiap.oficina.domain.usecase.cliente.BuscarClientePorNomeUseCase
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
class BuscarClientePorNomeUseCaseTest {

    @Mock
    lateinit var clienteRepository: ClienteRepository

    @InjectMocks
    lateinit var useCase: BuscarClientePorNomeUseCase

    private lateinit var cliente: Cliente
    private val nome = "Joao"

    @BeforeEach
    fun setUp() {
        cliente = Cliente(
            id = Id.generate(),
            nome = nome,
            documento = Documento.cpf("12345678909"),
            email = "joao@email.com",
        )
    }

    @Test
    fun `deve buscar cliente por nome com sucesso`() {
        `when`(clienteRepository.buscarPorNome(nome)).thenReturn(cliente)

        val resultado = useCase.executar(nome)

        assertNotNull(resultado)
        assertEquals(nome, resultado.nome)
        verify(clienteRepository, times(1)).buscarPorNome(nome)
    }
}
