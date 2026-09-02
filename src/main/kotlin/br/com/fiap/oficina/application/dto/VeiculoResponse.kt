package br.com.fiap.oficina.application.dto

data class VeiculoResponse(
    val id: String,
    val nome: String,
    val marca: String,
    val modelo: String,
    val ano: String,
    val placa: String,
    val motoristaId: String,
)
