package br.com.fiap.oficina.domain.usecase.cliente

import br.com.fiap.oficina.domain.repository.ClienteRepository
import br.com.fiap.oficina.domain.usecase.cliente.CriarClienteUseCase
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
class CriarClienteUseCaseTest {

    @Mock
    lateinit var clienteRepository: ClienteRepository

    @InjectMocks
    lateinit var useCase: CriarClienteUseCase

    private lateinit var cliente: Cliente

    @BeforeEach
    fun setUp(){
        cliente = Cliente(
            id = Id.generate(),
            nome = "Joao",
            documento = Documento.cpf("12345678909"),
            email = "joao@email.com",
        )
    }

    @Test
    fun `deve cadastrar uma cliente com sucesso`() {
        `when`(clienteRepository.salvar(cliente)).thenReturn(cliente)

        val resultado = useCase.executar(cliente)

        assertNotNull(resultado)
        assertEquals(cliente.id, resultado.id)
        assertEquals(cliente.nome, resultado.nome)
        assertEquals(cliente.documento, resultado.documento)
        assertEquals(cliente.email, resultado.email)

        verify(
            clienteRepository,
            times(1)
        ).salvar(cliente)
    }
}