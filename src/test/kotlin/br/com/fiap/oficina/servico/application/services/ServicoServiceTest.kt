package br.com.fiap.oficina.servico.application.services

import br.com.fiap.oficina.anyObject
import br.com.fiap.oficina.domain.entity.Cliente
import br.com.fiap.oficina.domain.entity.Funcionario
import br.com.fiap.oficina.domain.entity.Peca
import br.com.fiap.oficina.domain.entity.Veiculo
import br.com.fiap.oficina.domain.enum.Cargo
import br.com.fiap.oficina.domain.repository.ClienteRepository
import br.com.fiap.oficina.domain.repository.FuncionarioRepository
import br.com.fiap.oficina.domain.repository.PecaRepository
import br.com.fiap.oficina.domain.repository.VeiculoRepository
import br.com.fiap.oficina.domain.valueobject.Documento
import br.com.fiap.oficina.domain.valueobject.Id
import br.com.fiap.oficina.servico.application.dto.PecaServicoComando
import br.com.fiap.oficina.servico.application.dto.ServicoComando
import br.com.fiap.oficina.servico.domain.entities.OrdemServico
import br.com.fiap.oficina.servico.domain.entities.PecaServico
import br.com.fiap.oficina.servico.domain.enums.OrdemServicoStatus
import br.com.fiap.oficina.servico.domain.repositories.OrdemServicoRepository
import br.com.fiap.oficina.servico.domain.repositories.SequenciaOrdemServicoRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.EnumSource
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.never
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.math.BigDecimal
import java.time.Instant

@ExtendWith(MockitoExtension::class)
class ServicoServiceTest {
    @Mock
    lateinit var repository: OrdemServicoRepository

    @Mock
    lateinit var clienteRepository: ClienteRepository

    @Mock
    lateinit var veiculoRepository: VeiculoRepository

    @Mock
    lateinit var pecaRepository: PecaRepository

    @Mock
    lateinit var funcionarioRepository: FuncionarioRepository

    @Mock
    lateinit var sequenciaOrdemServicoRepository: SequenciaOrdemServicoRepository

    @InjectMocks
    lateinit var service: ServicoService

    private lateinit var ordemServicoId: Id
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
        ordemServicoId = Id.generate()
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
    fun `deve gerar numero da ordem de servico ao criar nova ordem`() {
        `when`(clienteRepository.buscarPorId(clienteId)).thenReturn(cliente)
        `when`(veiculoRepository.buscarPorId(veiculoId)).thenReturn(veiculo)
        `when`(funcionarioRepository.buscarPorId(funcionarioId)).thenReturn(funcionario)
        `when`(sequenciaOrdemServicoRepository.obterProximoValor()).thenReturn(123L)
        `when`(repository.salvar(anyObject())).thenAnswer {
            it.getArgument<OrdemServico>(0)
        }

        val resultado =
            service.salvar(
                ServicoComando(
                    descricao = "Troca de Óleo",
                    funcionarioId = funcionarioId,
                    clienteId = clienteId,
                    veiculoId = veiculoId,
                ),
            )

        assertNotNull(resultado.osNumber)
        assertTrue(
            resultado.osNumber!!.valor.matches(
                Regex("""OS-\d{4}-000123"""),
            ),
        )

        assertEquals(funcionarioId, resultado.funcionarioId)
        assertEquals(clienteId, resultado.clienteId)
        assertEquals(veiculoId, resultado.veiculoId)

        verify(sequenciaOrdemServicoRepository, times(1))
            .obterProximoValor()
        verify(repository, times(1))
            .salvar(anyObject())
    }

