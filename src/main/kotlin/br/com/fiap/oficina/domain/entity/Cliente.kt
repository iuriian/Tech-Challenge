package br.com.fiap.oficina.domain.entity

import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "clientes")
class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(nullable = false)
    lateinit var nome: String

    @Column(nullable = false, unique = true)
    lateinit var cpf: String


    lateinit var tipoPessoa: TipoPessoa

//    fun validaDocumento() = tipoPessoa.valida(Documento(this.))

    /*

    PJ

    Razao Social
    Fantasia
    cnpj (documento federal)


    List<Endereco>
    List<Contato>
    Email

    PF

    Nome
    Cpj (doc federal)
    Nascimento


    Endereco




     */




}