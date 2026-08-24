package br.com.fiap.oficina.infrastructure.persistence.entity.servico

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal
import java.util.UUID

@Entity
@Table(name = "catalogo_servicos")
class ServicoCatalogoJpaEntity(
    @Id
    var id: UUID = UUID.randomUUID(),

    @Column(nullable = false, length = 100)
    var descricao: String = "",

    @Column(nullable = false, precision = 10, scale = 2)
    var valor: BigDecimal = BigDecimal.ZERO,

    @Column(nullable = false)
    var ativo: Boolean = true,
)