package br.com.fiap.oficina.domain.entity

import br.com.fiap.oficina.domain.enum.Cargo
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.util.UUID

class FuncionarioTest {
    @Test
    fun `deve criar funcionario com dados validos`() {
        val funcionario = Funcionario.criar(nome = "João", cargo = "ATENDENTE")

        assertEquals("João", funcionario.nome)
        assertEquals(Cargo.ATENDENTE, funcionario.cargo)
        assertNotNull(funcionario.id)
        assertNotNull(funcionario.id.valor)
    }

    @Test
    fun `deve gerar ids diferentes em criacoes`() {
        val f1 = Funcionario.criar(nome = "A", cargo = "ATENDENTE")
        val f2 = Funcionario.criar(nome = "B", cargo = "ATENDENTE")

        assertNotEquals(f1.id, f2.id)
    }

    @Test
    fun `deve reconstruir com id valido`() {
        val uuid = "00000000-0000-0000-0000-000000000100"
        val funcionario = Funcionario.reconstruir(id = uuid, nome = "Maria", cargo = "MECANICO")

        assertEquals(UUID.fromString(uuid), funcionario.id.valor)
        assertEquals("Maria", funcionario.nome)
        assertEquals(Cargo.MECANICO, funcionario.cargo)
    }

    @Test
    fun `reconstruir com id invalido deve lancar excecao`() {
        assertThrows(IllegalArgumentException::class.java) {
            Funcionario.reconstruir(id = "invalid-uuid", nome = "X", cargo = "ATENDENTE")
        }
    }

    @Test
    fun `criar com cargo invalido deve lancar excecao`() {
        assertThrows(IllegalArgumentException::class.java) {
            Funcionario.criar(nome = "Bad", cargo = "UNKNOWN")
        }
    }

    @Test
    fun `reconstruir com cargo invalido deve lancar excecao`() {
        val uuid = "00000000-0000-0000-0000-000000000101"
        assertThrows(IllegalArgumentException::class.java) {
            Funcionario.reconstruir(id = uuid, nome = "Bad", cargo = "INVALID")
        }
    }
}
