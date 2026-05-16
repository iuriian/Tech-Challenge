package br.com.fiap.oficina.presentation.controller

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import br.com.fiap.oficina.infrastructure.OfficinaApplication

@SpringBootTest(classes = [OfficinaApplication::class])
@AutoConfigureMockMvc
class ClienteControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun `deve retornar lista de clientes`() {
        mockMvc.perform(get("/clientes"))
            .andExpect(status().isOk)
            .andExpect(content().string("Lista de clientes"))
    }
}
