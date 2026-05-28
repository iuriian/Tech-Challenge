package br.com.fiap.oficina.application

import br.com.fiap.oficina.infrastructure.persistence.entity.Cliente
import br.com.fiap.oficina.infrastructure.persistence.entity.Documento
import br.com.fiap.oficina.infrastructure.persistence.repository.ClienteRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import java.util.*

@ExtendWith(MockitoExtension::class)
class ClienteServiceTest {

    @Mock
    lateinit var repository: ClienteRepository

    @InjectMocks
    lateinit var service: ClienteService

    private lateinit var cliente: Cliente

    @BeforeEach
    fun setup() {
        cliente = Cliente().apply {
            id = 1L
            nome = "João Silva"
            documento = Documento.cpf("12345678909")
            email = "joao@email.com"
        }
    }

    @Test
    fun `deve salvar cliente com sucesso`() {
        `when`(repository.save(cliente)).thenReturn(cliente)

        val resultado = service.salvarCliente(cliente)

        assertNotNull(resultado)
        assertEquals(cliente.id, resultado.id)
        assertEquals(cliente.nome, resultado.nome)
        assertEquals(cliente.email, resultado.email)
        verify(repository, times(1)).save(cliente)
    }

    @Test
    fun `deve buscar cliente por id com sucesso`() {
        val id = 1L
        `when`(repository.findById(id)).thenReturn(Optional.of(cliente))

        val resultado = service.buscarPorId(id)

        assertNotNull(resultado)
        assertEquals(cliente.id, resultado?.id)
        assertEquals(cliente.nome, resultado?.nome)
        verify(repository, times(1)).findById(id)
    }

    @Test
    fun `deve retornar null quando cliente nao encontrado por id`() {
        val id = 999L
        `when`(repository.findById(id)).thenReturn(Optional.empty())

        val resultado = service.buscarPorId(id)

        assertNull(resultado)
        verify(repository, times(1)).findById(id)
    }

    @Test
    fun `deve buscar cliente por documento com sucesso`() {
        val documentoNumero = "12345678909"
        `when`(repository.findByDocumentoNumero(documentoNumero)).thenReturn(cliente)

        val resultado = service.buscarPorDocumento(documentoNumero)

        assertNotNull(resultado)
        assertEquals(cliente.nome, resultado?.nome)
        verify(repository, times(1)).findByDocumentoNumero(documentoNumero)
    }

    @Test
    fun `deve retornar null quando cliente nao encontrado por documento`() {
        val documentoNumero = "00000000000"
        `when`(repository.findByDocumentoNumero(documentoNumero)).thenReturn(null)

        val resultado = service.buscarPorDocumento(documentoNumero)

        assertNull(resultado)
        verify(repository, times(1)).findByDocumentoNumero(documentoNumero)
    }

    @Test
    fun `deve buscar cliente por nome com sucesso`() {
        val nome = "João Silva"
        `when`(repository.findByNome(nome)).thenReturn(cliente)

        val resultado = service.buscarPorNome(nome)

        assertNotNull(resultado)
        assertEquals(nome, resultado?.nome)
        verify(repository, times(1)).findByNome(nome)
    }

    @Test
    fun `deve retornar null quando cliente nao encontrado por nome`() {
        val nome = "Nome Inexistente"
        `when`(repository.findByNome(nome)).thenReturn(null)

        val resultado = service.buscarPorNome(nome)

        assertNull(resultado)
        verify(repository, times(1)).findByNome(nome)
    }
}