package br.com.fiap.oficina.domain.repository

import br.com.fiap.oficina.domain.model.Funcionario
import br.com.fiap.oficina.domain.valueobject.Id

interface FuncionarioRepository {
    fun salvar(funcionario: Funcionario): Funcionario

    fun listarTodos(): List<Funcionario>

    fun buscarPorId(id: Id): Funcionario?

    fun editar(funcionario: Funcionario): Funcionario

    fun deletar(id: Id)
}
