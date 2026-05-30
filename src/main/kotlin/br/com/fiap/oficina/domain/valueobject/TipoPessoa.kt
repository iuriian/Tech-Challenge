package br.com.fiap.oficina.domain.valueobject


interface ValidadorDocumento {
    fun valida(documento: Documento): Boolean
}

interface FormatadorDocumento {
    fun formata(numero: String): String
}

enum class TipoPessoa(
    private val validadorDocumento: ValidadorDocumento,
    private val formatadorDocumento: FormatadorDocumento
) {

    PESSOA_JURIDICA(ValidadorCnpj(), FormatadorCnpj()),
    PESSOA_FISICA(ValidadorCpf(), FormatadorCpf());

    fun valida(documento: Documento) = this.validadorDocumento.valida(documento)
    fun formata(numero: String): String = this.formatadorDocumento.formata(numero)

}

private const val CPF_LENGTH = 11
private const val CNPJ_LENGTH = 14
private const val MODULO_DIGITO_VERIFICADOR = 11
private const val RESTO_MINIMO_SEM_DIGITO = 2

private val CARACTERES_NAO_NUMERICOS = Regex("[^0-9]")

private fun calcularDigitoVerificador(base: String, pesos: IntArray): Int {
    val soma = base.mapIndexed { index, char -> char.digitToInt() * pesos[index] }.sum()
    val resto = soma % MODULO_DIGITO_VERIFICADOR
    return if (resto < RESTO_MINIMO_SEM_DIGITO) 0 else MODULO_DIGITO_VERIFICADOR - resto
}

abstract class ValidadorComDigitoVerificador(
    private val tamanho: Int,
    private val primeiroDigitoIndex: Int,
    private val segundoDigitoIndex: Int,
    private val pesosPrimeiroDigito: IntArray,
    private val pesosSegundoDigito: IntArray
) : ValidadorDocumento {

    override fun valida(documento: Documento): Boolean {
        val numeroLimpo = documento.numero.replace(CARACTERES_NAO_NUMERICOS, "")

        if ((numeroLimpo.length != tamanho) || (numeroLimpo.all { it == numeroLimpo[0] })) return false

        // Validação do primeiro dígito verificador
        val primeiroDigito = calcularDigitoVerificador(
            numeroLimpo.substring(0, primeiroDigitoIndex), pesosPrimeiroDigito
        )
        if (primeiroDigito != numeroLimpo[primeiroDigitoIndex].digitToInt()) return false

        // Validação do segundo dígito verificador
        val segundoDigito = calcularDigitoVerificador(
            numeroLimpo.substring(0, segundoDigitoIndex), pesosSegundoDigito
        )
        return segundoDigito == numeroLimpo[segundoDigitoIndex].digitToInt()
    }
}

class ValidadorCpf : ValidadorComDigitoVerificador(
    tamanho = CPF_LENGTH,
    primeiroDigitoIndex = PRIMEIRO_DIGITO_INDEX,
    segundoDigitoIndex = SEGUNDO_DIGITO_INDEX,
    pesosPrimeiroDigito = PESOS_PRIMEIRO_DIGITO,
    pesosSegundoDigito = PESOS_SEGUNDO_DIGITO
) {
    private companion object {
        const val PRIMEIRO_DIGITO_INDEX = 9
        const val SEGUNDO_DIGITO_INDEX = 10
        val PESOS_PRIMEIRO_DIGITO = intArrayOf(10, 9, 8, 7, 6, 5, 4, 3, 2)
        val PESOS_SEGUNDO_DIGITO = intArrayOf(11, 10, 9, 8, 7, 6, 5, 4, 3, 2)
    }
}

class ValidadorCnpj : ValidadorComDigitoVerificador(
    tamanho = CNPJ_LENGTH,
    primeiroDigitoIndex = PRIMEIRO_DIGITO_INDEX,
    segundoDigitoIndex = SEGUNDO_DIGITO_INDEX,
    pesosPrimeiroDigito = PESOS_PRIMEIRO_DIGITO,
    pesosSegundoDigito = PESOS_SEGUNDO_DIGITO
) {
    private companion object {
        const val PRIMEIRO_DIGITO_INDEX = 12
        const val SEGUNDO_DIGITO_INDEX = 13
        val PESOS_PRIMEIRO_DIGITO = intArrayOf(5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2)
        val PESOS_SEGUNDO_DIGITO = intArrayOf(6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2)
    }
}


class FormatadorCnpj : FormatadorDocumento {
    override fun formata(numero: String): String {
        val cnpjLimpo = numero.replace(CARACTERES_NAO_NUMERICOS, "")
        if (cnpjLimpo.length != CNPJ_LENGTH) return numero

        return cnpjLimpo.replace(MASCARA_CNPJ, "$1.$2.$3/$4-$5")
    }

    private companion object {
        val MASCARA_CNPJ = Regex("(\\d{2})(\\d{3})(\\d{3})(\\d{4})(\\d{2})")
    }
}

class FormatadorCpf : FormatadorDocumento {
    override fun formata(numero: String): String {
        val cpfLimpo = numero.replace(CARACTERES_NAO_NUMERICOS, "")
        if (cpfLimpo.length != CPF_LENGTH) return numero

        return cpfLimpo.replace(MASCARA_CPF, "$1.$2.$3-$4")
    }

    private companion object {
        val MASCARA_CPF = Regex("(\\d{3})(\\d{3})(\\d{3})(\\d{2})")
    }
}