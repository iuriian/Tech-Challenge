package br.com.fiap.oficina.presentation.dto

import br.com.fiap.oficina.infrastructure.persistence.entity.Cliente
import jakarta.validation.constraints.Size

data class VeiculoDTO(@Size(min=3, max=20) val nome: String,
                      val marca: String,
                      val modelo: String,
                      val ano: String,
                      @Size(min = 7, max = 7) val placa: String,
                      val motorista: Cliente? = null){}


/* oficina/domain/entity/Veiculo.kt
*   @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var idVeiculo: Long? = null

    @Column(nullable = false)
    lateinit var nome: String

    @Column(nullable = false)
    lateinit var modelo: String

    @Column(nullable = false)
    lateinit var ano: String

    @Column(nullable = false, unique = true)
    @Size(min = 7, max = 7, message = "Input must be exactly 7 characters long")
    lateinit var placa: String

    @Column(nullable = false, unique = true)
    lateinit var motorista: Cliente
* */