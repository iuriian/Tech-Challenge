package br.com.fiap.oficina.presentation.mapper

import br.com.fiap.oficina.domain.entity.Cliente
import br.com.fiap.oficina.domain.valueobject.Documento
import br.com.fiap.oficina.domain.valueobject.Id
import br.com.fiap.oficina.presentation.dto.VeiculoDTO
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class VeiculoMapperTest {

    private val mapper = VeiculoMapper()

    private val motorista = Cliente(
        id = Id.gerar(),
        nome = "Dono",
        documento = Documento.cpf("39053344705"),
        email = "dono@example.com"
    )

    @Test
    fun `deve mapear VeiculoDTO para Veiculo`() {
        val dto = VeiculoDTO(
            nome = "Gol do João",
            marca = "Volkswagen",
            modelo = "Gol 1.6",
            ano = "2020",
            placa = "ABC1D23",
            motorista = motorista
        )

        val veiculo = mapper.toEntity(dto)

        assertNotNull(veiculo.id)
        assertEquals("Gol do João", veiculo.nome)
        assertEquals("Volkswagen", veiculo.marca)
        assertEquals(motorista, veiculo.motorista)
    }

    @Test
    fun `deve lancar excecao ao mapear VeiculoDTO sem motorista`() {
        val dto = VeiculoDTO(
            nome = "Gol",
            marca = "Volkswagen",
            modelo = "Gol 1.6",
            ano = "2020",
            placa = "ABC1D23",
            motorista = null
        )

        val exception = assertThrows(IllegalArgumentException::class.java) {
            mapper.toEntity(dto)
        }

        assertEquals("Motorista é obrigatório", exception.message)
    }

    @Test
    fun `deve mapear Veiculo para VeiculoDTO`() {
        val dto = mapper.toResponse(mapper.toEntity(
            VeiculoDTO("Gol", "Volkswagen", "Gol 1.6", "2020", "ABC1D23", motorista)
        ))

        assertEquals("Gol", dto.nome)
        assertEquals("ABC1D23", dto.placa)
        assertEquals(motorista, dto.motorista)
    }
}
