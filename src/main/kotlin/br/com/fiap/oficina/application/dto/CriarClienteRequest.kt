package br.com.fiap.oficina.application.dto

data class CriarClienteRequest(
    val nome: String,
    val numeroDocumento: String,
    val tipoPessoa: String,
    val email: String,
    val endereco: EnderecoRequest? = null,
    val contatos: List<ContatoRequest> = emptyList(),
)
