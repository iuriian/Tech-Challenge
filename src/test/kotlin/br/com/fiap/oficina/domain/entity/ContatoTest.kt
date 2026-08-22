package br.com.fiap.oficina.domain.entity

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ContatoTest {

    @Test
    fun `deve criar um contato com todos os campos preenchidos`() {
        val contato = Contato.criar(
            tipo = "Pessoal",
            nome = "Maria Oliveira",
            telefone = "(11) 98888-7777"
        )

        assertNotNull(contato.id)
        assertEquals("Pessoal", contato.tipo)
        assertEquals("Maria Oliveira", contato.nome)
        assertEquals("(11) 98888-7777", contato.telefone)
    }

    @Test
    fun `deve rejeitar contato sem telefone`() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            Contato.criar(tipo = "Pessoal", nome = "Maria Oliveira", telefone = "")
        }

        assertEquals("Telefone do contato é obrigatório", exception.message)
    }

    @Test
    fun `deve rejeitar contato sem tipo`() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            Contato.criar(tipo = "", nome = "Maria Oliveira", telefone = "11999990000")
        }

        assertEquals("Tipo do contato é obrigatório", exception.message)
    }

    @Test
    fun `deve rejeitar contato sem nome`() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            Contato.criar(tipo = "Pessoal", nome = "", telefone = "11999990000")
        }

        assertEquals("Nome do contato é obrigatório", exception.message)
    }
}
