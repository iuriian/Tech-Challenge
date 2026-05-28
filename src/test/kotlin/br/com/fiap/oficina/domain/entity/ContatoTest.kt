package br.com.fiap.oficina.domain.entity

import br.com.fiap.oficina.infrastructure.persistence.entity.Cliente
import br.com.fiap.oficina.infrastructure.persistence.entity.Contato
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ContatoTest {

    @Test
    fun `deve criar um contato com todos os campos preenchidos`() {
        val contato = Contato().apply {
            id = 1L
            tipo = "Pessoal"
            nome = "Maria Oliveira"
            telefone = "(11) 98888-7777"
        }

        assertEquals(1L, contato.id)
        assertEquals("Pessoal", contato.tipo)
        assertEquals("Maria Oliveira", contato.nome)
        assertEquals("(11) 98888-7777", contato.telefone)
    }

    @Test
    fun `deve associar um cliente ao contato`() {
        val cliente = Cliente()
        val contato = Contato().apply {
            this.cliente = cliente
        }

        assertEquals(cliente, contato.cliente)
    }
}
