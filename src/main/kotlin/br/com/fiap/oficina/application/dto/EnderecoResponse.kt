package br.com.fiap.oficina.application.dto

data class EnderecoResponse(
    val logradouro: String,
    val numero: String,
    val complemento: String?,
    val bairro: String,
    val cidade: String,
    val estado: String,
    val cep: String,
)
