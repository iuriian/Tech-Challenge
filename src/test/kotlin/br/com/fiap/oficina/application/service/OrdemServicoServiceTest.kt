package br.com.fiap.oficina.application.service

import br.com.fiap.oficina.anyObject
import br.com.fiap.oficina.application.dto.ItemOrcamentoRequest
import br.com.fiap.oficina.application.dto.OrdemServicoRequest
import br.com.fiap.oficina.application.mapper.ItemOrcamentoMapper
import br.com.fiap.oficina.application.mapper.OrcamentoMapper
import br.com.fiap.oficina.application.mapper.OrdemServicoMapper
import br.com.fiap.oficina.application.mapper.TempoMedioExecucaoMapper
import br.com.fiap.oficina.domain.entity.Cliente
import br.com.fiap.oficina.domain.entity.Funcionario
import br.com.fiap.oficina.domain.entity.OrdemServico
import br.com.fiap.oficina.domain.entity.Peca
import br.com.fiap.oficina.domain.entity.Servico
import br.com.fiap.oficina.domain.entity.Veiculo
import br.com.fiap.oficina.domain.enum.Cargo
import br.com.fiap.oficina.domain.enum.OrdemServicoStatus
import br.com.fiap.oficina.domain.enum.TipoItemOrcamento
import br.com.fiap.oficina.domain.repository.ClienteRepository
import br.com.fiap.oficina.domain.repository.FuncionarioRepository
import br.com.fiap.oficina.domain.repository.OrdemServicoRepository
import br.com.fiap.oficina.domain.repository.PecaRepository
import br.com.fiap.oficina.domain.repository.SequenciaOrdemServicoRepository
import br.com.fiap.oficina.domain.repository.ServicoRepository
import br.com.fiap.oficina.domain.repository.VeiculoRepository
import br.com.fiap.oficina.domain.valueobject.Documento
import br.com.fiap.oficina.domain.valueobject.Id
import br.com.fiap.oficina.domain.valueobject.ItemOrcamento
import br.com.fiap.oficina.domain.valueobject.Orcamento
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
class OrdemServicoServiceTest {
    @Mock
    lateinit var repository: OrdemServicoRepository

    @Mock
    lateinit var clienteRepository: ClienteRepository

    @Mock
    lateinit var veiculoRepository: VeiculoRepository

    @Mock
    lateinit var pecaRepository: PecaRepository

    @Mock
    lateinit var servicoRepository: ServicoRepository

    @Mock
    lateinit var funcionarioRepository: FuncionarioRepository

    @Mock
    lateinit var sequenciaOrdemServicoRepository: SequenciaOrdemServicoRepository

    private lateinit var service: OrdemServicoService

    private lateinit var ordemServicoId: Id
    private lateinit var clienteId: Id
    private lateinit var veiculoId: Id
    private lateinit var funcionarioId: Id
    private lateinit var pecaId1: Id
    private lateinit var pecaId2: Id
    private lateinit var servicoId: Id

    private lateinit var cliente: Cliente
    private lateinit var veiculo: Veiculo
    private lateinit var funcionario: Funcionario
    private lateinit var peca1: Peca
    private lateinit var peca2: Peca
    private lateinit var servicoCatalogo: Servico

    @BeforeEach
    fun setup() {
        ordemServicoId = Id.generate()
        clienteId = Id.generate()
        veiculoId = Id.generate()
        funcionarioId = Id.generate()

        pecaId1 =
            Id.fromString(
                "00000000-0000-0000-0000-000000000001",
            )

        pecaId2 =
            Id.fromString(
                "00000000-0000-0000-0000-000000000002",
            )

        servicoId =
            Id.fromString(
                "00000000-0000-0000-0000-000000000003",
            )

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

        funcionario =
            Funcionario(
                id = funcionarioId,
                nome = "Funcionario Teste",
                cargo = Cargo.MECANICO,
            )

        peca1 = criarPeca(pecaId1, "PEC001")
        peca2 = criarPeca(pecaId2, "PEC002")

        servicoCatalogo =
            Servico(
                id = servicoId,
                descricao = "Troca de óleo",
                valor = BigDecimal("150.00"),
            )

        val itemOrcamentoMapper = ItemOrcamentoMapper()

        service =
            OrdemServicoService(
                repository = repository,
                clienteRepository = clienteRepository,
                veiculoRepository = veiculoRepository,
                pecaRepository = pecaRepository,
                servicoRepository = servicoRepository,
                funcionarioRepository = funcionarioRepository,
                sequenciaOrdemServicoRepository = sequenciaOrdemServicoRepository,
                ordemServicoMapper = OrdemServicoMapper(itemOrcamentoMapper),
                orcamentoMapper = OrcamentoMapper(itemOrcamentoMapper),
                tempoMedioExecucaoMapper = TempoMedioExecucaoMapper(),
            )
    }

