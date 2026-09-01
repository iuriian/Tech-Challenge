package br.com.fiap.oficina.application.service

import br.com.fiap.oficina.application.dto.PecaRequest
import br.com.fiap.oficina.application.dto.PecaResponse
import br.com.fiap.oficina.application.mapper.PecaMapper
import br.com.fiap.oficina.domain.usecase.peca.AtualizarPecaUseCase
import br.com.fiap.oficina.domain.usecase.peca.BuscarPecaPorCodigoUseCase
import br.com.fiap.oficina.domain.usecase.peca.BuscarPecaPorNomeUseCase
import br.com.fiap.oficina.domain.usecase.peca.CriarPecaUseCase
import br.com.fiap.oficina.domain.usecase.peca.DeletarPecaUseCase
import br.com.fiap.oficina.domain.usecase.peca.ListarPecasUseCase
import br.com.fiap.oficina.domain.usecase.peca.ReativarPecaUseCase
import br.com.fiap.oficina.domain.usecase.peca.ReporPecasUseCase
import br.com.fiap.oficina.domain.usecase.peca.RetirarPecasUseCase
import org.springframework.stereotype.Service

@Service
class PecaService(
    private val criarPecaUseCase: CriarPecaUseCase,
    private val listarPecasUseCase: ListarPecasUseCase,
    private val buscarPecaPorCodigoUseCase: BuscarPecaPorCodigoUseCase,
    private val buscarPecaPorNomeUseCase: BuscarPecaPorNomeUseCase,
    private val atualizarPecaUseCase: AtualizarPecaUseCase,
    private val retirarPecasUseCase: RetirarPecasUseCase,
    private val reporPecasUseCase: ReporPecasUseCase,
    private val reativarPecaUseCase: ReativarPecaUseCase,
    private val deletarPecaUseCase: DeletarPecaUseCase,
    private val mapper: PecaMapper,
) {
    fun criar(request: PecaRequest): PecaResponse {
        val peca = mapper.toDomain(request)
        val response = criarPecaUseCase.executar(peca)
        return mapper.toResponse(response)
    }

    fun listar(): List<PecaResponse> = listarPecasUseCase.executar().map { mapper.toResponse(it) }

    fun buscarPorCodigo(codigo: String): PecaResponse {
        val resultado = buscarPecaPorCodigoUseCase.executar(codigo)
        return mapper.toResponse(resultado)
    }

    fun buscarPorNome(nome: String): PecaResponse {
        val resultado = buscarPecaPorNomeUseCase.executar(nome)
        return mapper.toResponse(resultado)
    }

    fun atualizar(codigo: String, request: PecaRequest): PecaResponse {
        val dadosAtualizados = mapper.toDomain(request.copy(codigo = codigo))
        val response = atualizarPecaUseCase.executar(codigo, dadosAtualizados)
        return mapper.toResponse(response)
    }

    fun retirar(codigo: String, qtd: Int): PecaResponse {
        val response = retirarPecasUseCase.executar(codigo, qtd)
        return mapper.toResponse(response)
    }

    fun repor(codigo: String, qtd: Int): PecaResponse {
        val response = reporPecasUseCase.executar(codigo, qtd)
        return mapper.toResponse(response)
    }

    fun reativar(codigo: String): Boolean = reativarPecaUseCase.executar(codigo)

    fun deletar(codigo: String): Boolean = deletarPecaUseCase.executar(codigo)
}
