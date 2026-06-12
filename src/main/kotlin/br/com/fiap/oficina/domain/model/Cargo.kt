package br.com.fiap.oficina.domain.model

enum class Cargo(
    val id: Int,
) {
    ATENDENTE(1),
    MECANICO(2),
    ;

    companion object {
        fun fromId(id: Int): Cargo = entries.first { it.id == id }

        fun fromName(name: String): Cargo = Cargo.valueOf(name)
    }
}
