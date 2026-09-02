package br.com.fiap.oficina.domain.exception

import br.com.fiap.oficina.domain.valueobject.Id

class ServicoNaoEncontradoException(id: Id) :
    RuntimeException(
        "Serviço não encontrado com o ID: $id",
    )
