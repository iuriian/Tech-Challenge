package br.com.fiap.oficina.application.mapper

import br.com.fiap.oficina.domain.entity.Funcionario
import br.com.fiap.oficina.presentation.dto.FuncionarioDto

fun Funcionario.toDto(): FuncionarioDto =
    FuncionarioDto(
        nome = nome,
        cargo = cargo.name,
    )
