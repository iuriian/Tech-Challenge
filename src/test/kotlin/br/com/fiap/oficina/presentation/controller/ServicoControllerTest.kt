package br.com.fiap.oficina.presentation.controller

import br.com.fiap.oficina.application.service.ServicoService
import br.com.fiap.oficina.domain.entity.Cliente
import br.com.fiap.oficina.domain.entity.Servico
import br.com.fiap.oficina.presentation.mapper.ServicoMapper
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(ServicoController::class)
class ServicoControllerTest {

    @Autowired lateinit var mockMvc: MockMvc

    @MockitoBean lateinit var service: ServicoService

    @MockitoBean lateinit var mapper: ServicoMapper

    @Test
    @WithMockUser
    fun `deve buscar servico por id`() {
        val id = 1L
        val servico =
                Servico().apply {
                    this.id = id
                    this.descricao = "Revisao Geral"
                    this.cliente = Cliente().apply { this.id = 1L }
                    this.veiculo = br.com.fiap.oficina.domain.entity.Veiculo().apply { this.idVeiculo = 1L }
                    this.pecas = emptyList()
                    this.funcionarioId = 1L
                }

        `when`(service.listarPorId(id)).thenReturn(servico)

        mockMvc.perform(get("/servicos/$id")).andExpect(status().isOk)
    }
}
