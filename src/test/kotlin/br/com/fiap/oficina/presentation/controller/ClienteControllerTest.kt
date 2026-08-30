package br.com.fiap.oficina.presentation.controller

import br.com.fiap.oficina.application.mapper.ClienteApplicationMapper
import br.com.fiap.oficina.application.service.ClienteService
import br.com.fiap.oficina.domain.entity.Cliente
import br.com.fiap.oficina.domain.exception.ClienteNaoEncontradoException
import br.com.fiap.oficina.domain.usecase.cliente.AtualizarClienteUseCase
import br.com.fiap.oficina.domain.usecase.cliente.BuscarClientePorDocumentoUseCase
import br.com.fiap.oficina.domain.usecase.cliente.BuscarClientePorIdUseCase
import br.com.fiap.oficina.domain.usecase.cliente.BuscarClientePorNomeUseCase
import br.com.fiap.oficina.domain.usecase.cliente.CriarClienteUseCase
import br.com.fiap.oficina.domain.usecase.cliente.ListarClientesUseCase
import br.com.fiap.oficina.domain.usecase.cliente.RemoverClienteUseCase
import br.com.fiap.oficina.domain.valueobject.Documento
import br.com.fiap.oficina.domain.valueobject.Id
import br.com.fiap.oficina.presentation.exception.ClienteExceptionHandler
import br.com.fiap.oficina.presentation.mapper.ClienteMapper
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.UUID

@WebMvcTest(ClienteController::class)
@Import(ClienteService::class, ClienteApplicationMapper::class, ClienteMapper::class, ClienteExceptionHandler::class)
class ClienteControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @MockitoBean
    lateinit var criarClienteUseCase: CriarClienteUseCase

    @MockitoBean
    lateinit var buscarClientePorIdUseCase: BuscarClientePorIdUseCase

    @MockitoBean
    lateinit var buscarClientePorNomeUseCase: BuscarClientePorNomeUseCase

    @MockitoBean
    lateinit var buscarClientePorDocumentoUseCase: BuscarClientePorDocumentoUseCase

    @MockitoBean
    lateinit var listarClientesUseCase: ListarClientesUseCase

    @MockitoBean
    lateinit var atualizarClienteUseCase: AtualizarClienteUseCase

    @MockitoBean
    lateinit var removerClienteUseCase: RemoverClienteUseCase

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

        `when`(buscarClientePorIdUseCase.executar(Id.fromString(id.toString()))).thenReturn(cliente)

        mockMvc
            .perform(get("/clientes/$id"))
            .andExpect(status().isOk)
    }

    @Test
    @WithMockUser
    fun `deve retornar 404 quando cliente nao encontrado`() {
        val id = UUID.randomUUID()

        `when`(buscarClientePorIdUseCase.executar(Id.fromString(id.toString())))
            .thenThrow(ClienteNaoEncontradoException.porId(id.toString()))

        mockMvc
            .perform(get("/clientes/$id"))
            .andExpect(status().isNotFound)
    }
}
