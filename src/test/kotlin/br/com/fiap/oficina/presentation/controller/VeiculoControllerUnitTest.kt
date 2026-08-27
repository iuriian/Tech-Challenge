package br.com.fiap.oficina.presentation.controller

import br.com.fiap.oficina.anyObject
import br.com.fiap.oficina.application.dto.VeiculoResponse
import br.com.fiap.oficina.application.service.VeiculoService
import br.com.fiap.oficina.presentation.dto.VeiculoDTO
import br.com.fiap.oficina.presentation.mapper.VeiculoMapper
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

class VeiculoControllerUnitTest {
    private val service = mock(VeiculoService::class.java)
    private val mapper = VeiculoMapper()
    private val controller = VeiculoController(service, mapper)

    private val veiculoResponse =
        VeiculoResponse(
            id = "00000000-0000-0000-0000-000000000010",
            nome = "Gol",
            marca = "Volkswagen",
            modelo = "Gol 1.6",
            ano = "2020",
            placa = "ABC1D23",
            motoristaId = "00000000-0000-0000-0000-000000000050",
        )

    private fun veiculoDto() = VeiculoDTO(
        nome = "Gol",
        marca = "Volkswagen",
        modelo = "Gol 1.6",
        ano = "2020",
        placa = "ABC1D23",
        motoristaId = "00000000-0000-0000-0000-000000000050",
    )

    @BeforeEach
    fun setupRequestContext() {
        val request = MockHttpServletRequest("POST", "/veiculos")
        RequestContextHolder.setRequestAttributes(ServletRequestAttributes(request))
    }

    @AfterEach
    fun clearRequestContext() {
        RequestContextHolder.resetRequestAttributes()
    }

    @Test
    fun `criar deve retornar dto do veiculo salvo`() {
        `when`(service.criar(anyObject())).thenReturn(veiculoResponse)

        val response = controller.criar(veiculoDto())

        assertEquals("ABC1D23", response.body?.placa)
        assertEquals(201, response.statusCode.value())
    }

    @Test
    fun `buscarVeiculoPorId deve mapear resultado`() {
        `when`(service.buscarPorId(veiculoResponse.id)).thenReturn(veiculoResponse)

        assertEquals("Gol", controller.buscarVeiculoPorId(veiculoResponse.id).nome)
    }

    @Test
    fun `atualizar deve retornar dto do veiculo atualizado`() {
        `when`(service.atualizar(anyObject(), anyObject())).thenReturn(veiculoResponse)

        val dto = controller.atualizar(veiculoResponse.id, veiculoDto())

        assertEquals("ABC1D23", dto.placa)
    }

    @Test
    fun `remover deve delegar ao service`() {
        controller.remover(veiculoResponse.id)

        verify(service).remover(veiculoResponse.id)
    }
}
