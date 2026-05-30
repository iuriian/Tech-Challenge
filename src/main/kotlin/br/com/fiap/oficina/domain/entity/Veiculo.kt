package br.com.fiap.oficina.domain.entity

class Veiculo {

    var idVeiculo: Long? = null

    lateinit var marca: String

    lateinit var nome: String

    lateinit var modelo: String

    lateinit var ano: String

    lateinit var placa: String

    lateinit var motorista: Cliente
}
