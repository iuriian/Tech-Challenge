package br.com.fiap.oficina.application.mapper

import br.com.fiap.oficina.application.dto.AtualizarPecaRequest
import br.com.fiap.oficina.application.dto.CriarPecaRequest
import br.com.fiap.oficina.application.dto.PecaResponse
import br.com.fiap.oficina.domain.entity.Peca
import org.springframework.stereotype.Component

@Component
class PecaApplicationMapper {
    fun toDomain(request: CriarPecaRequest): Peca = Peca.criar(
        codigo = request.codigo,
        nome = request.nome,
        descricao = request.descricao,
        fabricante = request.fabricante,
        fornecedor = request.fornecedor,
        precoDeCompra = request.precoDeCompra,
        precoDeVenda = request.precoDeVenda,
        qtdEstoque = request.qtdEstoque,
    )

    fun toDomain(codigo: String, request: AtualizarPecaRequest): Peca = Peca.criar(
        codigo = codigo,
        nome = request.nome,
        descricao = request.descricao,
        fabricante = request.fabricante,
        fornecedor = request.fornecedor,
        precoDeCompra = request.precoDeCompra,
        precoDeVenda = request.precoDeVenda,
        qtdEstoque = 0,
    )

    fun toResponse(peca: Peca): PecaResponse = PecaResponse(
        id = peca.id.valor.toString(),
        codigo = peca.codigo,
        nome = peca.nome,
        descricao = peca.descricao,
        fabricante = peca.fabricante,
        fornecedor = peca.fornecedor,
        precoDeCompra = peca.precoDeCompra,
        precoDeVenda = peca.precoDeVenda,
        qtdEstoque = peca.qtdEstoque,
        ativo = peca.ativo,
    )
}
