package br.com.fiap.oficina.infrastructure.persistence.jpa.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.validation.constraints.Size
import java.util.UUID

@Entity
@Table(name = "veiculos")
class VeiculoJpaEntity(
    @Id
    var idVeiculo: UUID,
    @Column(nullable = false)
    var marca: String,
    @Column(nullable = false)
    var nome: String,
    @Column(nullable = false)
    var modelo: String,
    @Column(nullable = false)
    var ano: String,
    @Column(nullable = false, unique = true)
    @Size(min = 7, max = 7, message = "Input must be exactly 7 characters long")
    var placa: String,
    @ManyToOne
    @JoinColumn(name = "motorista_id")
    var motorista: ClienteJpaEntity,
)
