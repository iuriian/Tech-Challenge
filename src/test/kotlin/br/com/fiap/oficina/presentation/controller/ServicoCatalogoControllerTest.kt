package br.com.fiap.oficina.presentation.controller

import br.com.fiap.oficina.anyObject
import br.com.fiap.oficina.application.port.`in`.CriarServicoCommand
import br.com.fiap.oficina.application.port.`in`.CriarServicoUseCase
import br.com.fiap.oficina.domain.entity.Servico
import br.com.fiap.oficina.domain.valueobject.Id
import org.junit.jupiter.api.Test
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.math.BigDecimal

@WebMvcTest(ServicoCatalogoController::class)
class ServicoCatalogoControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @MockitoBean
    lateinit var criarServicoUseCase: CriarServicoUseCase

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `deve criar servico de catalogo`() {
        val servico =
            Servico(
                id = Id.fromString("00000000-0000-0000-0000-000000000020"),
                descricao = "Troca de óleo",
                valor = BigDecimal("150.00"),
            )

        `when`(criarServicoUseCase.executar(anyObject()))
            .thenReturn(servico)

        mockMvc
            .perform(
                post("/catalogo/servicos")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        """
                        {
                          "descricao": "Troca de óleo",
                          "valor": 150.00
                        }
                        """.trimIndent(),
                    ),
            ).andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(servico.id.valor.toString()))
            .andExpect(jsonPath("$.descricao").value("Troca de óleo"))
            .andExpect(jsonPath("$.valor").value(150.0))

        verify(criarServicoUseCase).executar(
            CriarServicoCommand(
                descricao = "Troca de óleo",
                valor = BigDecimal("150.00"),
            ),
        )
    }
}
