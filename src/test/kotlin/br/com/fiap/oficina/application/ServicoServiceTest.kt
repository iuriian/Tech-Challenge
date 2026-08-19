package br.com.fiap.oficina.application

import br.com.fiap.oficina.anyObject
import br.com.fiap.oficina.application.service.PecaServicoComando
import br.com.fiap.oficina.application.service.ServicoComando
import br.com.fiap.oficina.application.service.ServicoService
import br.com.fiap.oficina.domain.entity.Cliente
import br.com.fiap.oficina.domain.entity.Funcionario
import br.com.fiap.oficina.domain.entity.Peca
import br.com.fiap.oficina.domain.entity.PecaServico
import br.com.fiap.oficina.domain.entity.OrdemServico
import br.com.fiap.oficina.domain.entity.Veiculo
import br.com.fiap.oficina.domain.enum.Cargo
import br.com.fiap.oficina.domain.enum.OrdemServicoStatus
import br.com.fiap.oficina.domain.repository.ClienteRepository
import br.com.fiap.oficina.domain.repository.FuncionarioRepository
import br.com.fiap.oficina.domain.repository.PecaRepository
import br.com.fiap.oficina.domain.repository.OrdemServicoRepository
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
import java.time.Instant

@ExtendWith(MockitoExtension::class)
class ServicoServiceTest {
    @Mock lateinit var repository: OrdemServicoRepository

    @Mock lateinit var clienteRepository: ClienteRepository

    @Mock lateinit var veiculoRepository: VeiculoRepository

    @Mock lateinit var pecaRepository: PecaRepository

    @Mock lateinit var funcionarioRepository: FuncionarioRepository

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
    private lateinit var funcionarioId: Id
    private lateinit var funcionario: Funcionario

    @BeforeEach
    fun setup() {
        servicoId = Id.generate()
        clienteId = Id.generate()
        veiculoId = Id.generate()

        cliente =
            Cliente(
                id = clienteId,
                nome = "Cliente Teste",
                documento = Documento.cpf("39053344705"),
                email = "cliente@teste.com",
            )

        veiculo =
            Veiculo(
                id = veiculoId,
                marca = "Volkswagen",
                nome = "Gol",
                modelo = "Gol 1.6",
                ano = "2020",
                placa = "ABC1D23",
                motorista = cliente,
            )

        funcionarioId = Id.generate()
        funcionario =
            Funcionario(
                id = funcionarioId,
                nome = "Funcionario Teste",
                cargo = Cargo.MECANICO,
            )

        pecaId1 = Id.fromString("00000000-0000-0000-0000-000000000001")
        pecaId2 = Id.fromString("00000000-0000-0000-0000-000000000002")
        peca1 = criarPeca(pecaId1, "PEC001")
        peca2 = criarPeca(pecaId2, "PEC002")
    }

