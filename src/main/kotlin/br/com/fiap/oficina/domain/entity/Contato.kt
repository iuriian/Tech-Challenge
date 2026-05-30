package br.com.fiap.oficina.domain.entity

class Contato {

    var id: Long? = null

    lateinit var tipo: String

    lateinit var nome: String

    lateinit var telefone: String

    var cliente: Cliente? = null
}
