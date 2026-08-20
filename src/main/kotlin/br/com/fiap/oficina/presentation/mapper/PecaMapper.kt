package br.com.fiap.oficina.presentation.mapper

import br.com.fiap.oficina.domain.entity.Peca
import br.com.fiap.oficina.presentation.dto.PecaAtualizacaoDto
import br.com.fiap.oficina.presentation.dto.PecaDto
import org.springframework.stereotype.Component

@Component
class PecaMapper {
    fun toDto(peca: Peca): PecaDto = PecaDto(
        id = peca.id.valor,
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

    fun toEntity(dto: PecaDto): Peca = Peca.criar(
        codigo = dto.codigo,
        nome = dto.nome,
        descricao = dto.descricao,
        fabricante = dto.fabricante,
        fornecedor = dto.fornecedor,
        precoDeCompra = dto.precoDeCompra,
        precoDeVenda = dto.precoDeVenda,
        qtdEstoque = dto.qtdEstoque,
    )

    fun toEntity(dto: PecaAtualizacaoDto): Peca = Peca.criar(
        codigo = CODIGO_TEMPORARIO,
        nome = dto.nome,
        descricao = dto.descricao,
        fabricante = dto.fabricante,
        fornecedor = dto.fornecedor,
        precoDeCompra = dto.precoDeCompra,
        precoDeVenda = dto.precoDeVenda,
        qtdEstoque = 0,
    )

    private companion object {
        const val CODIGO_TEMPORARIO = "TMP"
    }
}
