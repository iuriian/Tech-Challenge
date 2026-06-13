package br.com.fiap.oficina.domain.entity

import br.com.fiap.oficina.domain.valueobject.Id

data class Veiculo(
    val id: Id,
    val marca: String,
    val nome: String,
    val modelo: String,
    val ano: String,
    val placa: String,
    val motorista: Cliente
) {

    companion object {
        const val PLACA_TAMANHO = 7

        fun criar(
            marca: String,
            nome: String,
            modelo: String,
            ano: String,
            placa: String,
            motorista: Cliente
        ): Veiculo {
            require(marca.isNotBlank()) { "Marca é obrigatória" }
            require(nome.isNotBlank()) { "Nome do veículo é obrigatório" }
            require(modelo.isNotBlank()) { "Modelo é obrigatório" }
            require(ano.isNotBlank()) { "Ano é obrigatório" }
            require(placa.length == PLACA_TAMANHO) { "Placa deve ter exatamente $PLACA_TAMANHO caracteres" }

            return Veiculo(
                id = Id.gerar(),
                marca = marca,
                nome = nome,
                modelo = modelo,
                ano = ano,
                placa = placa,
                motorista = motorista
            )
        }
    }
}
