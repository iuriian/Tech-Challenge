package br.com.fiap.oficina.presentation.controller

import br.com.fiap.oficina.application.service.ClienteService
import br.com.fiap.oficina.application.usecase.cliente.CriarClienteUseCase
import br.com.fiap.oficina.domain.entity.Cliente
import br.com.fiap.oficina.domain.valueobject.Documento
import br.com.fiap.oficina.domain.valueobject.Id
import br.com.fiap.oficina.presentation.mapper.ClienteMapper
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

@WebMvcTest(ClienteController::class)
class ClienteControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @MockitoBean
    lateinit var service: ClienteService

    @MockitoBean
    lateinit var mapper: ClienteMapper

    @MockitoBean
    lateinit var criarClienteUseCase: CriarClienteUseCase

    @Test
    @WithMockUser
    fun `deve buscar cliente por id`() {
        val id = UUID.randomUUID()
        val cliente =
            Cliente(
                id = Id.fromString(id.toString()),
                nome = "João Silva",
                documento = Documento.cpf("39053344705"),
                email = "joao.silva@example.com",
            )

        `when`(service.buscarPorId(Id.fromString(id.toString()))).thenReturn(cliente)

        mockMvc
            .perform(get("/clientes/$id"))
            .andExpect(status().isOk)
    }
}
