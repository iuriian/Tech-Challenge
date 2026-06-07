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
    var funcionarioId: Long? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", referencedColumnName = "id")
    lateinit var cliente: ClienteJpaEntity

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veiculo_id", referencedColumnName = "idVeiculo", nullable = false)
    lateinit var veiculo: VeiculoJpaEntity

    @ManyToMany
    @JoinTable(
        name = "servico_pecas",
        joinColumns = [JoinColumn(name = "servico_id")],
        inverseJoinColumns = [JoinColumn(name = "peca_id")]
    )
    var pecas: List<PecaJpaEntity> = mutableListOf()
}
