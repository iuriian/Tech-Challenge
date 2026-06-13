package br.com.fiap.oficina.presentation.controller

import br.com.fiap.oficina.application.service.ServicoService
import br.com.fiap.oficina.domain.entity.Cliente
import br.com.fiap.oficina.domain.entity.Servico
import br.com.fiap.oficina.domain.entity.Veiculo
import br.com.fiap.oficina.domain.enum.ServicoStatus
import br.com.fiap.oficina.domain.valueobject.Documento
import br.com.fiap.oficina.domain.valueobject.Id
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
import java.util.UUID

@WebMvcTest(ServicoController::class)
class ServicoControllerTest {

    @Autowired lateinit var mockMvc: MockMvc

    @MockitoBean lateinit var service: ServicoService

    @MockitoBean lateinit var mapper: ServicoMapper

    @Test
    @WithMockUser
    fun `deve buscar servico por id`() {
        val id = UUID.randomUUID()
        val cliente = Cliente(
            id = Id.gerar(),
            nome = "Cliente Teste",
            documento = Documento.cpf("39053344705"),
            email = "cliente@teste.com"
        )
        val veiculo = Veiculo(
            id = Id.gerar(),
            marca = "Volkswagen",
            nome = "Gol",
            modelo = "Gol 1.6",
            ano = "2020",
            placa = "ABC1D23",
            motorista = cliente
        )
        val servico = Servico(
            id = Id.from(id),
            descricao = "Revisao Geral",
            status = ServicoStatus.RECEBIDA,
            funcionarioId = 1L,
            cliente = cliente,
            veiculo = veiculo,
            pecas = emptyList()
        )

        `when`(service.listarPorId(Id.from(id))).thenReturn(servico)

        mockMvc.perform(get("/servicos/$id")).andExpect(status().isOk)
    }
}
