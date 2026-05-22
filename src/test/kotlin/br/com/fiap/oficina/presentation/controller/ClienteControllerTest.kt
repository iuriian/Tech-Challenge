package br.com.fiap.oficina.presentation.controller

import br.com.fiap.oficina.application.ClienteService
import br.com.fiap.oficina.domain.entity.Cliente
import br.com.fiap.oficina.presentation.mapper.ClienteMapper
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
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

    @Test
    fun `deve buscar cliente por id`() {
        val id = UUID.randomUUID()
        val cliente = Cliente().apply {
            this.id = id
            this.nome = "João Silva"
            this.cpf = "123.456.789-00"
        }
        
        `when`(service.buscarPorId(id)).thenReturn(cliente)
        
        mockMvc.perform(get("/clientes/$id"))
            .andExpect(status().isOk)
    }
}
