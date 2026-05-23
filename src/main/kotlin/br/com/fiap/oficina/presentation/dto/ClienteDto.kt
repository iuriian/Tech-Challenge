package br.com.fiap.oficina.presentation.dto

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Size


data class ClienteDto(@Size(min = 5, max = 50) val nome: String,
                      val documento: String){

}