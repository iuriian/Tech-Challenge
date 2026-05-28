package br.com.fiap.oficina.infrastructure.persistence.entity


interface ValidadorDocumento{
    fun valida(documento: Documento): Boolean
}

interface FormatadorDocumento {
    fun formata(numero: String): String
}

enum class TipoPessoa(private val validadorDocumento: ValidadorDocumento,
                      private val formatadorDocumento: FormatadorDocumento) {

    PESSOA_JURIDICA(ValidadorCnpj(), FormatadorCnpj()),
    PESSOA_FISICA(ValidadorCpf(), FormatadorCpf());

    fun valida(documento: Documento) = this.validadorDocumento.valida(documento)
    fun formata(numero: String): String = this.formatadorDocumento.formata(numero)

}

class ValidadorCpf : ValidadorDocumento{
    private val pesosCpfPrimeiroDigito = intArrayOf(10, 9, 8, 7, 6, 5, 4, 3, 2)
    private val pesosCpfSegundoDigito = intArrayOf(11, 10, 9, 8, 7, 6, 5, 4, 3, 2)

    override fun valida(documento: Documento): Boolean {
        val numero = documento.numero
        val cpfLimpo = numero.replace(Regex("[^0-9]"), "")

        if (cpfLimpo.length != 11) return false
        if (cpfLimpo.all { it == cpfLimpo[0] }) return false

        // Validação do primeiro dígito verificador
        val primeiroDigito = calcularDigitoCpf(cpfLimpo.substring(0, 9), pesosCpfPrimeiroDigito)
        if (primeiroDigito != cpfLimpo[9].digitToInt()) return false

        // Validação do segundo dígito verificador
        val segundoDigito = calcularDigitoCpf(cpfLimpo.substring(0, 10), pesosCpfSegundoDigito)
        return segundoDigito == cpfLimpo[10].digitToInt()
    }
    private fun calcularDigitoCpf(base: String, pesos: IntArray): Int {
        val soma = base.mapIndexed { index, char -> char.digitToInt() * pesos[index] }.sum()
        val resto = soma % 11
        return if (resto < 2) 0 else 11 - resto
    }
}

class ValidadorCnpj : ValidadorDocumento{

    private val pesosCnpjPrimeiroDigito = intArrayOf(5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2)
    private val pesosCnpjSegundoDigito = intArrayOf(6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2)

    override fun valida(documento: Documento): Boolean {
        val numero = documento.numero
        val cnpjLimpo = numero.replace(Regex("[^0-9]"), "")

        if (cnpjLimpo.length != 14) return false
        if (cnpjLimpo.all { it == cnpjLimpo[0] }) return false

        // Validação do primeiro dígito verificador
        val primeiroDigito = calcularDigitoCnpj(cnpjLimpo.substring(0, 12), pesosCnpjPrimeiroDigito)
        if (primeiroDigito != cnpjLimpo[12].digitToInt()) return false

        // Validação do segundo dígito verificador
        val segundoDigito = calcularDigitoCnpj(cnpjLimpo.substring(0, 13), pesosCnpjSegundoDigito)
        return segundoDigito == cnpjLimpo[13].digitToInt()
    }

    private fun calcularDigitoCnpj(base: String, pesos: IntArray): Int {
        val soma = base.mapIndexed { index, char -> char.digitToInt() * pesos[index] }.sum()
        val resto = soma % 11
        return if (resto < 2) 0 else 11 - resto
    }

}


class FormatadorCnpj : FormatadorDocumento {
    override fun formata(numero: String): String {
        val cnpjLimpo = numero.replace(Regex("[^0-9]"), "")
        if (cnpjLimpo.length != 14) return numero

        return "${cnpjLimpo.substring(0, 2)}.${cnpjLimpo.substring(2, 5)}.${cnpjLimpo.substring(5, 8)}/${cnpjLimpo.substring(8, 12)}-${cnpjLimpo.substring(12, 14)}"
    }
}

class FormatadorCpf : FormatadorDocumento {
    override fun formata(numero: String): String {
        val cpfLimpo = numero.replace(Regex("[^0-9]"), "")
        if (cpfLimpo.length != 11) return numero

        return "${cpfLimpo.substring(0, 3)}.${cpfLimpo.substring(3, 6)}.${cpfLimpo.substring(6, 9)}-${cpfLimpo.substring(9, 11)}"
    }
}
