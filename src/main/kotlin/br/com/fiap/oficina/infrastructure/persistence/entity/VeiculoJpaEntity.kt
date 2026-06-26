package br.com.fiap.oficina.infrastructure.persistence.entity

import jakarta.persistence.*
import jakarta.validation.constraints.Size
import java.util.UUID

@Entity
@Table(name = "veiculos")
class VeiculoJpaEntity {

    @Id
    var idVeiculo: UUID = UUID.randomUUID()

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

    @ManyToOne(cascade = [CascadeType.MERGE])
    lateinit var motorista: ClienteJpaEntity

}
