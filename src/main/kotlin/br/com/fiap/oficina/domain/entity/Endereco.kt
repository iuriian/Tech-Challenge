package br.com.fiap.oficina.domain.entity

class Endereco {

    var id: Long? = null

    lateinit var logradouro: String

    lateinit var numero: String

    var complemento: String? = null

    lateinit var bairro: String

    lateinit var cidade: String

    lateinit var estado: String

    lateinit var cep: String

    var cliente: Cliente? = null
}
