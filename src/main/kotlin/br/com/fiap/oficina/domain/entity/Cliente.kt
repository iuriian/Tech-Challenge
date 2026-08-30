package br.com.fiap.oficina.domain.entity

import br.com.fiap.oficina.domain.valueobject.Documento
import br.com.fiap.oficina.domain.valueobject.Id

data class Cliente(
    val id: Id,
    val nome: String,
    val documento: Documento,
    val email: String,
    val endereco: Endereco? = null,
    val contatos: List<Contato> = emptyList(),
) {
    companion object {
        fun criar(
            nome: String,
            documento: Documento,
            email: String,
            endereco: Endereco? = null,
            contatos: List<Contato> = emptyList(),
        ): Cliente {
            require(nome.isNotBlank()) { "Nome do cliente é obrigatório" }
            require(email.isNotBlank()) { "E-mail do cliente é obrigatório" }
            require(documento.isFormatoValido()) { "Documento inválido" }

            return Cliente(
                id = Id.generate(),
                nome = nome,
                documento = documento,
                email = email,
                endereco = endereco,
                contatos = contatos,
            )
        }

        fun reconstruir(
            id: String,
            nome: String,
            documento: Documento,
            email: String,
            endereco: Endereco? = null,
            contatos: List<Contato> = emptyList(),
        ): Cliente = Cliente(
            id = Id.fromString(id),
            nome = nome,
            documento = documento,
            email = email,
            endereco = endereco,
            contatos = contatos,
        )
    }
}
