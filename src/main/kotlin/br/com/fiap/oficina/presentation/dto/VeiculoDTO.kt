package br.com.fiap.oficina.presentation.dto

import br.com.fiap.oficina.domain.entity.Cliente
import jakarta.validation.constraints.Size

data class VeiculoDTO(@Size(min=3, max=20) val nome: String,
                      val marca: String,
                      val modelo: String,
                      val ano: String,
                      @Size(min = 7, max = 7) val placa: String,
                      val motorista: Cliente? = null)
