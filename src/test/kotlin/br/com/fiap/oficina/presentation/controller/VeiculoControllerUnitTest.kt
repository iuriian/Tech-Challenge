package br.com.fiap.oficina.presentation.controller

import br.com.fiap.oficina.anyObject
import br.com.fiap.oficina.domain.entity.Cliente
import br.com.fiap.oficina.domain.entity.Veiculo
import br.com.fiap.oficina.domain.usecase.veiculo.AtualizarVeiculoUseCase
import br.com.fiap.oficina.domain.usecase.veiculo.BuscarVeiculoPorIdUseCase
import br.com.fiap.oficina.domain.usecase.veiculo.BuscarVeiculoPorPlacaUseCase
import br.com.fiap.oficina.domain.usecase.veiculo.BuscarVeiculosPorMotoristaUseCase
import br.com.fiap.oficina.domain.usecase.veiculo.CriarVeiculoUseCase
import br.com.fiap.oficina.domain.usecase.veiculo.ListarVeiculosUseCase
import br.com.fiap.oficina.domain.usecase.veiculo.RemoverVeiculoUseCase
import br.com.fiap.oficina.domain.valueobject.Documento
import br.com.fiap.oficina.domain.valueobject.Id
import br.com.fiap.oficina.presentation.dto.VeiculoDTO
import br.com.fiap.oficina.presentation.mapper.VeiculoMapper
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import kotlin.test.assertEquals

class VeiculoControllerUnitTest {
    private val criarVeiculoUseCase = mock(CriarVeiculoUseCase::class.java)
    private val buscarVeiculoPorIdUseCase = mock(BuscarVeiculoPorIdUseCase::class.java)
    private val buscarVeiculoPorPlacaUseCase = mock(BuscarVeiculoPorPlacaUseCase::class.java)
    private val buscarVeiculosPorMotoristaUseCase = mock(BuscarVeiculosPorMotoristaUseCase::class.java)
    private val listarVeiculosUseCase = mock(ListarVeiculosUseCase::class.java)
    private val atualizarVeiculoUseCase = mock(AtualizarVeiculoUseCase::class.java)
    private val removerVeiculoUseCase = mock(RemoverVeiculoUseCase::class.java)
    private val controller =
        VeiculoController(
            criarVeiculoUseCase,
            buscarVeiculoPorIdUseCase,
            buscarVeiculoPorPlacaUseCase,
            buscarVeiculosPorMotoristaUseCase,
            listarVeiculosUseCase,
            atualizarVeiculoUseCase,
            removerVeiculoUseCase,
            VeiculoMapper(),
        )

    private val motorista =
        Cliente(
            id = Id.generate(),
            nome = "Dono",
            documento = Documento.cpf("39053344705"),
            email = "dono@example.com",
        )

    private val veiculo =
        Veiculo(
            id = Id.generate(),
            marca = "Volkswagen",
            nome = "Gol",
            modelo = "Gol 1.6",
            ano = "2020",
            placa = "ABC1D23",
            motorista = motorista,
        )

    private fun veiculoDto() = VeiculoDTO(
        nome = "Gol",
        marca = "Volkswagen",
        modelo = "Gol 1.6",
        ano = "2020",
        placa = "ABC1D23",
        motoristaId = motorista.id.valor.toString(),
    )

    @Test
    fun `criar deve retornar dto do veiculo salvo`() {
        `when`(criarVeiculoUseCase.executar(anyObject())).thenReturn(veiculo)

        val dto = controller.criar(veiculoDto())

        assertEquals("ABC1D23", dto.placa)
        assertEquals(motorista.id.valor.toString(), dto.motoristaId)
    }

    @Test
    fun `buscarVeiculoPorId deve mapear resultado`() {
        val id = veiculo.id.valor
        `when`(buscarVeiculoPorIdUseCase.executar(Id.fromString(id.toString()))).thenReturn(veiculo)

        assertEquals("Gol", controller.buscarVeiculoPorId(id.toString())?.nome)
    }

    @Test
    fun `buscarVeiculoPorPlaca deve mapear resultado`() {
        `when`(buscarVeiculoPorPlacaUseCase.executar("ABC1D23")).thenReturn(veiculo)

        assertEquals("Gol", controller.buscarVeiculoPorPlaca("ABC1D23")?.nome)
    }

    @Test
    fun `buscarVeiculosPorMotorista deve mapear lista`() {
        `when`(buscarVeiculosPorMotoristaUseCase.executar(motorista.id)).thenReturn(listOf(veiculo))

        assertEquals(1, controller.buscarVeiculosPorMotorista(motorista.id.valor.toString()).size)
    }

    @Test
    fun `listarTodos deve mapear resultados`() {
        `when`(listarVeiculosUseCase.executar()).thenReturn(listOf(veiculo))

        assertEquals(1, controller.listarTodos().size)
        assertEquals("Gol", controller.listarTodos().first().nome)
    }

    @Test
    fun `atualizar deve retornar dto do veiculo atualizado`() {
        `when`(atualizarVeiculoUseCase.executar(anyObject())).thenReturn(veiculo)

        val dto = controller.atualizar(veiculo.id.valor.toString(), veiculoDto())

        assertEquals("ABC1D23", dto.placa)
    }

    @Test
    fun `remover deve delegar ao use case`() {
        val id = veiculo.id.valor.toString()

        controller.remover(id)

        verify(removerVeiculoUseCase).executar(Id.fromString(id))
    }
}
