package br.com.fiap.oficina.application.mapper

import br.com.fiap.oficina.application.dto.PecaRequest
import br.com.fiap.oficina.application.dto.PecaResponse
import br.com.fiap.oficina.domain.entity.Peca
import org.springframework.stereotype.Component

@Component
class PecaMapper {
    fun toDomain(request: PecaRequest): Peca = Peca.criar(
        codigo = requireNotNull(request.codigo) { "Código da peça é obrigatório" },
        nome = request.nome,
        descricao = request.descricao,
        fabricante = request.fabricante,
        fornecedor = request.fornecedor,
        precoDeCompra = request.precoDeCompra?.toBigDecimal(),
        precoDeVenda = request.precoDeVenda.toBigDecimal(),
        qtdEstoque = request.qtdEstoque,
    )

    fun toResponse(peca: Peca): PecaResponse = PecaResponse(
        id = peca.id.valor.toString(),
        codigo = peca.codigo,
        nome = peca.nome,
        descricao = peca.descricao,
        fabricante = peca.fabricante,
        fornecedor = peca.fornecedor,
        precoDeCompra = peca.precoDeCompra?.toDouble(),
        precoDeVenda = peca.precoDeVenda.toDouble(),
        qtdEstoque = peca.qtdEstoque,
        ativo = peca.ativo,
    )
}
