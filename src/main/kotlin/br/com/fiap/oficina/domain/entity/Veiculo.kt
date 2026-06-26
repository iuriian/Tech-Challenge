package br.com.fiap.oficina.domain.entity

import br.com.fiap.oficina.domain.valueobject.Id

data class Veiculo(
    val id: Id,
    val marca: String,
    val nome: String,
    val modelo: String,
    val ano: String,
    val placa: String,
    val motorista: Cliente,
) {
    companion object {
        private val PLACA_REGEX = Regex("^[A-Za-z]{3}[0-9][A-Za-z0-9][0-9]{2}$")

        fun criar(
            marca: String,
            nome: String,
            modelo: String,
            ano: String,
            placa: String,
            motorista: Cliente,
        ): Veiculo {
            require(marca.isNotBlank()) { "Marca é obrigatória" }
            require(nome.isNotBlank()) { "Nome do veículo é obrigatório" }
            require(modelo.isNotBlank()) { "Modelo é obrigatório" }
            require(ano.isNotBlank()) { "Ano é obrigatório" }
            require(PLACA_REGEX.matches(placa)) {
                "Placa inválida: use o formato antigo (ABC1234) ou Mercosul (ABC1D23)"
            }

            return Veiculo(
                id = Id.generate(),
                marca = marca,
                nome = nome,
                modelo = modelo,
                ano = ano,
                placa = placa,
                motorista = motorista,
            )
        }
    }
}
