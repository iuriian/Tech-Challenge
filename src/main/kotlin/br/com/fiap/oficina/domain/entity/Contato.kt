package br.com.fiap.oficina.domain.entity

import br.com.fiap.oficina.domain.valueobject.Id

data class Contato(
    val id: Id,
    val tipo: String,
    val nome: String,
    val telefone: String,
) {
    companion object {
        fun criar(
            tipo: String,
            nome: String,
            telefone: String,
        ): Contato {
            require(tipo.isNotBlank()) { "Tipo do contato é obrigatório" }
            require(nome.isNotBlank()) { "Nome do contato é obrigatório" }
            require(telefone.isNotBlank()) { "Telefone do contato é obrigatório" }

            return Contato(
                id = Id.generate(),
                tipo = tipo,
                nome = nome,
                telefone = telefone,
            )
        }
    }
}
