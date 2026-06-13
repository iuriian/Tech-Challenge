package br.com.fiap.oficina.domain.valueobject

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class DocumentoTest {

    @Test
    fun `deve criar cpf via factory`() {
        val documento = Documento.cpf("39053344705")

        assertEquals(TipoPessoa.PESSOA_FISICA, documento.tipoPessoa)
        assertEquals("39053344705", documento.numero)
    }

    @Test
    fun `deve criar cnpj via factory`() {
        val documento = Documento.cnpj("11222333000181")

        assertEquals(TipoPessoa.PESSOA_JURIDICA, documento.tipoPessoa)
    }

    @Test
    fun `deve validar cpf valido`() {
        assertTrue(Documento.cpf("39053344705").isFormatoValido())
        assertTrue(Documento.cpf("390.533.447-05").isFormatoValido())
    }

    @Test
    fun `deve invalidar cpf invalido`() {
        assertFalse(Documento.cpf("12345678900").isFormatoValido())
        assertFalse(Documento.cpf("00000000000").isFormatoValido())
        assertFalse(Documento.cpf("123").isFormatoValido())
    }

    @Test
    fun `deve validar cnpj valido`() {
        assertTrue(Documento.cnpj("11222333000181").isFormatoValido())
        assertTrue(Documento.cnpj("11.222.333/0001-81").isFormatoValido())
    }

    @Test
    fun `deve invalidar cnpj invalido`() {
        assertFalse(Documento.cnpj("11222333000100").isFormatoValido())
        assertFalse(Documento.cnpj("00000000000000").isFormatoValido())
    }

    @Test
    fun `deve formatar cpf`() {
        assertEquals("390.533.447-05", Documento.cpf("39053344705").getNumeroFormatado())
    }

    @Test
    fun `deve formatar cnpj`() {
        assertEquals("11.222.333/0001-81", Documento.cnpj("11222333000181").getNumeroFormatado())
    }

    @Test
    fun `deve retornar numero original quando tamanho invalido para formatacao`() {
        assertEquals("123", Documento.cpf("123").getNumeroFormatado())
        assertEquals("123", Documento.cnpj("123").getNumeroFormatado())
    }

    @Test
    fun `equals deve ignorar formatacao`() {
        val comMascara = Documento.cpf("390.533.447-05")
        val semMascara = Documento.cpf("39053344705")

        assertEquals(comMascara, semMascara)
        assertEquals(comMascara.hashCode(), semMascara.hashCode())
    }

    @Test
    fun `equals deve diferenciar tipos de pessoa e numeros`() {
        assertNotEquals(Documento.cpf("39053344705"), Documento.cnpj("39053344705"))
        assertNotEquals(Documento.cpf("39053344705"), Documento.cpf("11144477735"))
        assertNotEquals(Documento.cpf("39053344705"), null)
        assertTrue(Documento.cpf("39053344705") == Documento.cpf("39053344705"))
    }

    @Test
    fun `toString deve conter numero formatado`() {
        val texto = Documento.cpf("39053344705").toString()

        assertTrue(texto.contains("390.533.447-05"))
        assertTrue(texto.contains("PESSOA_FISICA"))
    }
}
