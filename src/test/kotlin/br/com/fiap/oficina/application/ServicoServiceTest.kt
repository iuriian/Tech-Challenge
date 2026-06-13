package br.com.fiap.oficina.application

import br.com.fiap.oficina.application.service.ServicoComando
import br.com.fiap.oficina.application.service.ServicoService
import br.com.fiap.oficina.domain.entity.Cliente
import br.com.fiap.oficina.domain.entity.Peca
import br.com.fiap.oficina.domain.entity.Servico
import br.com.fiap.oficina.domain.entity.Veiculo
import br.com.fiap.oficina.domain.enum.ServicoStatus
import br.com.fiap.oficina.domain.repository.ClienteRepository
import br.com.fiap.oficina.domain.repository.PecaRepository
import br.com.fiap.oficina.domain.repository.ServicoRepository
import br.com.fiap.oficina.domain.repository.VeiculoRepository
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
import java.math.BigDecimal
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class ServicoServiceTest {

    @Mock lateinit var repository: ServicoRepository

    @Mock lateinit var clienteRepository: ClienteRepository

    @Mock lateinit var veiculoRepository: VeiculoRepository

    @Mock lateinit var pecaRepository: PecaRepository

    @InjectMocks lateinit var service: ServicoService

    private lateinit var servicoId: Id
    private lateinit var clienteId: Id
    private lateinit var veiculoId: Id
    private lateinit var cliente: Cliente
    private lateinit var veiculo: Veiculo
    private lateinit var peca1: Peca
    private lateinit var peca2: Peca
    private lateinit var pecaId1: Id
    private lateinit var pecaId2: Id

    @BeforeEach
    fun setup() {
        servicoId = Id.gerar()
        clienteId = Id.gerar()
        veiculoId = Id.gerar()

        cliente = Cliente(
            id = clienteId,
            nome = "Cliente Teste",
            documento = Documento.cpf("39053344705"),
            email = "cliente@teste.com"
        )

        veiculo = Veiculo(
            id = veiculoId,
            marca = "Volkswagen",
            nome = "Gol",
            modelo = "Gol 1.6",
            ano = "2020",
            placa = "ABC1D23",
            motorista = cliente
        )

        pecaId1 = Id.from(UUID.fromString("00000000-0000-0000-0000-000000000001"))
        pecaId2 = Id.from(UUID.fromString("00000000-0000-0000-0000-000000000002"))
        peca1 = criarPeca(pecaId1, "PEC001")
        peca2 = criarPeca(pecaId2, "PEC002")
    }

    @Test
    fun `deve salvar servico com sucesso`() {
        val esperado = Servico(
            id = servicoId,
            descricao = "Troca de Óleo",
            status = ServicoStatus.RECEBIDA,
            funcionarioId = 1L,
            cliente = cliente,
            veiculo = veiculo,
            pecas = listOf(peca1, peca2)
        )

        `when`(clienteRepository.buscarPorId(clienteId)).thenReturn(cliente)
        `when`(veiculoRepository.buscarPorId(veiculoId)).thenReturn(veiculo)
        `when`(pecaRepository.buscarPorId(pecaId1)).thenReturn(peca1)
        `when`(pecaRepository.buscarPorId(pecaId2)).thenReturn(peca2)
        `when`(repository.salvar(esperado)).thenReturn(esperado)

        val resultado = service.salvar(
            ServicoComando(
                id = servicoId,
                descricao = "Troca de Óleo",
                funcionarioId = 1L,
                status = ServicoStatus.RECEBIDA,
                clienteId = clienteId,
                veiculoId = veiculoId,
                pecasIds = listOf(pecaId1, pecaId2)
            )
        )

        assertNotNull(resultado)
        assertEquals(servicoId, resultado.id)
        assertEquals(cliente, resultado.cliente)
        assertEquals(listOf(peca1, peca2), resultado.pecas)
        verify(repository, times(1)).salvar(esperado)
    }

    @Test
    fun `deve lancar excecao ao salvar servico com cliente inexistente`() {
        `when`(clienteRepository.buscarPorId(clienteId)).thenReturn(null)

        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                service.salvar(
                    ServicoComando(
                        id = servicoId,
                        descricao = "Troca de Óleo",
                        funcionarioId = 1L,
                        clienteId = clienteId,
                        veiculoId = veiculoId,
                        pecasIds = listOf(pecaId1, pecaId2)
                    )
                )
            }

        assertEquals("Cliente não encontrado com o ID: $clienteId", exception.message)
        verifyNoInteractions(repository)
    }

    @Test
    fun `deve buscar servico por id com sucesso`() {
        val servico = Servico(
            id = servicoId,
            descricao = "Troca de Óleo",
            funcionarioId = 1L,
            cliente = cliente,
            veiculo = veiculo
        )
        `when`(repository.buscarPorId(servicoId)).thenReturn(servico)

        val resultado = service.listarPorId(servicoId)

        assertNotNull(resultado)
        assertEquals(servico.id, resultado?.id)
        verify(repository, times(1)).buscarPorId(servicoId)
    }

    @Test
    fun `deve remover servico com sucesso`() {
        `when`(repository.existePorId(servicoId)).thenReturn(true)

        service.deletarPorId(servicoId)

        verify(repository, times(1)).deletarPorId(servicoId)
    }

    @Test
    fun `deve lancar excecao ao tentar remover servico inexistente`() {
        val idInexistente = Id.gerar()
        `when`(repository.existePorId(idInexistente)).thenReturn(false)

        val exception =
            assertThrows(IllegalArgumentException::class.java) { service.deletarPorId(idInexistente) }

        assertEquals("Serviço não encontrado para deletar.", exception.message)
        verify(repository, never()).deletarPorId(idInexistente)
    }

    private fun criarPeca(id: Id, codigo: String): Peca =
        Peca(
            id = id,
            codigo = codigo,
            nome = "Peça Teste",
            precoDeVenda = BigDecimal.TEN
        )
}
