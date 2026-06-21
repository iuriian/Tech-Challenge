package br.com.fiap.oficina.presentation.mapper

import br.com.fiap.oficina.domain.entity.Cliente
import br.com.fiap.oficina.domain.entity.Veiculo
import br.com.fiap.oficina.domain.valueobject.Documento
import br.com.fiap.oficina.domain.valueobject.Id
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

    private val veiculo = Veiculo(
        id = Id.gerar(),
        nome = "Gol do João",
        marca = "Volkswagen",
        modelo = "Gol 1.6",
        ano = "2020",
        placa = "ABC1D23",
        motorista = motorista
    )

    @Test
    fun `deve mapear Veiculo para VeiculoDTO com todos os campos`() {
        val dto = mapper.toResponse(veiculo)

        assertEquals(veiculo.id.valor, dto.id)
        assertEquals(veiculo.nome, dto.nome)
        assertEquals(veiculo.marca, dto.marca)
        assertEquals(veiculo.modelo, dto.modelo)
        assertEquals(veiculo.ano, dto.ano)
        assertEquals(veiculo.placa, dto.placa)
        assertEquals(motorista.id.valor, dto.motoristaId)
    }
}
