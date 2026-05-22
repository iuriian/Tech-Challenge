package br.com.fiap.oficina.domain.entity

import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "clientes")
class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null

    @Column(nullable = false)
    lateinit var nome: String

    @Column(nullable = false, unique = true)
    lateinit var cpf: String



}