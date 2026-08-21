package br.com.fiap.oficina.presentation.controller

import br.com.fiap.oficina.anyObject
import br.com.fiap.oficina.application.usecase.veiculo.AtualizarVeiculoUseCase
import br.com.fiap.oficina.application.usecase.veiculo.BuscarVeiculoPorIdUseCase
import br.com.fiap.oficina.application.usecase.veiculo.BuscarVeiculoPorPlacaUseCase
import br.com.fiap.oficina.application.usecase.veiculo.BuscarVeiculosPorMotoristaUseCase
import br.com.fiap.oficina.application.usecase.veiculo.CriarVeiculoUseCase
import br.com.fiap.oficina.application.usecase.veiculo.ListarVeiculosUseCase
import br.com.fiap.oficina.application.usecase.veiculo.RemoverVeiculoUseCase
import br.com.fiap.oficina.domain.entity.Cliente
import br.com.fiap.oficina.domain.entity.Veiculo
import br.com.fiap.oficina.domain.valueobject.Documento
import br.com.fiap.oficina.domain.valueobject.Id
import br.com.fiap.oficina.presentation.mapper.VeiculoMapper
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(VeiculoController::class)
@Import(VeiculoMapper::class)
class VeiculoControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @MockitoBean
    lateinit var criarVeiculoUseCase: CriarVeiculoUseCase

    @MockitoBean
    lateinit var buscarVeiculoPorIdUseCase: BuscarVeiculoPorIdUseCase

    @MockitoBean
    lateinit var buscarVeiculoPorPlacaUseCase: BuscarVeiculoPorPlacaUseCase

    @MockitoBean
    lateinit var buscarVeiculosPorMotoristaUseCase: BuscarVeiculosPorMotoristaUseCase

    @MockitoBean
    lateinit var listarVeiculosUseCase: ListarVeiculosUseCase

    @MockitoBean
    lateinit var atualizarVeiculoUseCase: AtualizarVeiculoUseCase

    @MockitoBean
    lateinit var removerVeiculoUseCase: RemoverVeiculoUseCase

    private val motoristaId = "00000000-0000-0000-0000-000000000050"
    private val veiculoId = "00000000-0000-0000-0000-000000000010"

    private val motorista =
        Cliente(
            id = Id.fromString(motoristaId),
            nome = "Dono",
            documento = Documento.cpf("39053344705"),
            email = "dono@example.com",
        )

    private val veiculo =
        Veiculo(
            id = Id.fromString(veiculoId),
            marca = "Volkswagen",
            nome = "Gol",
            modelo = "Gol 1.6",
            ano = "2020",
            placa = "ABC1D23",
            motorista = motorista,
        )

    @Test
    @WithMockUser(roles = ["ATENDENTE"])
    fun `deve criar veiculo via endpoint POST`() {
        val requestJson =
            """
            {
              "nome": "Gol",
              "marca": "Volkswagen",
              "modelo": "Gol 1.6",
              "ano": "2020",
              "placa": "ABC1D23",
              "motoristaId": "$motoristaId"
            }
            """.trimIndent()

        `when`(criarVeiculoUseCase.executar(anyObject())).thenReturn(veiculo)

        mockMvc
            .perform(
                post("/veiculos")
                    .with(SecurityMockMvcRequestPostProcessors.csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson),
            ).andExpect(status().isCreated)
            .andExpect(jsonPath("$.placa").value("ABC1D23"))
            .andExpect(jsonPath("$.motoristaId").value(motoristaId))
    }

    @Test
    @WithMockUser(roles = ["ATENDENTE"])
    fun `deve buscar veiculo por id via GET`() {
        `when`(buscarVeiculoPorIdUseCase.executar(Id.fromString(veiculoId))).thenReturn(veiculo)

        mockMvc
            .perform(get("/veiculos/$veiculoId"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.nome").value("Gol"))
    }

    @Test
    @WithMockUser(roles = ["ATENDENTE"])
    fun `deve buscar veiculo por placa via GET`() {
        `when`(buscarVeiculoPorPlacaUseCase.executar("ABC1D23")).thenReturn(veiculo)

        mockMvc
            .perform(get("/veiculos/placa/ABC1D23"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.placa").value("ABC1D23"))
    }

    @Test
    @WithMockUser(roles = ["ATENDENTE"])
    fun `deve listar veiculos via GET`() {
        `when`(listarVeiculosUseCase.executar()).thenReturn(listOf(veiculo))

        mockMvc
            .perform(get("/veiculos"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(1))
    }

    @Test
    @WithMockUser(roles = ["ATENDENTE"])
    fun `deve buscar veiculos por motorista via GET`() {
        `when`(buscarVeiculosPorMotoristaUseCase.executar(Id.fromString(motoristaId))).thenReturn(listOf(veiculo))

        mockMvc
            .perform(get("/veiculos/motorista/$motoristaId"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(1))
    }

    @Test
    @WithMockUser(roles = ["ATENDENTE"])
    fun `deve atualizar veiculo via PUT`() {
        val requestJson =
            """
            {
              "nome": "Gol Atualizado",
              "marca": "Volkswagen",
              "modelo": "Gol 1.6",
              "ano": "2021",
              "placa": "ABC1D23",
              "motoristaId": "$motoristaId"
            }
            """.trimIndent()
        val atualizado = veiculo.copy(nome = "Gol Atualizado", ano = "2021")

        `when`(atualizarVeiculoUseCase.executar(anyObject())).thenReturn(atualizado)

        mockMvc
            .perform(
                put("/veiculos/$veiculoId")
                    .with(SecurityMockMvcRequestPostProcessors.csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson),
            ).andExpect(status().isOk)
            .andExpect(jsonPath("$.nome").value("Gol Atualizado"))
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `deve remover veiculo via DELETE`() {
        mockMvc
            .perform(
                delete("/veiculos/$veiculoId")
                    .with(SecurityMockMvcRequestPostProcessors.csrf()),
            ).andExpect(status().isNoContent)
    }
}