    @Test
    fun `deve gerar numero da ordem de servico ao criar nova ordem`() {
        prepararEntidadesRelacionadas()

        `when`(
            sequenciaOrdemServicoRepository.obterProximoValor(),
        ).thenReturn(123L)

        var ordemSalva: OrdemServico? = null

        `when`(
            repository.salvar(anyObject()),
        ).thenAnswer {
            it.getArgument<OrdemServico>(0).also { ordem ->
                ordemSalva = ordem
            }
        }

        val resultado =
            service.criar(
                request(),
            )

        val ordemPersistida = requireNotNull(ordemSalva)

        assertNotNull(ordemPersistida.osNumber)
        assertTrue(
            ordemPersistida.osNumber!!.valor.matches(
                Regex("""OS-\d{4}-000123"""),
            ),
        )

        assertEquals(funcionarioId.valor.toString(), resultado.funcionarioId)
        assertEquals(clienteId.valor.toString(), resultado.clienteId)
        assertEquals(veiculoId.valor.toString(), resultado.veiculoId)
        assertTrue(resultado.itens.isEmpty())

        verify(
            sequenciaOrdemServicoRepository,
            times(1),
        ).obterProximoValor()
    }

    @Test
    fun `deve atualizar ordem de servico com itens de peca`() {
        val existente =
            ordemServicoComStatus(
                OrdemServicoStatus.RECEBIDA,
            )

        `when`(
            repository.buscarPorId(ordemServicoId),
        ).thenReturn(existente)

        prepararEntidadesRelacionadas()

        `when`(
            pecaRepository.buscarPorId(pecaId1),
        ).thenReturn(peca1)

        `when`(
            pecaRepository.buscarPorId(pecaId2),
        ).thenReturn(peca2)

        `when`(
            repository.salvar(anyObject()),
        ).thenAnswer {
            it.getArgument<OrdemServico>(0)
        }

        val resultado =
            service.atualizar(
                id = ordemServicoId.valor.toString(),
                request =
                request(
                    descricao = "Troca de Óleo",
                    itens =
                    listOf(
                        itemRequest(
                            TipoItemOrcamento.PECA,
                            pecaId1,
                            BigDecimal("2"),
                        ),
                        itemRequest(
                            TipoItemOrcamento.PECA,
                            pecaId2,
                            BigDecimal("3"),
                        ),
                    ),
                ),
            )

        assertEquals(ordemServicoId.valor, resultado.id)
        assertEquals("Troca de Óleo", resultado.descricao)
        assertEquals(2, resultado.itens.size)

        verify(
            sequenciaOrdemServicoRepository,
            never(),
        ).obterProximoValor()
    }

    @Test
    fun `deve criar ordem de servico com item de servico do catalogo`() {
        prepararEntidadesRelacionadas()

        `when`(
            servicoRepository.buscarPorId(servicoId),
        ).thenReturn(servicoCatalogo)

        `when`(
            sequenciaOrdemServicoRepository.obterProximoValor(),
        ).thenReturn(123L)

        `when`(
            repository.salvar(anyObject()),
        ).thenAnswer {
            it.getArgument<OrdemServico>(0)
        }

        val resultado =
            service.criar(
                request(
                    descricao = "Revisão",
                    itens =
                    listOf(
                        itemRequest(
                            TipoItemOrcamento.SERVICO,
                            servicoId,
                        ),
                    ),
                ),
            )

        val item = resultado.itens.single()

        assertEquals(TipoItemOrcamento.SERVICO, item.tipo)
        assertEquals(servicoId.valor, item.referenciaId)
        assertEquals("Troca de óleo", item.descricao)
        assertEquals(BigDecimal("150.00"), item.valorUnitario)
        assertEquals(BigDecimal.ONE, item.quantidade)
    }

    @Test
    fun `deve lancar excecao ao criar ordem com cliente inexistente`() {
        `when`(
            clienteRepository.buscarPorId(clienteId),
        ).thenReturn(null)

        val exception =
            assertThrows(
                IllegalArgumentException::class.java,
            ) {
                service.criar(
                    request(),
                )
            }

        assertEquals(
            "Cliente não encontrado com o ID: $clienteId",
            exception.message,
        )

        verifyNoInteractions(repository)
    }

