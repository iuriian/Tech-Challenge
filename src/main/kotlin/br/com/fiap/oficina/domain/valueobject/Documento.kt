package br.com.fiap.oficina.domain.valueobject

class Documento(
    val numero: String,
    val tipoPessoa: TipoPessoa
) {

    fun isFormatoValido(): Boolean = this.tipoPessoa.valida(this)

    fun getNumeroFormatado(): String = this.tipoPessoa.formata(numero)


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Documento) return false
        val thisLimpo = numero.replace(Regex(NUMBER_PATTERN), "")
        val otherLimpo = other.numero.replace(Regex(NUMBER_PATTERN), "")
        return thisLimpo == otherLimpo && tipoPessoa == other.tipoPessoa
    }

    override fun hashCode(): Int {
        val limpo = numero.replace(Regex("[^0-9]"), "")
        return 31 * limpo.hashCode() + tipoPessoa.hashCode()
    }

    override fun toString(): String {
        return "Documento(numero='${getNumeroFormatado()}', tipoPessoa=$tipoPessoa)"
    }

    companion object {
        private const val NUMBER_PATTERN = "[ˆ0-9]"
        fun cpf(numero: String) = Documento(numero, TipoPessoa.PESSOA_FISICA)
        fun cnpj(numero: String) = Documento(numero, TipoPessoa.PESSOA_JURIDICA)
    }
}
