package br.com.fiap.oficina.infrastructure.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.math.BigDecimal
import java.util.UUID

@Entity
@Table(name = "servico_pecas")
class PecaServicoJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    lateinit var id: UUID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "servico_id", referencedColumnName = "id", nullable = false)
    lateinit var servico: ServicoJpaEntity

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "peca_id", referencedColumnName = "id", nullable = false)
    lateinit var peca: PecaJpaEntity

    @Column(nullable = false, precision = 10, scale = 2)
    var quantidade: BigDecimal = BigDecimal.ZERO
}
