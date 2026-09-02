package br.com.fiap.oficina.domain.exception

class VeiculoNaoEncontradoException(message: String) : RuntimeException(message) {
    companion object {
        fun porId(id: String): VeiculoNaoEncontradoException =
            VeiculoNaoEncontradoException("Veículo não encontrado com o ID: $id")

        fun porPlaca(placa: String): VeiculoNaoEncontradoException =
            VeiculoNaoEncontradoException("Veículo não encontrado com a placa: $placa")
    }
}
