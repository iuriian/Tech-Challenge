package br.com.fiap.oficina.application.usecase.cliente

import br.com.fiap.oficina.application.port.out.ClienteRepository
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
class BuscarClientePorDocumentoUseCaseTest {

    @Mock
    lateinit var clienteRepository: ClienteRepository

    @InjectMocks
    lateinit var useCase: BuscarClientePorDocumentoUseCase

    private lateinit var cliente: Cliente
    private val numeroDocumento = "12345678909"

    @BeforeEach
    fun setUp() {
        cliente = Cliente(
            id = Id.generate(),
            nome = "Joao",
            documento = Documento.cpf(numeroDocumento),
            email = "joao@email.com",
        )
    }

    @Test
    fun `deve buscar cliente por documento com sucesso`() {
        `when`(clienteRepository.buscarPorDocumento(numeroDocumento)).thenReturn(cliente)

        val resultado = useCase.executar(numeroDocumento)

        assertNotNull(resultado)
        assertEquals(cliente.nome, resultado.nome)
        verify(clienteRepository, times(1)).buscarPorDocumento(numeroDocumento)
    }
}
