package br.com.fiap.oficina.infrastructure.persistence.mapper

import br.com.fiap.oficina.domain.entity.Cliente
import br.com.fiap.oficina.domain.entity.Contato
import br.com.fiap.oficina.domain.entity.Endereco
import br.com.fiap.oficina.domain.valueobject.Documento
import br.com.fiap.oficina.domain.valueobject.Id
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ClientePersistenceMapperTest {
    private val mapper = ClientePersistenceMapper()

    private fun clienteCompleto(): Cliente =
        Cliente(
            id = Id.generate(),
            nome = "João da Silva",
            documento = Documento.cpf("39053344705"),
            email = "joao@example.com",
            endereco =
                Endereco(
                    id = Id.generate(),
                    logradouro = "Rua A",
                    numero = "100",
                    complemento = "Apto 1",
                    bairro = "Centro",
                    cidade = "São Paulo",
                    estado = "SP",
                    cep = "01000000",
                ),
            contatos =
                listOf(
                    Contato(Id.generate(), "CELULAR", "João", "11999990000"),
                ),
        )

    @Test
    fun `deve fazer round-trip de cliente completo`() {
        val cliente = clienteCompleto()

        val resultado = mapper.toDomain(mapper.toJpa(cliente))

        assertEquals(cliente, resultado)
    }

    @Test
    fun `deve religar referencias bidirecionais ao mapear para jpa`() {
        val cliente = clienteCompleto()

        val jpa = mapper.toJpa(cliente)

        assertEquals(cliente.id.valor, jpa.id)
        assertSame(jpa, jpa.endereco?.cliente)
        assertEquals(1, jpa.contatos.size)
        jpa.contatos.forEach { assertSame(jpa, it.cliente) }
    }

    @Test
    fun `deve fazer round-trip de cliente sem endereco e sem contatos`() {
        val cliente =
            Cliente(
                id = Id.generate(),
                nome = "Empresa LTDA",
                documento = Documento.cnpj("11222333000181"),
                email = "empresa@example.com",
            )

        val jpa = mapper.toJpa(cliente)

        assertNull(jpa.endereco)
        assertTrue(jpa.contatos.isEmpty())
        assertEquals(cliente, mapper.toDomain(jpa))
    }
}
