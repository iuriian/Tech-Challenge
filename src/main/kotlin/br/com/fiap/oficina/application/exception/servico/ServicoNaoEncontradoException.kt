package br.com.fiap.oficina.application.exception.servico

import br.com.fiap.oficina.domain.valueobject.Id

class ServicoNaoEncontradoException(
    id: Id,
) : RuntimeException(
    "Serviço não encontrado com o ID: $id",
)