package br.com.fiap.oficina.domain.entity

import br.com.fiap.oficina.domain.enum.Cargo
import br.com.fiap.oficina.domain.valueobject.Id

data class Funcionario(val id: Id, val nome: String, val cargo: Cargo) {
    companion object {
        fun criar(nome: String, cargo: String): Funcionario = Funcionario(
            id = Id.generate(),
            nome = nome,
            cargo = Cargo.fromName(cargo),
        )

        fun reconstruir(id: String, nome: String, cargo: String): Funcionario = Funcionario(
            id = Id.fromString(id),
            nome = nome,
            cargo = Cargo.fromName(cargo),
        )
    }
}
