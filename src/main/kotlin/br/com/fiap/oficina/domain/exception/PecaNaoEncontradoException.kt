package br.com.fiap.oficina.domain.exception

class PecaNaoEncontradoException(message: String) : RuntimeException(message) {
    companion object {
        fun porCodigo(codigo: String): PecaNaoEncontradoException =
            PecaNaoEncontradoException("Peça não encontrada com o código: $codigo")

        fun porNome(nome: String): PecaNaoEncontradoException =
            PecaNaoEncontradoException("Peça não encontrada com o nome: $nome")
    }
}
