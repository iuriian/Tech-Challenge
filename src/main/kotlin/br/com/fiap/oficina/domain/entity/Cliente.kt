package br.com.fiap.oficina.domain.entity

import br.com.fiap.oficina.domain.valueobject.Documento

class Cliente {

    var id: Long? = null

    lateinit var nome: String

    lateinit var documento: Documento

    lateinit var email: String

    var endereco: Endereco? = null

    var contatos: MutableList<Contato> = mutableListOf()
}
