package br.com.fiap.oficina.infrastructure.persistence.entity

import br.com.fiap.oficina.domain.enum.OrdemServicoStatus
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "servicos")
class ServicoJpaEntity(
    @Id
    var id: UUID,
    @Column(nullable = false)
    var descricao: String,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: OrdemServicoStatus? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "funcionario_id", referencedColumnName = "id", nullable = false)
    var funcionario: FuncionarioEntity,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", referencedColumnName = "id")
    var cliente: ClienteJpaEntity,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veiculo_id", referencedColumnName = "idVeiculo", nullable = false)
    var veiculo: VeiculoJpaEntity,
    @OneToMany(mappedBy = "servico", cascade = [CascadeType.ALL], orphanRemoval = true)
    var pecas: MutableList<PecaServicoJpaEntity> = mutableListOf(),
    @Column(name = "data_abertura", nullable = false)
    var dataAbertura: Instant,
    @Column(name = "data_inicio_execucao")
    var dataInicioExecucao: Instant? = null,
    @Column(name = "data_finalizacao")
    var dataFinalizacao: Instant? = null,
)