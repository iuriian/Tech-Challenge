package br.com.fiap.oficina.domain.valueobject

import java.util.UUID

data class Id(val valor: UUID) {

    companion object {
        fun gerar(): Id = Id(UUID.randomUUID())

        fun from(valor: UUID): Id = Id(valor)
    }

    override fun toString(): String = valor.toString()
}
