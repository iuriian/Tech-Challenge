package br.com.fiap.oficina.application.servico.exception

import br.com.fiap.oficina.domain.valueobject.Id

class ServicoNaoEncontradoException(
    id: Id,
) : RuntimeException(
    "Serviço não encontrado com o ID: $id",
)