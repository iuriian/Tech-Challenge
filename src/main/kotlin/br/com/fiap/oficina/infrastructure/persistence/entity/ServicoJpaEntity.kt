package br.com.fiap.oficina.infrastructure.persistence.entity

import br.com.fiap.oficina.domain.enum.ServicoStatus
import jakarta.persistence.*
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "servicos")
class ServicoJpaEntity {

    @Id
    var id: UUID = UUID.randomUUID()

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

    @OneToMany(mappedBy = "servico", cascade = [CascadeType.ALL], orphanRemoval = true)
    var pecas: MutableList<PecaServicoJpaEntity> = mutableListOf()

    @Column(name = "data_abertura", nullable = false)
    lateinit var dataAbertura: Instant

    @Column(name = "data_inicio_execucao")
    var dataInicioExecucao: Instant? = null

    @Column(name = "data_finalizacao")
    var dataFinalizacao: Instant? = null
}
