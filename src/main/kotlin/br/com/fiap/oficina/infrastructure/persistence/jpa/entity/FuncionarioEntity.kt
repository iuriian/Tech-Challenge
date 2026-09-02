package br.com.fiap.oficina.infrastructure.persistence.jpa.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "funcionarios")
class FuncionarioEntity(
    @Id
    var id: UUID,
    var nome: String,
    var cargo: Int,
)
