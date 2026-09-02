package br.com.fiap.oficina.application.dto

import jakarta.validation.constraints.NotBlank

data class FuncionarioRequest(
    @field:NotBlank
    val nome: String,
    @field:NotBlank
    val cargo: String,
    val id: String? = null,
)
