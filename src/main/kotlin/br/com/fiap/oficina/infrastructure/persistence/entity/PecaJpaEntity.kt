package br.com.fiap.oficina.infrastructure.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal

@Entity
@Table(name = "pecas")
class PecaJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(nullable = false, unique = true)
    lateinit var codigo: String

    @Column(nullable = false)
    lateinit var nome: String

    @Column
    var descricao: String? = null

    @Column
    var fabricante: String? = null

    @Column
    var fornecedor: String? = null

    @Column(name = "preco_de_compra", precision = 10, scale = 2)
    var precoDeCompra: BigDecimal? = null

    @Column(name = "preco_de_venda", nullable = false, precision = 10, scale = 2)
    var precoDeVenda: BigDecimal = BigDecimal.ZERO

    @Column(name = "qtd_estoque", nullable = false)
    var qtdEstoque: Int = 0

    @Column(nullable = false)
    var ativo: Boolean = true
}
