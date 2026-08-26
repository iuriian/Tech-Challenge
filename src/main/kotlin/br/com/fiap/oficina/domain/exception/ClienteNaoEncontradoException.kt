package br.com.fiap.oficina.domain.exception

class ClienteNaoEncontradoException(message: String) : RuntimeException(message) {
    companion object {
        fun porId(id: String): ClienteNaoEncontradoException =
            ClienteNaoEncontradoException("Cliente não encontrado com o ID: $id")

        fun porNome(nome: String): ClienteNaoEncontradoException =
            ClienteNaoEncontradoException("Cliente não encontrado com o nome: $nome")

        fun porDocumento(documento: String): ClienteNaoEncontradoException =
            ClienteNaoEncontradoException("Cliente não encontrado com o documento: $documento")
    }
}
