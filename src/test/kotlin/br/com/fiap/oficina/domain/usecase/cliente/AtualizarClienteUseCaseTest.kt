package br.com.fiap.oficina.domain.usecase.cliente

import br.com.fiap.oficina.domain.repository.ClienteRepository
import br.com.fiap.oficina.domain.entity.Cliente
import br.com.fiap.oficina.domain.valueobject.Documento
import br.com.fiap.oficina.domain.valueobject.Id
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
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
class AtualizarClienteUseCaseTest {

    @Mock
    lateinit var clienteRepository: ClienteRepository

    @InjectMocks
    lateinit var useCase: AtualizarClienteUseCase

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
    fun `deve atualizar um cliente com sucesso`() {
        `when`(clienteRepository.buscarPorId(cliente.id)).thenReturn(cliente)
        `when`(clienteRepository.salvar(cliente)).thenReturn(cliente)

        val resultado = useCase.executar(cliente)

        assertNotNull(resultado)
        assertEquals(cliente.id, resultado.id)
        assertEquals(cliente.nome, resultado.nome)
        assertEquals(cliente.documento, resultado.documento)
        assertEquals(cliente.email, resultado.email)

        verify(clienteRepository, times(1)).buscarPorId(cliente.id)
        verify(clienteRepository, times(1)).salvar(cliente)
    }

    @Test
    fun `deve lancar excecao quando cliente nao encontrado`() {
        `when`(clienteRepository.buscarPorId(cliente.id)).thenReturn(null)

        val exception = assertThrows<IllegalArgumentException> {
            useCase.executar(cliente)
        }

        assertEquals("Cliente não encontrado com o ID: ${cliente.id}", exception.message)
        verify(clienteRepository, times(1)).buscarPorId(cliente.id)
        verify(clienteRepository, times(0)).salvar(cliente)
    }
}
