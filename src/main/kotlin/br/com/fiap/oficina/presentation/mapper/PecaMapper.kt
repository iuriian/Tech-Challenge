package br.com.fiap.oficina.presentation.mapper

import br.com.fiap.oficina.application.dto.AtualizarPecaRequest
import br.com.fiap.oficina.application.dto.CriarPecaRequest
import br.com.fiap.oficina.application.dto.PecaResponse
import br.com.fiap.oficina.presentation.dto.PecaAtualizacaoDto
import br.com.fiap.oficina.presentation.dto.PecaDto
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class PecaMapper {
    fun toCriarRequest(dto: PecaDto): CriarPecaRequest = CriarPecaRequest(
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

    fun toDto(response: PecaResponse): PecaDto = PecaDto(
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
