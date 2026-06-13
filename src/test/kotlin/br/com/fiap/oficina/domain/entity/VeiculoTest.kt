package br.com.fiap.oficina.domain.entity

import br.com.fiap.oficina.domain.valueobject.Documento
import br.com.fiap.oficina.domain.valueobject.Id
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class VeiculoTest {

    private fun motorista(): Cliente =
        Cliente(
            id = Id.gerar(),
            nome = "Dono",
            documento = Documento.cpf("39053344705"),
            email = "dono@example.com"
        )

    @Test
    fun `deve criar um veiculo valido`() {
        val dono = motorista()
        val veiculo = Veiculo.criar(
            marca = "Volkswagen",
            nome = "Gol do João",
            modelo = "Gol 1.6",
            ano = "2020",
            placa = "ABC1D23",
            motorista = dono
        )

        assertNotNull(veiculo.id)
        assertEquals("Volkswagen", veiculo.marca)
        assertEquals("Gol do João", veiculo.nome)
        assertEquals("Gol 1.6", veiculo.modelo)
        assertEquals("2020", veiculo.ano)
        assertEquals("ABC1D23", veiculo.placa)
        assertEquals(dono, veiculo.motorista)
    }

    @Test
    fun `deve rejeitar placa com tamanho invalido`() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            Veiculo.criar(
                marca = "Volkswagen",
                nome = "Gol",
                modelo = "Gol 1.6",
                ano = "2020",
                placa = "ABC",
                motorista = motorista()
            )
        }

        assertEquals("Placa deve ter exatamente 7 caracteres", exception.message)
    }

    @Test
    fun `deve rejeitar marca em branco`() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            Veiculo.criar(
                marca = "",
                nome = "Gol",
                modelo = "Gol 1.6",
                ano = "2020",
                placa = "ABC1D23",
                motorista = motorista()
            )
        }

        assertEquals("Marca é obrigatória", exception.message)
    }

    @Test
    fun `deve rejeitar ano em branco`() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            Veiculo.criar(
                marca = "Volkswagen",
                nome = "Gol",
                modelo = "Gol 1.6",
                ano = "",
                placa = "ABC1D23",
                motorista = motorista()
            )
        }

        assertEquals("Ano é obrigatório", exception.message)
    }

    @Test
    fun `deve rejeitar nome em branco`() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            Veiculo.criar(
                marca = "Volkswagen",
                nome = "",
                modelo = "Gol 1.6",
                ano = "2020",
                placa = "ABC1D23",
                motorista = motorista()
            )
        }

        assertEquals("Nome do veículo é obrigatório", exception.message)
    }

    @Test
    fun `deve rejeitar modelo em branco`() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            Veiculo.criar(
                marca = "Volkswagen",
                nome = "Gol",
                modelo = "",
                ano = "2020",
                placa = "ABC1D23",
                motorista = motorista()
            )
        }

        assertEquals("Modelo é obrigatório", exception.message)
    }
}
