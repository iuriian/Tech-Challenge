package br.com.fiap.oficina.infrastructure.persistence.mapper

import br.com.fiap.oficina.domain.entity.Funcionario
import br.com.fiap.oficina.domain.enum.Cargo
import br.com.fiap.oficina.domain.valueobject.Id
import br.com.fiap.oficina.infrastructure.persistence.jpa.entity.FuncionarioEntity

fun Funcionario.toEntity(): FuncionarioEntity = FuncionarioEntity(
    id = id.valor,
    nome = nome,
    cargo = cargo.id,
)

fun FuncionarioEntity.toDomain(): Funcionario = Funcionario(
    id = Id(id),
    nome = nome,
    cargo = Cargo.fromId(cargo),
)
