package br.com.fiap.oficina.domain.entity

import jakarta.persistence.*

@Entity
@Table(name = "servicos")
class Servico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(nullable = false)
    lateinit var descricao: String

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", referencedColumnName = "id")
    lateinit var cliente: Cliente

    @Column(name = "veiculo_id", nullable = false)
    var veiculoId: String? = null

    @Column(name = "pecas_servico", nullable = false)
    var pecasIds: List<Long>? = null

    @Column(name = "funcionario_id",nullable = false)
    var funcionarioId: String? = null
}