    @Test
    fun `deve salvar servico com sucesso`() {
        val quantidade1 = BigDecimal("2")
        val quantidade2 = BigDecimal("3")
        val pecasEsperadas =
            listOf(
                PecaServico.criar(peca1, quantidade1),
                PecaServico.criar(peca2, quantidade2),
            )
        val esperado =
            OrdemServico(
                id = servicoId,
                descricao = "Troca de Óleo",
                status = OrdemServicoStatus.RECEBIDA,
                funcionario = funcionario,
                cliente = cliente,
                veiculo = veiculo,
                pecas = pecasEsperadas,
            )

        `when`(repository.buscarPorId(servicoId)).thenReturn(esperado)
        `when`(clienteRepository.buscarPorId(clienteId)).thenReturn(cliente)
        `when`(veiculoRepository.buscarPorId(veiculoId)).thenReturn(veiculo)
        `when`(funcionarioRepository.buscarPorId(funcionarioId)).thenReturn(funcionario)
        `when`(pecaRepository.buscarPorId(pecaId1)).thenReturn(peca1)
        `when`(pecaRepository.buscarPorId(pecaId2)).thenReturn(peca2)
        `when`(repository.salvar(anyObject())).thenReturn(esperado)

        val resultado =
            service.salvar(
                ServicoComando(
                    id = servicoId,
                    descricao = "Troca de Óleo",
                    funcionarioId = funcionarioId,
                    status = OrdemServicoStatus.RECEBIDA,
                    clienteId = clienteId,
                    veiculoId = veiculoId,
                    pecas =
                        listOf(
                            PecaServicoComando(pecaId1, quantidade1),
                            PecaServicoComando(pecaId2, quantidade2),
                        ),
                ),
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
                        funcionarioId = funcionarioId,
                        clienteId = clienteId,
                        veiculoId = veiculoId,
                        pecas =
                            listOf(
                                PecaServicoComando(pecaId1, BigDecimal("2")),
                                PecaServicoComando(pecaId2, BigDecimal("3")),
                            ),
                    ),
                )
            }

        assertEquals("Cliente não encontrado com o ID: $clienteId", exception.message)
        verifyNoInteractions(repository)
    }

    @Test
    fun `deve buscar servico por id com sucesso`() {
        val ordemServico =
            OrdemServico(
                id = servicoId,
                descricao = "Troca de Óleo",
                funcionario = funcionario,
                cliente = cliente,
                veiculo = veiculo,
            )
        `when`(repository.buscarPorId(servicoId)).thenReturn(ordemServico)

        val resultado = service.listarPorId(servicoId)

        assertNotNull(resultado)
        assertEquals(ordemServico.id, resultado?.id)
        verify(repository, times(1)).buscarPorId(servicoId)
    }

    @Test
    fun `deve obter orcamento totalizando valor das pecas`() {
        val ordemServico =
            OrdemServico(
                id = servicoId,
                descricao = "Troca de Óleo",
                funcionario = funcionario,
                cliente = cliente,
                veiculo = veiculo,
                pecas =
                    listOf(
                        PecaServico.criar(peca1, BigDecimal("2")), // 10 * 2 = 20
                        PecaServico.criar(peca2, BigDecimal("3")), // 10 * 3 = 30
                    ),
            )
        `when`(repository.buscarPorId(servicoId)).thenReturn(ordemServico)

        val orcamento = service.obterOrcamento(servicoId)

        assertEquals(servicoId, orcamento.ordemServicoId)
        assertEquals(2, orcamento.itens.size)
        assertEquals(0, BigDecimal("50").compareTo(orcamento.valorTotal))
        verify(repository, times(1)).buscarPorId(servicoId)
    }

    @Test
    fun `deve lancar excecao ao obter orcamento de servico inexistente`() {
        `when`(repository.buscarPorId(servicoId)).thenReturn(null)

        val exception =
            assertThrows(IllegalArgumentException::class.java) {
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
        val idInexistente = Id.generate()
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
        "FINALIZADA, ENTREGUE",
    )
    fun `avancarStatus deve seguir o fluxo principal definido pelo dominio`(
        de: OrdemServicoStatus,
        esperado: OrdemServicoStatus,
    ) {
        val atual = servicoComStatus(de)
        `when`(repository.buscarPorId(servicoId)).thenReturn(atual)
        `when`(repository.salvar(anyObject())).thenAnswer { it.getArgument<OrdemServico>(0) }

        val resultado = service.avancarStatus(servicoId)

        assertEquals(esperado, resultado.status)
        verify(repository, times(1)).salvar(anyObject())
    }

    @ParameterizedTest
    @EnumSource(value = OrdemServicoStatus::class, names = ["ENTREGUE", "CANCELADA"])
    fun `avancarStatus deve falhar em estado final`(status: OrdemServicoStatus) {
        `when`(repository.buscarPorId(servicoId)).thenReturn(servicoComStatus(status))

        assertThrows(IllegalStateException::class.java) { service.avancarStatus(servicoId) }
        verify(repository, never()).salvar(anyObject())
    }

    @Test
    fun `avancarStatus deve lancar excecao quando servico nao existe`() {
        `when`(repository.buscarPorId(servicoId)).thenReturn(null)

        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                service.avancarStatus(servicoId)
            }

        assertEquals("Serviço não encontrado com o ID: $servicoId", exception.message)
        verify(repository, never()).salvar(anyObject())
    }

    @ParameterizedTest
    @EnumSource(value = OrdemServicoStatus::class, names = ["EM_EXECUCAO", "CANCELADA"])
    fun `alterarStatus deve permitir saidas de AGUARDANDO_APROVACAO`(alvo: OrdemServicoStatus) {
        val atual = servicoComStatus(OrdemServicoStatus.AGUARDANDO_APROVACAO)
        `when`(repository.buscarPorId(servicoId)).thenReturn(atual)
        `when`(repository.salvar(anyObject())).thenAnswer { it.getArgument<OrdemServico>(0) }

        val resultado = service.alterarStatus(servicoId, alvo)

        assertEquals(alvo, resultado.status)
    }

    @ParameterizedTest
    @CsvSource(
        "RECEBIDA, EM_EXECUCAO",
        "RECEBIDA, CANCELADA",
        "AGUARDANDO_APROVACAO, FINALIZADA",
        "EM_EXECUCAO, CANCELADA",
        "ENTREGUE, CANCELADA",
    )
    fun `alterarStatus deve rejeitar transicoes invalidas`(
        de: OrdemServicoStatus,
        alvo: OrdemServicoStatus,
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
            service.alterarStatus(servicoId, OrdemServicoStatus.EM_DIAGNOSTICO)
        }
        verify(repository, never()).salvar(anyObject())
    }

    @Test
    fun `listarPorCliente deve retornar servicos do cliente`() {
        `when`(repository.listarPorCliente(clienteId)).thenReturn(listOf(servicoComStatus(OrdemServicoStatus.RECEBIDA)))

        val resultado = service.listarPorCliente(clienteId)

        assertEquals(1, resultado.size)
        assertEquals(clienteId, resultado.first().cliente.id)
        verify(repository).listarPorCliente(clienteId)
    }

    @Test
    fun `listarPorCliente deve retornar lista vazia quando cliente nao tem servicos`() {
        `when`(repository.listarPorCliente(clienteId)).thenReturn(emptyList())

        val resultado = service.listarPorCliente(clienteId)

        assertTrue(resultado.isEmpty())
    }

    @Test
    fun `calcularTempoMedioExecucao deve retornar null quando nao ha servicos finalizados`() {
        `when`(repository.listarTodos()).thenReturn(listOf(servicoComStatus(OrdemServicoStatus.EM_EXECUCAO)))

        val resultado = service.calcularTempoMedioExecucao()

        assertEquals(0, resultado.totalServicosFinalizados)
        assertNull(resultado.tempoMedioMinutos)
    }

    @Test
    fun `calcularTempoMedioExecucao deve calcular media em minutos`() {
        val inicio = Instant.parse("2025-01-01T08:00:00Z")
        val fim1 = Instant.parse("2025-01-01T10:00:00Z") // 120 min
        val fim2 = Instant.parse("2025-01-01T11:00:00Z") // 180 min (inicio até fim2)
        val s1 =
            servicoComStatus(OrdemServicoStatus.FINALIZADA)
                .copy(dataInicioExecucao = inicio, dataFinalizacao = fim1)
        val s2 =
            servicoComStatus(OrdemServicoStatus.FINALIZADA)
                .copy(dataInicioExecucao = inicio, dataFinalizacao = fim2)
        `when`(repository.listarTodos()).thenReturn(listOf(s1, s2))

        val resultado = service.calcularTempoMedioExecucao()

        assertEquals(2, resultado.totalServicosFinalizados)
        assertEquals(150.0, resultado.tempoMedioMinutos)
    }

    @Test
    fun `calcularTempoMedioExecucao deve ignorar servicos sem dataInicioExecucao`() {
        val inicio = Instant.parse("2025-01-01T08:00:00Z")
        val fim = Instant.parse("2025-01-01T09:00:00Z") // 60 min
        val completo =
            servicoComStatus(OrdemServicoStatus.FINALIZADA)
                .copy(dataInicioExecucao = inicio, dataFinalizacao = fim)
        val semInicio =
            servicoComStatus(OrdemServicoStatus.FINALIZADA)
                .copy(dataInicioExecucao = null, dataFinalizacao = fim)
        `when`(repository.listarTodos()).thenReturn(listOf(completo, semInicio))

        val resultado = service.calcularTempoMedioExecucao()

        assertEquals(1, resultado.totalServicosFinalizados)
        assertEquals(60.0, resultado.tempoMedioMinutos)
    }

    private fun servicoComStatus(status: OrdemServicoStatus): OrdemServico =
        OrdemServico(
            id = servicoId,
            descricao = "Troca de Óleo",
            status = status,
            funcionario = funcionario,
            cliente = cliente,
            veiculo = veiculo,
        )

    private fun criarPeca(
        id: Id,
        codigo: String,
    ): Peca =
        Peca(
            id = id,
            codigo = codigo,
            nome = "Peça Teste",
            precoDeVenda = BigDecimal.TEN,
        )
}
