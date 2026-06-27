package br.com.fiap.oficina.domain.entity

import br.com.fiap.oficina.domain.valueobject.Id

data class Endereco(
    val id: Id,
    val logradouro: String,
    val numero: String,
    val complemento: String? = null,
    val bairro: String,
    val cidade: String,
    val estado: String,
    val cep: String,
) {
    companion object {
        fun criar(
            logradouro: String,
            numero: String,
            complemento: String? = null,
            bairro: String,
            cidade: String,
            estado: String,
            cep: String,
        ): Endereco {
            require(logradouro.isNotBlank()) { "Logradouro é obrigatório" }
            require(numero.isNotBlank()) { "Número é obrigatório" }
            require(bairro.isNotBlank()) { "Bairro é obrigatório" }
            require(cidade.isNotBlank()) { "Cidade é obrigatória" }
            require(estado.isNotBlank()) { "Estado é obrigatório" }
            require(cep.isNotBlank()) { "CEP é obrigatório" }

            return Endereco(
                id = Id.generate(),
                logradouro = logradouro,
                numero = numero,
                complemento = complemento,
                bairro = bairro,
                cidade = cidade,
                estado = estado,
                cep = cep,
            )
        }
    }
}
