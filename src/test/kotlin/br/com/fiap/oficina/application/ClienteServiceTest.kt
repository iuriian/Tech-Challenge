package br.com.fiap.oficina.application

import br.com.fiap.oficina.application.service.ClienteService
import br.com.fiap.oficina.domain.entity.Cliente
import br.com.fiap.oficina.domain.repository.ClienteRepository
import br.com.fiap.oficina.domain.valueobject.Documento
import br.com.fiap.oficina.domain.valueobject.Id
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class ClienteServiceTest {

    @Mock
    lateinit var repository: ClienteRepository

    @InjectMocks
    lateinit var service: ClienteService

    private lateinit var clienteId: Id
    private lateinit var cliente: Cliente

    @BeforeEach
    fun setup() {
        clienteId = Id.gerar()
        cliente = Cliente(
            id = clienteId,
            nome = "João Silva",
            documento = Documento.cpf("12345678909"),
            email = "joao@email.com"
        )
    }

    @Test
    fun `deve salvar cliente com sucesso`() {
        `when`(repository.salvar(cliente)).thenReturn(cliente)

        val resultado = service.salvarCliente(cliente)

        assertNotNull(resultado)
        assertEquals(cliente.id, resultado.id)
        assertEquals(cliente.nome, resultado.nome)
        assertEquals(cliente.email, resultado.email)
        verify(repository, times(1)).salvar(cliente)
    }

    @Test
    fun `deve buscar cliente por id com sucesso`() {
        `when`(repository.buscarPorId(clienteId)).thenReturn(cliente)

        val resultado = service.buscarPorId(clienteId)

        assertNotNull(resultado)
        assertEquals(cliente.id, resultado?.id)
        assertEquals(cliente.nome, resultado?.nome)
        verify(repository, times(1)).buscarPorId(clienteId)
    }

    @Test
    fun `deve retornar null quando cliente nao encontrado por id`() {
        val idInexistente = Id.gerar()
        `when`(repository.buscarPorId(idInexistente)).thenReturn(null)

        val resultado = service.buscarPorId(idInexistente)

        assertNull(resultado)
        verify(repository, times(1)).buscarPorId(idInexistente)
    }

    @Test
    fun `deve buscar cliente por documento com sucesso`() {
        val documentoNumero = "12345678909"
        `when`(repository.buscarPorDocumento(documentoNumero)).thenReturn(cliente)

        val resultado = service.buscarPorDocumento(documentoNumero)

        assertNotNull(resultado)
        assertEquals(cliente.nome, resultado?.nome)
        verify(repository, times(1)).buscarPorDocumento(documentoNumero)
    }

    @Test
    fun `deve retornar null quando cliente nao encontrado por documento`() {
        val documentoNumero = "00000000000"
        `when`(repository.buscarPorDocumento(documentoNumero)).thenReturn(null)

        val resultado = service.buscarPorDocumento(documentoNumero)

        assertNull(resultado)
        verify(repository, times(1)).buscarPorDocumento(documentoNumero)
    }

    @Test
    fun `deve buscar cliente por nome com sucesso`() {
        val nome = "João Silva"
        `when`(repository.buscarPorNome(nome)).thenReturn(cliente)

        val resultado = service.buscarPorNome(nome)

        assertNotNull(resultado)
        assertEquals(nome, resultado?.nome)
        verify(repository, times(1)).buscarPorNome(nome)
    }

    @Test
    fun `deve retornar null quando cliente nao encontrado por nome`() {
        val nome = "Nome Inexistente"
        `when`(repository.buscarPorNome(nome)).thenReturn(null)

        val resultado = service.buscarPorNome(nome)

        assertNull(resultado)
        verify(repository, times(1)).buscarPorNome(nome)
    }

    @Test
    fun `deve listar todos os clientes`() {
        `when`(repository.listarTodos()).thenReturn(listOf(cliente))

        val resultado = service.listarTodos()

        assertEquals(listOf(cliente), resultado)
        verify(repository, times(1)).listarTodos()
    }

    @Test
    fun `deve remover cliente por id`() {
        service.removerCliente(clienteId)

        verify(repository, times(1)).remover(clienteId)
    }
}
