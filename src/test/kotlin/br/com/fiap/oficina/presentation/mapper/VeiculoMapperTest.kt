package br.com.fiap.oficina.presentation.mapper

import br.com.fiap.oficina.application.dto.VeiculoResponse
import br.com.fiap.oficina.presentation.dto.VeiculoDTO
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class VeiculoMapperTest {
    private val mapper = VeiculoMapper()

    @Test
    fun `deve mapear VeiculoResponse para VeiculoDTO`() {
        val response =
            VeiculoResponse(
                id = "00000000-0000-0000-0000-000000000010",
                nome = "Gol do João",
                marca = "Volkswagen",
                modelo = "Gol 1.6",
                ano = "2020",
                placa = "ABC1D23",
                motoristaId = "00000000-0000-0000-0000-000000000050",
            )

        val dto = mapper.toDto(response)

        assertEquals(response.nome, dto.nome)
        assertEquals(response.placa, dto.placa)
        assertEquals(response.motoristaId, dto.motoristaId)
    }

    @Test
    fun `deve mapear VeiculoDTO para CriarVeiculoRequest`() {
        val dto =
            VeiculoDTO(
                nome = "Gol",
                marca = "Volkswagen",
                modelo = "Gol 1.6",
                ano = "2020",
                placa = "ABC1D23",
                motoristaId = "00000000-0000-0000-0000-000000000050",
            )

        val request = mapper.toCriarRequest(dto)

        assertEquals(dto.placa, request.placa)
        assertEquals(dto.motoristaId, request.motoristaId)
    }
}
