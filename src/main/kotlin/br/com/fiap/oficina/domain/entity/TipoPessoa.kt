package br.com.fiap.oficina.domain.entity


interface ValidadorDocumento{
    fun valida(documento: Documento): Boolean
}

class ValidadorCpf : ValidadorDocumento{
    override fun valida(documento: Documento): Boolean {
        return true
    }
}

class ValidadorCnpj : ValidadorDocumento{
    override fun valida(documento: Documento): Boolean {
        return true
    }
}

enum class TipoPessoa(private val validadorDocumento: ValidadorDocumento) {

    PESSOA_JURIDICA(ValidadorCnpj()),
    PESSOA_FISICA(ValidadorCpf());

    fun valida(documento: Documento) = this.validadorDocumento.valida(documento)

}