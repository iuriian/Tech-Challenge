package br.com.fiap.oficina.application

import br.com.fiap.oficina.application.service.ServicoService
import br.com.fiap.oficina.domain.entity.Cliente
import br.com.fiap.oficina.domain.entity.Servico
import br.com.fiap.oficina.domain.repository.ClienteRepository
import br.com.fiap.oficina.domain.repository.ServicoRepository
import br.com.fiap.oficina.domain.repository.VeiculoRepository
import br.com.fiap.oficina.domain.repository.PecaRepository
import br.com.fiap.oficina.domain.entity.Veiculo
import br.com.fiap.oficina.domain.entity.Peca
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class ServicoServiceTest {

    @Mock lateinit var repository: ServicoRepository

    @Mock lateinit var clienteRepository: ClienteRepository

    @Mock lateinit var veiculoRepository: VeiculoRepository

    @Mock lateinit var pecaRepository: PecaRepository

    @InjectMocks lateinit var service: ServicoService

    private lateinit var cliente: Cliente
    private lateinit var veiculo: Veiculo
    private lateinit var peca1: Peca
    private lateinit var peca2: Peca
    private lateinit var servico: Servico

    @BeforeEach
    fun setup() {
        cliente =
                Cliente().apply {
                    id = 1L
                    nome = "Cliente Teste"
                }

        veiculo = Veiculo().apply {
            idVeiculo = 1L
        }

        peca1 = Peca().apply { id = 1L }
        peca2 = Peca().apply { id = 2L }

        servico =
                Servico().apply {
                    id = 1L
                    descricao = "Troca de Óleo"
                    this.cliente = this@ServicoServiceTest.cliente
                    this.veiculo = this@ServicoServiceTest.veiculo
                    funcionarioId = 1L
                    this.pecas = listOf(peca1, peca2)
                }
    }

    @Test
    fun `deve salvar servico com sucesso`() {
        `when`(clienteRepository.buscarPorId(1L)).thenReturn(cliente)
        `when`(veiculoRepository.buscarPorId(1L)).thenReturn(veiculo)
        `when`(pecaRepository.buscarPorId(1L)).thenReturn(peca1)
        `when`(pecaRepository.buscarPorId(2L)).thenReturn(peca2)
        `when`(repository.salvar(servico)).thenReturn(servico)

        val resultado = service.salvar(servico, 1L, 1L, listOf(1L, 2L))

        assertNotNull(resultado)
        assertEquals(servico.id, resultado.id)
        assertEquals(cliente, resultado.cliente)
        verify(repository, times(1)).salvar(servico)
    }

    @Test
    fun `deve lancar excecao ao salvar servico com cliente inexistente`() {
        `when`(clienteRepository.buscarPorId(1L)).thenReturn(null)

        val exception =
                assertThrows(IllegalArgumentException::class.java) { service.salvar(servico, 1L, 1L, listOf(1L, 2L)) }

        assertEquals("Cliente não encontrado com o ID: 1", exception.message)
        verify(repository, never()).salvar(servico)
    }

    @Test
    fun `deve buscar servico por id com sucesso`() {
        `when`(repository.buscarPorId(1L)).thenReturn(servico)

        val resultado = service.listarPorId(1L)

        assertNotNull(resultado)
        assertEquals(servico.id, resultado?.id)
        verify(repository, times(1)).buscarPorId(1L)
    }

    @Test
    fun `deve remover servico com sucesso`() {
        `when`(repository.existePorId(1L)).thenReturn(true)

        service.deletarPorId(1L)

        verify(repository, times(1)).deletarPorId(1L)
    }

    @Test
    fun `deve lancar excecao ao tentar remover servico inexistente`() {
        `when`(repository.existePorId(2L)).thenReturn(false)

        val exception =
                assertThrows(IllegalArgumentException::class.java) { service.deletarPorId(2L) }

        assertEquals("Serviço não encontrado para deletar.", exception.message)
        verify(repository, never()).deletarPorId(anyLong())
    }
}