    @Test
    fun `deve lancar excecao ao criar ordem com funcionario inexistente`() {
        `when`(
            clienteRepository.buscarPorId(clienteId),
        ).thenReturn(cliente)

        `when`(
            funcionarioRepository.buscarPorId(funcionarioId),
        ).thenReturn(null)

        val exception =
            assertThrows(
                IllegalArgumentException::class.java,
            ) {
                service.criar(
                    request(),
                )
            }

        assertEquals(
            "Funcionário não encontrado com o ID: $funcionarioId",
            exception.message,
        )

        verifyNoInteractions(repository)
    }

    @Test
    fun `deve lancar excecao ao criar ordem com veiculo inexistente`() {
        `when`(
            clienteRepository.buscarPorId(clienteId),
        ).thenReturn(cliente)

        `when`(
            funcionarioRepository.buscarPorId(funcionarioId),
        ).thenReturn(funcionario)

        `when`(
            veiculoRepository.buscarPorId(veiculoId),
        ).thenReturn(null)

        val exception =
            assertThrows(
                IllegalArgumentException::class.java,
            ) {
                service.criar(
                    request(),
                )
            }

        assertEquals(
            "Veículo não encontrado com o ID: $veiculoId",
            exception.message,
        )

        verifyNoInteractions(repository)
    }

    @Test
    fun `deve lancar excecao ao atualizar ordem inexistente`() {
        prepararEntidadesRelacionadas()

        `when`(
            repository.buscarPorId(ordemServicoId),
        ).thenReturn(null)

        val exception =
            assertThrows(
                IllegalArgumentException::class.java,
            ) {
                service.atualizar(
                    ordemServicoId.valor.toString(),
                    request(),
                )
            }

        assertEquals(
            "Ordem de serviço não encontrada com o ID: $ordemServicoId",
            exception.message,
        )

        verify(
            repository,
            never(),
        ).salvar(anyObject())

        verify(
            sequenciaOrdemServicoRepository,
            never(),
        ).obterProximoValor()
    }

    @Test
    fun `deve buscar ordem de servico por id`() {
        val ordemServico =
            ordemServicoComStatus(
                OrdemServicoStatus.RECEBIDA,
            )

        `when`(
            repository.buscarPorId(ordemServicoId),
        ).thenReturn(ordemServico)

        val resultado =
            service.listarPorId(
                ordemServicoId.valor.toString(),
            )

        assertNotNull(resultado)
        assertEquals(
            ordemServicoId.valor,
            resultado?.id,
        )

        verify(repository).buscarPorId(ordemServicoId)
    }

    @Test
    fun `deve listar todas as ordens de servico`() {
        `when`(
            repository.listarTodos(),
        ).thenReturn(
            listOf(
                ordemServicoComStatus(
                    OrdemServicoStatus.RECEBIDA,
                ),
            ),
        )

        val resultado = service.listarTodos()

        assertEquals(1, resultado.size)
        assertEquals(ordemServicoId.valor, resultado.single().id)
    }

    @Test
    fun `deve obter orcamento da ordem de servico`() {
        val orcamento =
            Orcamento(
                itens =
                listOf(
                    ItemOrcamento.dePeca(
                        peca = peca1,
                        quantidade = BigDecimal("2"),
                    ),
                    ItemOrcamento.dePeca(
                        peca = peca2,
                        quantidade = BigDecimal("3"),
                    ),
                ),
            )

        `when`(
            repository.buscarPorId(ordemServicoId),
        ).thenReturn(
            ordemServicoComStatus(
                OrdemServicoStatus.RECEBIDA,
            ).copy(
                orcamento = orcamento,
            ),
        )

        val resultado =
            service.obterOrcamento(
                ordemServicoId.valor.toString(),
            )

        assertEquals(2, resultado.itens.size)

        assertEquals(
            0,
            BigDecimal("50").compareTo(
                resultado.valorTotal,
            ),
        )
    }

    @Test
    fun `deve lancar excecao quando peca do orcamento nao existir`() {
        prepararEntidadesRelacionadas()

        `when`(
            pecaRepository.buscarPorId(pecaId1),
        ).thenReturn(null)

        val exception =
            assertThrows(
                IllegalArgumentException::class.java,
            ) {
                service.criar(
                    request(
                        itens =
                        listOf(
                            itemRequest(
                                TipoItemOrcamento.PECA,
                                pecaId1,
                            ),
                        ),
                    ),
                )
            }

        assertEquals(
            "Peça não encontrada com o ID: $pecaId1",
            exception.message,
        )

        verify(
            repository,
            never(),
        ).salvar(anyObject())
    }

