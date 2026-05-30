package br.com.fiap.oficina.infrastructure.persistence.entity

import br.com.fiap.oficina.domain.enum.ServicoStatus
import jakarta.persistence.*

@Entity
@Table(name = "servicos")
class ServicoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(nullable = false)
    lateinit var descricao: String

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: ServicoStatus? = null

    @Column(name = "funcionario_id", nullable = false)
    var funcionarioId: String? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", referencedColumnName = "id")
    lateinit var cliente: ClienteJpaEntity

    @Column(name = "veiculo_id", nullable = false)
    var veiculoId: Long? = null

    @ElementCollection
    @CollectionTable(name = "servico_pecas", joinColumns = [JoinColumn(name = "servico_id")])
    @Column(name = "peca_id")
    var pecasIds: List<Long> = mutableListOf()
}
