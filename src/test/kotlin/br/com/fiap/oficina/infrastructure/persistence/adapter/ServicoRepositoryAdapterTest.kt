package br.com.fiap.oficina.infrastructure.persistence.adapter

import br.com.fiap.oficina.anyObject
import br.com.fiap.oficina.domain.entity.Cliente
import br.com.fiap.oficina.domain.entity.Servico
import br.com.fiap.oficina.domain.entity.Veiculo
import br.com.fiap.oficina.domain.enum.ServicoStatus
import br.com.fiap.oficina.domain.valueobject.Documento
import br.com.fiap.oficina.domain.valueobject.Id
import br.com.fiap.oficina.infrastructure.persistence.entity.ServicoJpaEntity
import br.com.fiap.oficina.infrastructure.persistence.mapper.ClientePersistenceMapper
import br.com.fiap.oficina.infrastructure.persistence.mapper.PecaPersistenceMapper
import br.com.fiap.oficina.infrastructure.persistence.mapper.ServicoPersistenceMapper
import br.com.fiap.oficina.infrastructure.persistence.mapper.VeiculoPersistenceMapper
import br.com.fiap.oficina.infrastructure.persistence.repository.ServicoJpaRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import java.util.Optional

@ExtendWith(MockitoExtension::class)
class ServicoRepositoryAdapterTest {
    @Mock
    lateinit var jpaRepository: ServicoJpaRepository

    private val clienteMapper = ClientePersistenceMapper()
    private val mapper =
        ServicoPersistenceMapper(
            clienteMapper,
            VeiculoPersistenceMapper(clienteMapper),
            PecaPersistenceMapper(),
        )
    private lateinit var adapter: ServicoRepositoryAdapter
    private lateinit var servico: Servico
    private lateinit var jpa: ServicoJpaEntity

    @BeforeEach
    fun setup() {
        adapter = ServicoRepositoryAdapter(jpaRepository, mapper)
        val cliente =
            Cliente(
                id = Id.generate(),
                nome = "Cliente",
                documento = Documento.cpf("39053344705"),
                email = "cliente@example.com",
            )
        val veiculo =
            Veiculo(
                id = Id.generate(),
                marca = "Volkswagen",
                nome = "Gol",
                modelo = "Gol 1.6",
                ano = "2020",
                placa = "ABC1D23",
                motorista = cliente,
            )
        val funcionario =
            br.com.fiap.oficina.domain.entity.Funcionario(
                id = Id.generate(),
                nome = "Funcionario Teste",
                cargo = br.com.fiap.oficina.domain.enum.Cargo.MECANICO,
            )
        servico =
            Servico(
                id = Id.generate(),
                descricao = "Troca de óleo",
                status = ServicoStatus.RECEBIDA,
                funcionario = funcionario,
                cliente = cliente,
                veiculo = veiculo,
            )
        jpa = mapper.toJpa(servico)
    }

    @Test
    fun `salvar deve persistir e mapear de volta`() {
        `when`(jpaRepository.save(anyObject())).thenAnswer { it.getArgument<ServicoJpaEntity>(0) }

        assertEquals(servico, adapter.salvar(servico))
    }

    @Test
    fun `buscarPorId deve mapear quando presente`() {
        `when`(jpaRepository.findById(servico.id.valor)).thenReturn(Optional.of(jpa))

        assertEquals(servico, adapter.buscarPorId(servico.id))
    }

    @Test
    fun `listarTodos deve mapear lista`() {
        `when`(jpaRepository.findAll()).thenReturn(listOf(jpa))

        assertEquals(listOf(servico), adapter.listarTodos())
    }

    @Test
    fun `existePorId deve delegar ao jpa`() {
        `when`(jpaRepository.existsById(servico.id.valor)).thenReturn(true)

        assertTrue(adapter.existePorId(servico.id))
    }

    @Test
    fun `deletarPorId deve delegar deleteById`() {
        adapter.deletarPorId(servico.id)

        verify(jpaRepository).deleteById(servico.id.valor)
    }
}
