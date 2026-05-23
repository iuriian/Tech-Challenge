package br.com.fiap.oficina.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Transient

@Embeddable
class Documento(
    @Column(name = "documento_numero", nullable = false, unique = true)
    val numero: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_pessoa", nullable = false)
    val tipoPessoa: TipoPessoa
) {

    protected constructor() : this("", TipoPessoa.PESSOA_FISICA)

    fun isFormatoValido(): Boolean = this.tipoPessoa.valida(this)

    @Transient
    fun getNumeroFormatado(): String = this.tipoPessoa.formata(numero)


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Documento) return false
        val thisLimpo = numero.replace(Regex("[^0-9]"), "")
        val otherLimpo = other.numero.replace(Regex("[^0-9]"), "")
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
        fun cpf(numero: String) = Documento(numero, TipoPessoa.PESSOA_FISICA)
        fun cnpj(numero: String) = Documento(numero, TipoPessoa.PESSOA_JURIDICA)
    }
}