    @Test
    fun `deve lancar excecao quando servico do catalogo nao existir`() {
        prepararEntidadesRelacionadas()

        `when`(
            servicoRepository.buscarPorId(servicoId),
        ).thenReturn(null)

        val exception =
            assertThrows(
                IllegalArgumentException::class.java,
            ) {
                service.criar(
                    request(
                        itens =
                        listOf(
                            itemRequest(
                                TipoItemOrcamento.SERVICO,
                                servicoId,
                            ),
                        ),
                    ),
                )
            }

        assertEquals(
            "Serviço do catálogo não encontrado com o ID: $servicoId",
            exception.message,
        )

        verify(
            repository,
            never(),
        ).salvar(anyObject())
    }

    @Test
    fun `deve lancar excecao quando servico do catalogo estiver inativo`() {
        prepararEntidadesRelacionadas()

        `when`(
            servicoRepository.buscarPorId(servicoId),
        ).thenReturn(
            servicoCatalogo.desativar(),
        )

        val exception =
            assertThrows(
                IllegalArgumentException::class.java,
            ) {
                service.criar(
                    request(
                        itens =
                        listOf(
                            itemRequest(
                                TipoItemOrcamento.SERVICO,
                                servicoId,
                            ),
                        ),
                    ),
                )
            }

        assertEquals(
            "Serviço do catálogo está inativo: $servicoId",
            exception.message,
        )
    }

    @Test
    fun `deve lancar excecao ao obter orcamento de ordem inexistente`() {
        `when`(
            repository.buscarPorId(ordemServicoId),
        ).thenReturn(null)

        val exception =
            assertThrows(
                IllegalArgumentException::class.java,
            ) {
                service.obterOrcamento(
                    ordemServicoId.valor.toString(),
                )
            }

        assertEquals(
            "Ordem de serviço não encontrada com o ID: $ordemServicoId",
            exception.message,
        )
    }

    @Test
    fun `deve remover ordem de servico`() {
        `when`(
            repository.existePorId(ordemServicoId),
        ).thenReturn(true)

        service.deletarPorId(
            ordemServicoId.valor.toString(),
        )

        verify(repository).deletarPorId(ordemServicoId)
    }

    @Test
    fun `deve falhar ao remover ordem inexistente`() {
        `when`(
            repository.existePorId(ordemServicoId),
        ).thenReturn(false)

        val exception =
            assertThrows(
                IllegalArgumentException::class.java,
            ) {
                service.deletarPorId(
                    ordemServicoId.valor.toString(),
                )
            }

        assertEquals(
            "Ordem de serviço não encontrada para deletar.",
            exception.message,
        )

        verify(
            repository,
            never(),
        ).deletarPorId(ordemServicoId)
    }

    @ParameterizedTest
    @CsvSource(
        "RECEBIDA, EM_DIAGNOSTICO",
        "EM_DIAGNOSTICO, AGUARDANDO_APROVACAO",
        "AGUARDANDO_APROVACAO, EM_EXECUCAO",
        "EM_EXECUCAO, FINALIZADA",
        "FINALIZADA, ENTREGUE",
    )
    fun `avancar status deve seguir fluxo`(de: OrdemServicoStatus, esperado: OrdemServicoStatus) {
        `when`(
            repository.buscarPorId(ordemServicoId),
        ).thenReturn(
            ordemServicoComStatus(de),
        )

        `when`(
            repository.salvar(anyObject()),
        ).thenAnswer {
            it.getArgument<OrdemServico>(0)
        }

        val resultado =
            service.avancarStatus(
                ordemServicoId.valor.toString(),
            )

        assertEquals(
            esperado,
            resultado.status,
        )
    }

    @ParameterizedTest
    @EnumSource(
        value = OrdemServicoStatus::class,
        names = ["ENTREGUE", "CANCELADA"],
    )
    fun `avancar status deve falhar em estado final`(status: OrdemServicoStatus) {
        `when`(
            repository.buscarPorId(ordemServicoId),
        ).thenReturn(
            ordemServicoComStatus(status),
        )

        assertThrows(
            IllegalStateException::class.java,
        ) {
            service.avancarStatus(
                ordemServicoId.valor.toString(),
            )
        }

        verify(
            repository,
            never(),
        ).salvar(anyObject())
    }

