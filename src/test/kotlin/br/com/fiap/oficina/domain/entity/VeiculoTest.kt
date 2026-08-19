package br.com.fiap.oficina.domain.entity

import br.com.fiap.oficina.domain.valueobject.Documento
import br.com.fiap.oficina.domain.valueobject.Id
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class VeiculoTest {
    private fun motorista(): Cliente = Cliente(
        id = Id.generate(),
        nome = "Dono",
        documento = Documento.cpf("39053344705"),
        email = "dono@example.com",
    )

    @Test
    fun `deve criar um veiculo valido`() {
        val dono = motorista()
        val veiculo =
            Veiculo.criar(
                marca = "Volkswagen",
                nome = "Gol do João",
                modelo = "Gol 1.6",
                ano = "2020",
                placa = "ABC1D23",
                motorista = dono,
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
    fun `deve criar veiculo com placa no formato antigo`() {
        val veiculo =
            Veiculo.criar(
                marca = "Ford",
                nome = "Ka",
                modelo = "Ka 1.0",
                ano = "2015",
                placa = "ABC1234",
                motorista = motorista(),
            )
        assertEquals("ABC1234", veiculo.placa)
    }

    @Test
    fun `deve rejeitar placa com tamanho invalido`() {
        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                Veiculo.criar(
                    marca = "Volkswagen",
                    nome = "Gol",
                    modelo = "Gol 1.6",
                    ano = "2020",
                    placa = "ABC",
                    motorista = motorista(),
                )
            }

        assertEquals("Placa inválida: use o formato antigo (ABC1234) ou Mercosul (ABC1D23)", exception.message)
    }

    @Test
    fun `deve rejeitar placa iniciada com digito`() {
        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                Veiculo.criar(
                    marca = "Volkswagen",
                    nome = "Gol",
                    modelo = "Gol 1.6",
                    ano = "2020",
                    placa = "1BC1234",
                    motorista = motorista(),
                )
            }

        assertEquals("Placa inválida: use o formato antigo (ABC1234) ou Mercosul (ABC1D23)", exception.message)
    }

    @Test
    fun `deve rejeitar placa com letras nas posicoes de digito`() {
        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                Veiculo.criar(
                    marca = "Volkswagen",
                    nome = "Gol",
                    modelo = "Gol 1.6",
                    ano = "2020",
                    placa = "ABCDEFG",
                    motorista = motorista(),
                )
            }

        assertEquals("Placa inválida: use o formato antigo (ABC1234) ou Mercosul (ABC1D23)", exception.message)
    }

    @Test
    fun `deve aceitar placa mercosul com letra minuscula`() {
        val veiculo =
            Veiculo.criar(
                marca = "Honda",
                nome = "Civic",
                modelo = "Civic 2.0",
                ano = "2023",
                placa = "abc1d23",
                motorista = motorista(),
            )
        assertEquals("abc1d23", veiculo.placa)
    }

    @Test
    fun `deve rejeitar marca em branco`() {
        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                Veiculo.criar(
                    marca = "",
                    nome = "Gol",
                    modelo = "Gol 1.6",
                    ano = "2020",
                    placa = "ABC1D23",
                    motorista = motorista(),
                )
            }

        assertEquals("Marca é obrigatória", exception.message)
    }

    @Test
    fun `deve rejeitar ano em branco`() {
        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                Veiculo.criar(
                    marca = "Volkswagen",
                    nome = "Gol",
                    modelo = "Gol 1.6",
                    ano = "",
                    placa = "ABC1D23",
                    motorista = motorista(),
                )
            }

        assertEquals("Ano é obrigatório", exception.message)
    }

    @Test
    fun `deve rejeitar nome em branco`() {
        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                Veiculo.criar(
                    marca = "Volkswagen",
                    nome = "",
                    modelo = "Gol 1.6",
                    ano = "2020",
                    placa = "ABC1D23",
                    motorista = motorista(),
                )
            }

        assertEquals("Nome do veículo é obrigatório", exception.message)
    }

    @Test
    fun `deve rejeitar modelo em branco`() {
        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                Veiculo.criar(
                    marca = "Volkswagen",
                    nome = "Gol",
                    modelo = "",
                    ano = "2020",
                    placa = "ABC1D23",
                    motorista = motorista(),
                )
            }

        assertEquals("Modelo é obrigatório", exception.message)
    }
}
