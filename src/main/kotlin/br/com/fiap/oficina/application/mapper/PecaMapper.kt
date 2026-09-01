package br.com.fiap.oficina.application.mapper

import br.com.fiap.oficina.application.dto.AtualizarPecaRequest
import br.com.fiap.oficina.application.dto.PecaDto
import br.com.fiap.oficina.application.dto.PecaResponse
import br.com.fiap.oficina.domain.entity.Peca
import br.com.fiap.oficina.presentation.dto.PecaAtualizacaoDto
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.util.UUID
import br.com.fiap.oficina.presentation.dto.PecaDto as PecaPresentationDto

@Component
class PecaMapper {
    fun toDomain(request: PecaDto): Peca = toDomain(
        codigo = request.codigo,
        nome = request.nome,
        descricao = request.descricao,
        fabricante = request.fabricante,
        fornecedor = request.fornecedor,
        precoDeCompra = request.precoDeCompra,
        precoDeVenda = request.precoDeVenda,
        qtdEstoque = request.qtdEstoque,
    )

    fun toDomain(codigo: String, request: AtualizarPecaRequest): Peca = toDomain(
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

    fun toCriarRequest(dto: PecaPresentationDto): PecaDto = PecaDto(
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

    private fun toDomain(
        codigo: String,
        nome: String,
        descricao: String? = null,
        fabricante: String? = null,
        fornecedor: String? = null,
        precoDeCompra: BigDecimal? = null,
        precoDeVenda: BigDecimal,
        qtdEstoque: Int = 0,
    ): Peca = Peca.criar(
        codigo = codigo,
        nome = nome,
        descricao = descricao,
        fabricante = fabricante,
        fornecedor = fornecedor,
        precoDeCompra = precoDeCompra,
        precoDeVenda = precoDeVenda,
        qtdEstoque = qtdEstoque,
    )
}
