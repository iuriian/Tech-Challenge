package br.com.fiap.oficina.domain.entity

import jakarta.persistence.*

@Entity
@Table(name = "enderecos")
class Endereco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(nullable = false)
    lateinit var logradouro: String

    @Column(nullable = false)
    lateinit var numero: String

    @Column(nullable = true)
    var complemento: String? = null

    @Column(nullable = false)
    lateinit var bairro: String

    @Column(nullable = false)
    lateinit var cidade: String

    @Column(nullable = false)
    lateinit var estado: String

    @Column(nullable = false)
    lateinit var cep: String

    @OneToOne(mappedBy = "endereco")
    var cliente: Cliente? = null
}
