package br.com.fiap.oficina.application

import br.com.fiap.oficina.anyObject
import br.com.fiap.oficina.application.service.PecaServicoComando
import br.com.fiap.oficina.application.service.ServicoComando
import br.com.fiap.oficina.application.service.ServicoService
import br.com.fiap.oficina.domain.entity.Cliente
import br.com.fiap.oficina.domain.entity.Peca
import br.com.fiap.oficina.domain.entity.PecaServico
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
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.EnumSource
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
        val quantidade1 = BigDecimal("2")
        val quantidade2 = BigDecimal("3")
        val pecasEsperadas = listOf(
            PecaServico.criar(peca1, quantidade1),
            PecaServico.criar(peca2, quantidade2)
        )
        val esperado = Servico(
            id = servicoId,
            descricao = "Troca de Óleo",
            status = ServicoStatus.RECEBIDA,
            funcionarioId = 1L,
            cliente = cliente,
            veiculo = veiculo,
            pecas = pecasEsperadas
        )

        `when`(repository.buscarPorId(servicoId)).thenReturn(esperado)
        `when`(clienteRepository.buscarPorId(clienteId)).thenReturn(cliente)
        `when`(veiculoRepository.buscarPorId(veiculoId)).thenReturn(veiculo)
        `when`(pecaRepository.buscarPorId(pecaId1)).thenReturn(peca1)
        `when`(pecaRepository.buscarPorId(pecaId2)).thenReturn(peca2)
        `when`(repository.salvar(anyObject())).thenReturn(esperado)

        val resultado = service.salvar(
            ServicoComando(
                id = servicoId,
                descricao = "Troca de Óleo",
                funcionarioId = 1L,
                status = ServicoStatus.RECEBIDA,
                clienteId = clienteId,
                veiculoId = veiculoId,
                pecas = listOf(
                    PecaServicoComando(pecaId1, quantidade1),
                    PecaServicoComando(pecaId2, quantidade2)
                )
            )
        )

        assertNotNull(resultado)
        assertEquals(servicoId, resultado.id)
        assertEquals(cliente, resultado.cliente)
        assertEquals(pecasEsperadas, resultado.pecas)
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
                        pecas = listOf(
                            PecaServicoComando(pecaId1, BigDecimal("2")),
                            PecaServicoComando(pecaId2, BigDecimal("3"))
                        )
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
    fun `deve obter orcamento totalizando valor das pecas`() {
        val servico = Servico(
            id = servicoId,
            descricao = "Troca de Óleo",
            funcionarioId = 1L,
            cliente = cliente,
            veiculo = veiculo,
            pecas = listOf(
                PecaServico.criar(peca1, BigDecimal("2")), // 10 * 2 = 20
                PecaServico.criar(peca2, BigDecimal("3"))  // 10 * 3 = 30
            )
        )
        `when`(repository.buscarPorId(servicoId)).thenReturn(servico)

        val orcamento = service.obterOrcamento(servicoId)

        assertEquals(servicoId, orcamento.servicoId)
        assertEquals(2, orcamento.itens.size)
        assertEquals(0, BigDecimal("50").compareTo(orcamento.valorTotal))
        verify(repository, times(1)).buscarPorId(servicoId)
    }

    @Test
    fun `deve lancar excecao ao obter orcamento de servico inexistente`() {
        `when`(repository.buscarPorId(servicoId)).thenReturn(null)

        val exception = assertThrows(IllegalArgumentException::class.java) {
            service.obterOrcamento(servicoId)
        }

        assertEquals("Serviço não encontrado com o ID: $servicoId", exception.message)
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

    @ParameterizedTest
    @CsvSource(
        "RECEBIDA, EM_DIAGNOSTICO",
        "EM_DIAGNOSTICO, AGUARDANDO_APROVACAO",
        "AGUARDANDO_APROVACAO, EM_EXECUCAO",
        "EM_EXECUCAO, FINALIZADA",
        "FINALIZADA, ENTREGUE"
    )
    fun `avancarStatus deve seguir a ordem de declaracao do enum`(
        de: ServicoStatus,
        esperado: ServicoStatus
    ) {
        val atual = servicoComStatus(de)
        val salvo = atual.alterarStatus(esperado)
        `when`(repository.buscarPorId(servicoId)).thenReturn(atual)
        `when`(repository.salvar(salvo)).thenReturn(salvo)

        val resultado = service.avancarStatus(servicoId)

        assertEquals(esperado, resultado.status)
        verify(repository, times(1)).salvar(salvo)
    }

    @ParameterizedTest
    @EnumSource(value = ServicoStatus::class, names = ["ENTREGUE", "CANCELADA"])
    fun `avancarStatus deve falhar em estado final`(status: ServicoStatus) {
        `when`(repository.buscarPorId(servicoId)).thenReturn(servicoComStatus(status))

        assertThrows(IllegalStateException::class.java) { service.avancarStatus(servicoId) }
        verify(repository, never()).salvar(anyObject())
    }

    @Test
    fun `avancarStatus deve lancar excecao quando servico nao existe`() {
        `when`(repository.buscarPorId(servicoId)).thenReturn(null)

        val exception = assertThrows(IllegalArgumentException::class.java) {
            service.avancarStatus(servicoId)
        }

        assertEquals("Serviço não encontrado com o ID: $servicoId", exception.message)
        verify(repository, never()).salvar(anyObject())
    }

    @ParameterizedTest
    @EnumSource(value = ServicoStatus::class, names = ["EM_EXECUCAO", "CANCELADA"])
    fun `alterarStatus deve permitir saidas de AGUARDANDO_APROVACAO`(alvo: ServicoStatus) {
        val atual = servicoComStatus(ServicoStatus.AGUARDANDO_APROVACAO)
        val salvo = atual.alterarStatus(alvo)
        `when`(repository.buscarPorId(servicoId)).thenReturn(atual)
        `when`(repository.salvar(salvo)).thenReturn(salvo)

        val resultado = service.alterarStatus(servicoId, alvo)

        assertEquals(alvo, resultado.status)
    }

    @ParameterizedTest
    @CsvSource(
        "RECEBIDA, EM_EXECUCAO",
        "RECEBIDA, CANCELADA",
        "AGUARDANDO_APROVACAO, FINALIZADA",
        "EM_EXECUCAO, CANCELADA",
        "ENTREGUE, CANCELADA"
    )
    fun `alterarStatus deve rejeitar transicoes invalidas`(
        de: ServicoStatus,
        alvo: ServicoStatus
    ) {
        `when`(repository.buscarPorId(servicoId)).thenReturn(servicoComStatus(de))

        assertThrows(IllegalStateException::class.java) {
            service.alterarStatus(servicoId, alvo)
        }
        verify(repository, never()).salvar(anyObject())
    }

    @Test
    fun `alterarStatus deve lancar excecao quando servico nao existe`() {
        `when`(repository.buscarPorId(servicoId)).thenReturn(null)

        assertThrows(IllegalArgumentException::class.java) {
            service.alterarStatus(servicoId, ServicoStatus.EM_DIAGNOSTICO)
        }
        verify(repository, never()).salvar(anyObject())
    }

    private fun servicoComStatus(status: ServicoStatus): Servico =
        Servico(
            id = servicoId,
            descricao = "Troca de Óleo",
            status = status,
            funcionarioId = 1L,
            cliente = cliente,
            veiculo = veiculo
        )

    private fun criarPeca(id: Id, codigo: String): Peca =
        Peca(
            id = id,
            codigo = codigo,
            nome = "Peça Teste",
            precoDeVenda = BigDecimal.TEN
        )
}
