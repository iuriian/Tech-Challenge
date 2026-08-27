package br.com.fiap.oficina.application

import br.com.fiap.oficina.anyObject
import br.com.fiap.oficina.application.dto.CriarVeiculoRequest
import br.com.fiap.oficina.application.mapper.VeiculoApplicationMapper
import br.com.fiap.oficina.application.service.VeiculoService
import br.com.fiap.oficina.domain.entity.Cliente
import br.com.fiap.oficina.domain.entity.Veiculo
import br.com.fiap.oficina.domain.exception.VeiculoNaoEncontradoException
import br.com.fiap.oficina.domain.usecase.veiculo.AtualizarVeiculoUseCase
import br.com.fiap.oficina.domain.usecase.veiculo.BuscarVeiculoPorIdUseCase
import br.com.fiap.oficina.domain.usecase.veiculo.BuscarVeiculoPorPlacaUseCase
import br.com.fiap.oficina.domain.usecase.veiculo.BuscarVeiculosPorMotoristaUseCase
import br.com.fiap.oficina.domain.usecase.veiculo.CriarVeiculoUseCase
import br.com.fiap.oficina.domain.usecase.veiculo.ListarVeiculosUseCase
import br.com.fiap.oficina.domain.usecase.veiculo.RemoverVeiculoUseCase
import br.com.fiap.oficina.domain.valueobject.Documento
import br.com.fiap.oficina.domain.valueobject.Id
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class VeiculoServiceTest {
    @Mock
    lateinit var criarVeiculoUseCase: CriarVeiculoUseCase

    @Mock
    lateinit var listarVeiculosUseCase: ListarVeiculosUseCase

    @Mock
    lateinit var buscarVeiculoPorIdUseCase: BuscarVeiculoPorIdUseCase

    @Mock
    lateinit var buscarVeiculoPorPlacaUseCase: BuscarVeiculoPorPlacaUseCase

    @Mock
    lateinit var buscarVeiculosPorMotoristaUseCase: BuscarVeiculosPorMotoristaUseCase

    @Mock
    lateinit var atualizarVeiculoUseCase: AtualizarVeiculoUseCase

    @Mock
    lateinit var removerVeiculoUseCase: RemoverVeiculoUseCase

    private val mapper = VeiculoApplicationMapper()
    private lateinit var service: VeiculoService
    private lateinit var veiculo: Veiculo

    @BeforeEach
    fun setup() {
        service =
            VeiculoService(
                criarVeiculoUseCase,
                listarVeiculosUseCase,
                buscarVeiculoPorIdUseCase,
                buscarVeiculoPorPlacaUseCase,
                buscarVeiculosPorMotoristaUseCase,
                atualizarVeiculoUseCase,
                removerVeiculoUseCase,
                mapper,
            )
        val motorista =
            Cliente(
                id = Id.generate(),
                nome = "Dono",
                documento = Documento.cpf("39053344705"),
                email = "dono@example.com",
            )
        veiculo =
            Veiculo(
                id = Id.generate(),
                marca = "Volkswagen",
                nome = "Gol",
                modelo = "Gol 1.6",
                ano = "2020",
                placa = "ABC1D23",
                motorista = motorista,
            )
    }

    @Test
    fun `deve criar veiculo`() {
        val request =
            CriarVeiculoRequest(
                nome = "Gol",
                marca = "Volkswagen",
                modelo = "Gol 1.6",
                ano = "2020",
                placa = "ABC1D23",
                motoristaId = veiculo.motorista.id.valor.toString(),
            )
        `when`(criarVeiculoUseCase.executar(anyObject())).thenReturn(veiculo)

        val response = service.criar(request)

        assertEquals(veiculo.placa, response.placa)
    }

    @Test
    fun `deve buscar veiculo por id`() {
        `when`(buscarVeiculoPorIdUseCase.executar(anyObject())).thenReturn(veiculo)

        val response = service.buscarPorId(veiculo.id.valor.toString())

        assertEquals(veiculo.nome, response.nome)
    }

    @Test
    fun `deve remover veiculo`() {
        service.remover(veiculo.id.valor.toString())

        verify(removerVeiculoUseCase).executar(Id.fromString(veiculo.id.valor.toString()))
    }

    @Test
    fun `deve propagar VeiculoNaoEncontradoException`() {
        `when`(buscarVeiculoPorIdUseCase.executar(anyObject()))
            .thenThrow(VeiculoNaoEncontradoException.porId(veiculo.id.valor.toString()))

        org.junit.jupiter.api.assertThrows<VeiculoNaoEncontradoException> {
            service.buscarPorId(veiculo.id.valor.toString())
        }
    }
}
