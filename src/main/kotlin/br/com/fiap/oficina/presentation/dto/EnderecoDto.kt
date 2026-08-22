package br.com.fiap.oficina.presentation.dto

data class EnderecoDto(
    val logradouro: String,
    val numero: String,
    val complemento: String?,
    val bairro: String,
    val cidade: String,
    val estado: String,
    val cep: String
)
