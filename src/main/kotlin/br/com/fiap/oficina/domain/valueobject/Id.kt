package br.com.fiap.oficina.domain.valueobject

import java.util.UUID

data class Id(val valor: UUID) {
    companion object {
        fun generate(): Id = Id(UUID.randomUUID())

        fun fromString(id: String): Id = Id(UUID.fromString(id))
    }
}
