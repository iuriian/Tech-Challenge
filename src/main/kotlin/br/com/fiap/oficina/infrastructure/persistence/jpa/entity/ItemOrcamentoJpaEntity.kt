package br.com.fiap.oficina.infrastructure.persistence.jpa.entity

import br.com.fiap.oficina.domain.enum.TipoItemOrcamento
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
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
@Table(name = "itens_orcamento")
class ItemOrcamentoJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    lateinit var id: UUID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "ordem_servico_id",
        referencedColumnName = "id",
        nullable = false,
    )
    lateinit var ordemServico: OrdemServicoJpaEntity

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    lateinit var tipo: TipoItemOrcamento

    @Column(name = "referencia_id", nullable = false)
    lateinit var referenciaId: UUID

    @Column(nullable = false, length = 100)
    lateinit var descricao: String

    @Column(
        name = "valor_unitario",
        nullable = false,
        precision = 10,
        scale = 2,
    )
    var valorUnitario: BigDecimal = BigDecimal.ZERO

    @Column(
        nullable = false,
        precision = 10,
        scale = 2,
    )
    var quantidade: BigDecimal = BigDecimal.ZERO

    @Column(name = "codigo_referencia", length = 50)
    var codigoReferencia: String? = null
}
