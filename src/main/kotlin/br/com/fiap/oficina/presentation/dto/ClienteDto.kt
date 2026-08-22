package br.com.fiap.oficina.presentation.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Size
import java.util.UUID


data class ClienteDto(@Size(min = 5, max = 50) val nome: String,
                      val numeroDocumento: String,
                      val tipoPessoa: String,
                      @Email  val email: String,
                      val endereco: EnderecoDto? = null,
                      val contatos: List<ContatoDto> = emptyList(),
                      val id: UUID? = null)
