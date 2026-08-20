package br.com.fiap.oficina.domain.repository

import br.com.fiap.oficina.domain.entity.Peca
import br.com.fiap.oficina.domain.valueobject.Id

interface PecaRepository {
    fun salvar(peca: Peca): Peca

    fun listarAtivos(): List<Peca>

    fun buscarAtivoPorCodigo(codigo: String): Peca?

    fun buscarAtivoPorNome(nome: String): Peca?

    fun existeAtivoPorCodigo(codigo: String): Boolean

    fun buscarPorCodigo(codigo: String): Peca?

    fun existePorCodigo(codigo: String): Boolean

    fun buscarPorId(id: Id): Peca?
}
