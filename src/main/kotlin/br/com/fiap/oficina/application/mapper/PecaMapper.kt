package br.com.fiap.oficina.application.mapper

import br.com.fiap.oficina.application.dto.AtualizarPecaRequest
import br.com.fiap.oficina.application.dto.PecaRequest
import br.com.fiap.oficina.application.dto.PecaResponse
import br.com.fiap.oficina.domain.entity.Peca
import br.com.fiap.oficina.presentation.dto.PecaAtualizacaoDto
import org.springframework.stereotype.Component
import java.util.UUID
import br.com.fiap.oficina.presentation.dto.PecaDto as PecaPresentationDto

@Component
class PecaMapper {
    fun toDomain(request: PecaRequest): Peca = Peca.criar(
        codigo = request.codigo,
        nome = request.nome,
        descricao = request.descricao,
        fabricante = request.fabricante,
        fornecedor = request.fornecedor,
        precoDeCompra = request.precoDeCompra,
        precoDeVenda = request.precoDeVenda,
        qtdEstoque = request.qtdEstoque,
    )

    fun toPecaRequest(codigo: String, request: AtualizarPecaRequest): PecaRequest = PecaRequest(
        codigo = codigo,
        nome = request.nome,
        descricao = request.descricao,
        fabricante = request.fabricante,
        fornecedor = request.fornecedor,
        precoDeCompra = request.precoDeCompra,
        precoDeVenda = request.precoDeVenda,
        qtdEstoque = request.qtdEstoque,
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

    fun toCriarRequest(dto: PecaPresentationDto): PecaRequest = PecaRequest(
        codigo = dto.codigo,
        nome = dto.nome,
        descricao = dto.descricao,
        fabricante = dto.fabricante,
        fornecedor = dto.fornecedor,
        precoDeCompra = dto.precoDeCompra,
        precoDeVenda = dto.precoDeVenda,
        qtdEstoque = dto.qtdEstoque,
    )

    fun toAtualizarRequest(dto: PecaAtualizacaoDto): AtualizarPecaRequest = AtualizarPecaRequest(
        nome = dto.nome,
        descricao = dto.descricao,
        fabricante = dto.fabricante,
        fornecedor = dto.fornecedor,
        precoDeCompra = dto.precoDeCompra,
        precoDeVenda = dto.precoDeVenda,
    )

    fun toDto(response: PecaResponse): PecaPresentationDto = PecaPresentationDto(
        id = UUID.fromString(response.id),
        codigo = response.codigo,
        nome = response.nome,
        descricao = response.descricao,
        fabricante = response.fabricante,
        fornecedor = response.fornecedor,
        precoDeCompra = response.precoDeCompra,
        precoDeVenda = response.precoDeVenda,
        qtdEstoque = response.qtdEstoque,
        ativo = response.ativo,
    )
}
