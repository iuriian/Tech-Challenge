package br.com.fiap.oficina.presentation.controller;

import br.com.fiap.oficina.application.ServicoService;
import br.com.fiap.oficina.presentation.mapper.ServicoMapper;

@RestController
@RequestMapping("/servicos")
public class ServicoController(
    private val service:ServicoService,
    private val mapper:ServicoMapper,
) {

    fun criar(){}

    fun atualizar(){}

    fun listarPorId(){}

    fun listarTodos(){}

    fun deletarPorId(){}

}
