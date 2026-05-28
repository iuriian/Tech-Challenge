package br.com.fiap.oficina.infrastructure.persistence.entity

import jakarta.persistence.*
import jakarta.validation.constraints.Size

@Entity
@Table(name = "veiculos")
class Veiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var idVeiculo: Long? = null

    @Column(nullable = false)
    lateinit var marca: String

    @Column(nullable = false)
    lateinit var nome: String

    @Column(nullable = false)
    lateinit var modelo: String

    @Column(nullable = false)
    lateinit var ano: String

    @Column(nullable = false, unique = true)
    @Size(min = 7, max = 7, message = "Input must be exactly 7 characters long")
    lateinit var placa: String

    @ManyToOne(cascade = [(CascadeType.ALL)])
    lateinit var motorista: Cliente

}