    @ParameterizedTest
    @EnumSource(
        value = OrdemServicoStatus::class,
        names = ["EM_EXECUCAO", "CANCELADA"],
    )
    fun `alterar status deve permitir saidas de aguardando aprovacao`(alvo: OrdemServicoStatus) {
        `when`(
            repository.buscarPorId(ordemServicoId),
        ).thenReturn(
            ordemServicoComStatus(
                OrdemServicoStatus.AGUARDANDO_APROVACAO,
            ),
        )

        `when`(
            repository.salvar(anyObject()),
        ).thenAnswer {
            it.getArgument<OrdemServico>(0)
        }

        val resultado =
            service.alterarStatus(
                ordemServicoId.valor.toString(),
                alvo,
            )

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
    fun `alterar status deve rejeitar transicoes invalidas`(de: OrdemServicoStatus, alvo: OrdemServicoStatus) {
        `when`(
            repository.buscarPorId(ordemServicoId),
        ).thenReturn(
            ordemServicoComStatus(de),
        )

        assertThrows(
            IllegalStateException::class.java,
        ) {
            service.alterarStatus(
                ordemServicoId.valor.toString(),
                alvo,
            )
        }

        verify(
            repository,
            never(),
        ).salvar(anyObject())
    }

    @Test
    fun `listar por cliente deve retornar ordens`() {
        `when`(
            repository.listarPorCliente(clienteId),
        ).thenReturn(
            listOf(
                ordemServicoComStatus(
                    OrdemServicoStatus.RECEBIDA,
                ),
            ),
        )

        val resultado =
            service.listarPorCliente(
                clienteId.valor.toString(),
            )

        assertEquals(1, resultado.size)
        assertEquals(
            clienteId.valor.toString(),
            resultado.first().clienteId,
        )
    }

    @Test
    fun `listar por cliente deve retornar lista vazia`() {
        `when`(
            repository.listarPorCliente(clienteId),
        ).thenReturn(emptyList())

        val resultado =
            service.listarPorCliente(
                clienteId.valor.toString(),
            )

        assertTrue(resultado.isEmpty())
    }

    @Test
    fun `tempo medio deve retornar null sem finalizadas`() {
        `when`(
            repository.listarTodos(),
        ).thenReturn(
            listOf(
                ordemServicoComStatus(
                    OrdemServicoStatus.EM_EXECUCAO,
                ),
            ),
        )

        val resultado =
            service.calcularTempoMedioExecucao()

        assertEquals(
            0,
            resultado.totalOrdensFinalizadas,
        )

        assertNull(
            resultado.tempoMedioMinutos,
        )
    }

    @Test
    fun `tempo medio deve calcular media em minutos`() {
        val inicio =
            Instant.parse(
                "2025-01-01T08:00:00Z",
            )

        val ordem1 =
            ordemServicoComStatus(
                OrdemServicoStatus.FINALIZADA,
            ).copy(
                dataInicioExecucao = inicio,
                dataFinalizacao =
                Instant.parse(
                    "2025-01-01T10:00:00Z",
                ),
            )

        val ordem2 =
            ordemServicoComStatus(
                OrdemServicoStatus.FINALIZADA,
            ).copy(
                dataInicioExecucao = inicio,
                dataFinalizacao =
                Instant.parse(
                    "2025-01-01T11:00:00Z",
                ),
            )

        `when`(
            repository.listarTodos(),
        ).thenReturn(
            listOf(
                ordem1,
                ordem2,
            ),
        )

        val resultado =
            service.calcularTempoMedioExecucao()

        assertEquals(
            2,
            resultado.totalOrdensFinalizadas,
        )

        assertEquals(
            150.0,
            resultado.tempoMedioMinutos,
        )
    }

    private fun request(
        descricao: String = "Troca de Óleo",
        itens: List<ItemOrcamentoRequest> = emptyList(),
    ): OrdemServicoRequest = OrdemServicoRequest(
        descricao = descricao,
        funcionarioId = funcionarioId.valor.toString(),
        clienteId = clienteId.valor.toString(),
        veiculoId = veiculoId.valor.toString(),
        itens = itens,
    )

    private fun itemRequest(
        tipo: TipoItemOrcamento,
        referenciaId: Id,
        quantidade: BigDecimal = BigDecimal.ONE,
    ): ItemOrcamentoRequest = ItemOrcamentoRequest(
        tipo = tipo,
        referenciaId = referenciaId.valor.toString(),
        quantidade = quantidade,
    )

    private fun prepararEntidadesRelacionadas() {
        `when`(
            clienteRepository.buscarPorId(clienteId),
        ).thenReturn(cliente)

        `when`(
            funcionarioRepository.buscarPorId(funcionarioId),
        ).thenReturn(funcionario)

        `when`(
            veiculoRepository.buscarPorId(veiculoId),
        ).thenReturn(veiculo)
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
