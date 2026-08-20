package br.com.fiap.oficina.presentation.controller

import br.com.fiap.oficina.anyObject
import br.com.fiap.oficina.application.service.VeiculoService
import br.com.fiap.oficina.domain.entity.Cliente
import br.com.fiap.oficina.domain.entity.Veiculo
import br.com.fiap.oficina.domain.valueobject.Documento
import br.com.fiap.oficina.domain.valueobject.Id
import br.com.fiap.oficina.presentation.dto.VeiculoDTO
import br.com.fiap.oficina.presentation.mapper.VeiculoMapper
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import kotlin.test.assertEquals

class VeiculoControllerUnitTest {
    private val service = mock(VeiculoService::class.java)
    private val controller = VeiculoController(service, VeiculoMapper())

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

    @Test
    fun `criar deve retornar dto do veiculo salvo`() {
        `when`(service.salvarVeiculo(anyObject())).thenReturn(veiculo)

        val dto =
            controller.criar(
                VeiculoDTO("Gol", "Volkswagen", "Gol 1.6", "2020", "ABC1D23", motorista.id.valor.toString()),
            )

        assertEquals("ABC1D23", dto.placa)
        assertEquals(motorista.id.valor.toString(), dto.motoristaId)
    }

    @Test
    fun `buscarVeiculoPorId deve mapear resultado`() {
        val id = veiculo.id.valor
        `when`(service.buscarPorId(Id.fromString(id.toString()))).thenReturn(veiculo)

        assertEquals("Gol", controller.buscarVeiculoPorId(id.toString())?.nome)
    }

    @Test
    fun `buscarVeiculoPorPlaca deve mapear resultado`() {
        `when`(service.buscarPorPlaca("ABC1D23")).thenReturn(veiculo)

        assertEquals("Gol", controller.buscarVeiculoPorPlaca("ABC1D23")?.nome)
    }

    @Test
    fun `buscarVeiculosPorMotorista deve mapear lista`() {
        `when`(service.buscarPorMotorista(motorista.id)).thenReturn(listOf(veiculo))

        assertEquals(1, controller.buscarVeiculosPorMotorista(motorista.id.valor.toString()).size)
    }
}
