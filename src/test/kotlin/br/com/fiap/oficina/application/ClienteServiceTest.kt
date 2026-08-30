package br.com.fiap.oficina.application

import br.com.fiap.oficina.anyObject
import br.com.fiap.oficina.application.dto.AtualizarClienteRequest
import br.com.fiap.oficina.application.dto.CriarClienteRequest
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
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class ClienteServiceTest {
    @Mock
    lateinit var criarClienteUseCase: CriarClienteUseCase

    @Mock
    lateinit var listarClientesUseCase: ListarClientesUseCase

    @Mock
    lateinit var buscarClientePorIdUseCase: BuscarClientePorIdUseCase

    @Mock
    lateinit var buscarClientePorNomeUseCase: BuscarClientePorNomeUseCase

    @Mock
    lateinit var buscarClientePorDocumentoUseCase: BuscarClientePorDocumentoUseCase

    @Mock
    lateinit var atualizarClienteUseCase: AtualizarClienteUseCase

    @Mock
    lateinit var removerClienteUseCase: RemoverClienteUseCase

    private val mapper = ClienteApplicationMapper()
    private lateinit var service: ClienteService
    private lateinit var cliente: Cliente

    @BeforeEach
    fun setup() {
        service =
            ClienteService(
                criarClienteUseCase,
                listarClientesUseCase,
                buscarClientePorIdUseCase,
                buscarClientePorNomeUseCase,
                buscarClientePorDocumentoUseCase,
                atualizarClienteUseCase,
                removerClienteUseCase,
                mapper,
            )
        cliente =
            Cliente(
                id = Id.generate(),
                nome = "João Silva",
                documento = Documento.cpf("39053344705"),
                email = "joao@example.com",
            )
    }

    @Test
    fun `deve criar cliente`() {
        val request =
            CriarClienteRequest(
                nome = "João Silva",
                numeroDocumento = "39053344705",
                tipoPessoa = "PESSOA_FISICA",
                email = "joao@example.com",
            )
        `when`(criarClienteUseCase.executar(anyObject())).thenReturn(cliente)

        val response = service.criar(request)

        assertEquals(cliente.nome, response.nome)
        assertEquals(cliente.id.valor.toString(), response.id)
    }

    @Test
    fun `deve buscar cliente por id`() {
        `when`(buscarClientePorIdUseCase.executar(anyObject())).thenReturn(cliente)

        val response = service.buscarPorId(cliente.id.valor.toString())

        assertEquals(cliente.nome, response.nome)
    }

    @Test
    fun `deve alterar cliente`() {
        val request =
            AtualizarClienteRequest(
                nome = "João Silva",
                numeroDocumento = "39053344705",
                tipoPessoa = "PESSOA_FISICA",
                email = "joao@example.com",
            )
        `when`(atualizarClienteUseCase.executar(anyObject())).thenReturn(cliente)

        val response = service.alterar(cliente.id.valor.toString(), request)

        assertEquals(cliente.nome, response.nome)
    }

    @Test
    fun `deve remover cliente`() {
        service.remover(cliente.id.valor.toString())

        verify(removerClienteUseCase).executar(Id.fromString(cliente.id.valor.toString()))
    }

    @Test
    fun `deve propagar ClienteNaoEncontradoException`() {
        `when`(buscarClientePorIdUseCase.executar(anyObject()))
            .thenThrow(ClienteNaoEncontradoException.porId(cliente.id.valor.toString()))

        org.junit.jupiter.api.assertThrows<ClienteNaoEncontradoException> {
            service.buscarPorId(cliente.id.valor.toString())
        }
    }
}
