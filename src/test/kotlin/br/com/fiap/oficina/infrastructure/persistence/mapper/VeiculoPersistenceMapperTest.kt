package br.com.fiap.oficina.infrastructure.persistence.mapper

import br.com.fiap.oficina.domain.entity.Cliente
import br.com.fiap.oficina.domain.entity.Veiculo
import br.com.fiap.oficina.domain.valueobject.Documento
import br.com.fiap.oficina.domain.valueobject.Id
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class VeiculoPersistenceMapperTest {

    private val mapper = VeiculoPersistenceMapper(ClientePersistenceMapper())

    @Test
    fun `deve fazer round-trip de veiculo`() {
        val veiculo = Veiculo(
            id = Id.gerar(),
            marca = "Volkswagen",
            nome = "Gol",
            modelo = "Gol 1.6",
            ano = "2020",
            placa = "ABC1D23",
            motorista = Cliente(
                id = Id.gerar(),
                nome = "Dono",
                documento = Documento.cpf("39053344705"),
                email = "dono@example.com"
            )
        )

        val jpa = mapper.toJpa(veiculo)

        assertEquals(veiculo.id.valor, jpa.idVeiculo)
        assertEquals(veiculo.placa, jpa.placa)
        assertEquals(veiculo, mapper.toDomain(jpa))
    }
}
