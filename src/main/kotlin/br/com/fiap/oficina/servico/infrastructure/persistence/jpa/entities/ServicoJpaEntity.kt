package br.com.fiap.oficina.servico.infrastructure.persistence.jpa.entities

import br.com.fiap.oficina.servico.domain.enums.OrdemServicoStatus
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "servicos")
class ServicoJpaEntity(
    @Id
    var id: UUID,

    @Column(name = "os_number", length = 50)
    var osNumber: String? = null,

    @Column(name = "prazo_minutos")
    var prazoMinutos: Long? = null,

    @Column(nullable = false)
    var descricao: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: OrdemServicoStatus? = null,

    @Column(name = "funcionario_id", nullable = false)
    var funcionarioId: UUID,

    @Column(name = "cliente_id", nullable = false)
    var clienteId: UUID,

    @Column(name = "veiculo_id", nullable = false)
    var veiculoId: UUID,

    @OneToMany(mappedBy = "servico", cascade = [CascadeType.ALL], orphanRemoval = true)
    var pecas: MutableList<PecaServicoJpaEntity> = mutableListOf(),

    @Column(name = "data_abertura", nullable = false)
    var dataAbertura: Instant,

    @Column(name = "data_inicio_execucao")
    var dataInicioExecucao: Instant? = null,

    @Column(name = "data_finalizacao")
    var dataFinalizacao: Instant? = null,
)
