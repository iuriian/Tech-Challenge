package br.com.fiap.oficina.presentation.mapper

import br.com.fiap.oficina.domain.entity.Funcionario
import br.com.fiap.oficina.presentation.dto.FuncionarioDto

fun Funcionario.toDto(): FuncionarioDto = FuncionarioDto(
    id = id.valor.toString(),
    nome = nome,
    cargo = cargo.name,
)
