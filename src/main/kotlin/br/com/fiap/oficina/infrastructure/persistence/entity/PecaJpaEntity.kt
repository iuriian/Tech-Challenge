package br.com.fiap.oficina.infrastructure.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal
import java.util.UUID

@Entity
@Table(name = "pecas")
class PecaJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,

    @Column(nullable = false, unique = true, length = 10)
    var codigo: String = "",

    @Column(nullable = false, length = 100)
    var nome: String = "",

    @Column(length = 255)
    var descricao: String? = null,

    @Column(length = 100)
    var fabricante: String? = null,

    @Column(length = 100)
    var fornecedor: String? = null,

    @Column(name = "preco_de_compra", precision = 10, scale = 2)
    var precoDeCompra: BigDecimal? = null,

    @Column(name = "preco_de_venda", nullable = false, precision = 10, scale = 2)
    var precoDeVenda: BigDecimal = BigDecimal.ZERO,

    @Column(name = "qtd_estoque", nullable = false)
    var qtdEstoque: Int = 0,

    @Column(nullable = false)
    var ativo: Boolean = true
)