    @Test
    fun `deve salvar ordem de servico com sucesso`() {
        val quantidade1 = BigDecimal("2")
        val quantidade2 = BigDecimal("3")
        val pecasEsperadas =
            listOf(
                PecaServico.criar(peca1, quantidade1),
                PecaServico.criar(peca2, quantidade2),
            )

        val esperado =
            OrdemServico(
                id = ordemServicoId,
                descricao = "Troca de Óleo",
                status = OrdemServicoStatus.RECEBIDA,
                funcionarioId = funcionarioId,
                clienteId = clienteId,
                veiculoId = veiculoId,
                pecas = pecasEsperadas,
            )

        `when`(repository.buscarPorId(ordemServicoId)).thenReturn(esperado)
        `when`(clienteRepository.buscarPorId(clienteId)).thenReturn(cliente)
        `when`(veiculoRepository.buscarPorId(veiculoId)).thenReturn(veiculo)
        `when`(funcionarioRepository.buscarPorId(funcionarioId)).thenReturn(funcionario)
        `when`(pecaRepository.buscarPorId(pecaId1)).thenReturn(peca1)
        `when`(pecaRepository.buscarPorId(pecaId2)).thenReturn(peca2)
        `when`(repository.salvar(anyObject())).thenReturn(esperado)

        val resultado =
            service.salvar(
                ServicoComando(
                    id = ordemServicoId,
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
        assertEquals(ordemServicoId, resultado.id)
        assertEquals(clienteId, resultado.clienteId)
        assertEquals(pecasEsperadas, resultado.pecas)
        verify(repository, times(1)).salvar(esperado)
        verify(sequenciaOrdemServicoRepository, never())
            .obterProximoValor()
    }

    @Test
    fun `deve lancar excecao ao salvar ordem de servico com cliente inexistente`() {
        `when`(clienteRepository.buscarPorId(clienteId)).thenReturn(null)

        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                service.salvar(
                    ServicoComando(
                        id = ordemServicoId,
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
    fun `deve buscar ordem de servico por id com sucesso`() {
        val ordemServico =
            OrdemServico(
                id = ordemServicoId,
                descricao = "Troca de Óleo",
                funcionarioId = funcionarioId,
                clienteId = clienteId,
                veiculoId = veiculoId,
            )

        `when`(repository.buscarPorId(ordemServicoId)).thenReturn(ordemServico)

        val resultado = service.listarPorId(ordemServicoId)

        assertNotNull(resultado)
        assertEquals(ordemServico.id, resultado?.id)
        verify(repository, times(1)).buscarPorId(ordemServicoId)
    }

    @Test
    fun `deve obter orcamento totalizando valor das pecas`() {
        val ordemServico =
            OrdemServico(
                id = ordemServicoId,
                descricao = "Troca de Óleo",
                funcionarioId = funcionarioId,
                clienteId = clienteId,
                veiculoId = veiculoId,
                pecas =
                listOf(
                    PecaServico.criar(peca1, BigDecimal("2")),
                    PecaServico.criar(peca2, BigDecimal("3")),
                ),
            )

        `when`(repository.buscarPorId(ordemServicoId)).thenReturn(ordemServico)

        val orcamento = service.obterOrcamento(ordemServicoId)

        assertEquals(ordemServicoId, orcamento.ordemServicoId)
        assertEquals(2, orcamento.itens.size)
        assertEquals(0, BigDecimal("50").compareTo(orcamento.valorTotal))
        verify(repository, times(1)).buscarPorId(ordemServicoId)
    }

    @Test
    fun `deve lancar excecao ao obter orcamento de ordem de servico inexistente`() {
        `when`(repository.buscarPorId(ordemServicoId)).thenReturn(null)

        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                service.obterOrcamento(ordemServicoId)
            }

        assertEquals("Serviço não encontrado com o ID: $ordemServicoId", exception.message)
    }

    @Test
    fun `deve remover ordem de servico com sucesso`() {
        `when`(repository.existePorId(ordemServicoId)).thenReturn(true)

        service.deletarPorId(ordemServicoId)

        verify(repository, times(1)).deletarPorId(ordemServicoId)
    }

    @Test
    fun `deve lancar excecao ao tentar remover ordem de servico inexistente`() {
        val idInexistente = Id.generate()
        `when`(repository.existePorId(idInexistente)).thenReturn(false)

        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                service.deletarPorId(idInexistente)
            }

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
    fun `avancarStatus deve seguir a ordem de declaracao do enum`(
        de: OrdemServicoStatus,
        esperado: OrdemServicoStatus,
    ) {
        val atual = ordemServicoComStatus(de)
        `when`(repository.buscarPorId(ordemServicoId)).thenReturn(atual)
        `when`(repository.salvar(anyObject())).thenAnswer {
            it.getArgument<OrdemServico>(0)
        }

        val resultado = service.avancarStatus(ordemServicoId)

        assertEquals(esperado, resultado.status)
        verify(repository, times(1)).salvar(anyObject())
    }

    @ParameterizedTest
    @EnumSource(
        value = OrdemServicoStatus::class,
        names = ["ENTREGUE", "CANCELADA"],
    )
    fun `avancarStatus deve falhar em estado final`(status: OrdemServicoStatus) {
        `when`(
            repository.buscarPorId(ordemServicoId),
        ).thenReturn(ordemServicoComStatus(status))

        assertThrows(IllegalStateException::class.java) {
            service.avancarStatus(ordemServicoId)
        }

        verify(repository, never()).salvar(anyObject())
    }

    @Test
    fun `avancarStatus deve lancar excecao quando ordem de servico nao existe`() {
        `when`(repository.buscarPorId(ordemServicoId)).thenReturn(null)

        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                service.avancarStatus(ordemServicoId)
            }

        assertEquals("Serviço não encontrado com o ID: $ordemServicoId", exception.message)
        verify(repository, never()).salvar(anyObject())
    }

    @ParameterizedTest
    @EnumSource(
        value = OrdemServicoStatus::class,
        names = ["EM_EXECUCAO", "CANCELADA"],
    )
    fun `alterarStatus deve permitir saidas de AGUARDANDO_APROVACAO`(alvo: OrdemServicoStatus) {
        val atual =
            ordemServicoComStatus(
                OrdemServicoStatus.AGUARDANDO_APROVACAO,
            )

        `when`(repository.buscarPorId(ordemServicoId)).thenReturn(atual)
        `when`(repository.salvar(anyObject())).thenAnswer {
            it.getArgument<OrdemServico>(0)
        }

        val resultado = service.alterarStatus(ordemServicoId, alvo)

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
    fun `alterarStatus deve rejeitar transicoes invalidas`(de: OrdemServicoStatus, alvo: OrdemServicoStatus) {
        `when`(
            repository.buscarPorId(ordemServicoId),
        ).thenReturn(ordemServicoComStatus(de))

        assertThrows(IllegalStateException::class.java) {
            service.alterarStatus(ordemServicoId, alvo)
        }

        verify(repository, never()).salvar(anyObject())
    }

    @Test
    fun `alterarStatus deve lancar excecao quando ordem de servico nao existe`() {
        `when`(repository.buscarPorId(ordemServicoId)).thenReturn(null)

        assertThrows(IllegalArgumentException::class.java) {
            service.alterarStatus(
                ordemServicoId,
                OrdemServicoStatus.EM_DIAGNOSTICO,
            )
        }

        verify(repository, never()).salvar(anyObject())
    }

    @Test
    fun `listarPorCliente deve retornar ordens de servico do cliente`() {
        `when`(
            repository.listarPorCliente(clienteId),
        ).thenReturn(
            listOf(
                ordemServicoComStatus(OrdemServicoStatus.RECEBIDA),
            ),
        )

        val resultado = service.listarPorCliente(clienteId)

        assertEquals(1, resultado.size)
        assertEquals(clienteId, resultado.first().clienteId)
        verify(repository).listarPorCliente(clienteId)
    }

    @Test
    fun `listarPorCliente deve retornar lista vazia quando cliente nao tem ordens de servico`() {
        `when`(repository.listarPorCliente(clienteId)).thenReturn(emptyList())

        val resultado = service.listarPorCliente(clienteId)

        assertTrue(resultado.isEmpty())
    }

    @Test
    fun `calcularTempoMedioExecucao deve retornar null quando nao ha ordens finalizadas`() {
        `when`(
            repository.listarTodos(),
        ).thenReturn(
            listOf(
                ordemServicoComStatus(OrdemServicoStatus.EM_EXECUCAO),
            ),
        )

        val resultado = service.calcularTempoMedioExecucao()

        assertEquals(0, resultado.totalServicosFinalizados)
        assertNull(resultado.tempoMedioMinutos)
    }

    @Test
    fun `calcularTempoMedioExecucao deve calcular media em minutos`() {
        val inicio = Instant.parse("2025-01-01T08:00:00Z")
        val fim1 = Instant.parse("2025-01-01T10:00:00Z")
        val fim2 = Instant.parse("2025-01-01T11:00:00Z")

        val ordemServico1 =
            ordemServicoComStatus(OrdemServicoStatus.FINALIZADA)
                .copy(
                    dataInicioExecucao = inicio,
                    dataFinalizacao = fim1,
                )

        val ordemServico2 =
            ordemServicoComStatus(OrdemServicoStatus.FINALIZADA)
                .copy(
                    dataInicioExecucao = inicio,
                    dataFinalizacao = fim2,
                )

        `when`(
            repository.listarTodos(),
        ).thenReturn(listOf(ordemServico1, ordemServico2))

        val resultado = service.calcularTempoMedioExecucao()

        assertEquals(2, resultado.totalServicosFinalizados)
        assertEquals(150.0, resultado.tempoMedioMinutos)
    }

    @Test
    fun `calcularTempoMedioExecucao deve ignorar ordens sem dataInicioExecucao`() {
        val inicio = Instant.parse("2025-01-01T08:00:00Z")
        val fim = Instant.parse("2025-01-01T09:00:00Z")

        val completa =
            ordemServicoComStatus(OrdemServicoStatus.FINALIZADA)
                .copy(
                    dataInicioExecucao = inicio,
                    dataFinalizacao = fim,
                )

        val semInicio =
            ordemServicoComStatus(OrdemServicoStatus.FINALIZADA)
                .copy(
                    dataInicioExecucao = null,
                    dataFinalizacao = fim,
                )

        `when`(
            repository.listarTodos(),
        ).thenReturn(listOf(completa, semInicio))

        val resultado = service.calcularTempoMedioExecucao()

        assertEquals(1, resultado.totalServicosFinalizados)
        assertEquals(60.0, resultado.tempoMedioMinutos)
    }

    private fun ordemServicoComStatus(status: OrdemServicoStatus): OrdemServico = OrdemServico(
        id = ordemServicoId,
        descricao = "Troca de Óleo",
        status = status,
        funcionarioId = funcionarioId,
        clienteId = clienteId,
        veiculoId = veiculoId,
    )

    private fun criarPeca(id: Id, codigo: String): Peca = Peca(
        id = id,
        codigo = codigo,
        nome = "Peça Teste",
        precoDeVenda = BigDecimal.TEN,
    )
}
