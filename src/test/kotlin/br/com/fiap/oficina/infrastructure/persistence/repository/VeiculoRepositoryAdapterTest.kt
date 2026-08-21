package br.com.fiap.oficina.infrastructure.persistence.repository

import br.com.fiap.oficina.anyObject
import br.com.fiap.oficina.domain.entity.Cliente
import br.com.fiap.oficina.domain.entity.Veiculo
import br.com.fiap.oficina.domain.valueobject.Documento
import br.com.fiap.oficina.domain.valueobject.Id
import br.com.fiap.oficina.infrastructure.persistence.entity.VeiculoJpaEntity
import br.com.fiap.oficina.infrastructure.persistence.jpa.VeiculoJpaRepository
import br.com.fiap.oficina.infrastructure.persistence.mapper.ClientePersistenceMapper
import br.com.fiap.oficina.infrastructure.persistence.mapper.VeiculoPersistenceMapper
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
class VeiculoRepositoryAdapterTest {
    @Mock
    lateinit var jpaRepository: VeiculoJpaRepository

    private val clienteMapper = ClientePersistenceMapper()
    private val mapper = VeiculoPersistenceMapper(clienteMapper)
    private lateinit var adapter: VeiculoRepositoryAdapter
    private lateinit var cliente: Cliente
    private lateinit var veiculo: Veiculo
    private lateinit var jpa: VeiculoJpaEntity

    @BeforeEach
    fun setup() {
        adapter = VeiculoRepositoryAdapter(jpaRepository, mapper)
        cliente =
            Cliente(
                id = Id.generate(),
                nome = "Dono",
                documento = Documento.cpf("39053344705"),
                email = "dono@example.com",
            )
        veiculo =
            Veiculo(
                id = Id.generate(),
                marca = "Volkswagen",
                nome = "Gol",
                modelo = "Gol 1.6",
                ano = "2020",
                placa = "ABC1D23",
                motorista = cliente,
            )
        jpa = mapper.toJpa(veiculo)
    }

    @Test
    fun `salvar deve persistir e mapear de volta`() {
        `when`(jpaRepository.save(anyObject())).thenAnswer { it.getArgument<VeiculoJpaEntity>(0) }

        assertEquals(veiculo, adapter.salvar(veiculo))
    }

    @Test
    fun `buscarPorId deve mapear resultado`() {
        `when`(jpaRepository.findByIdVeiculo(veiculo.id.valor)).thenReturn(jpa)

        assertEquals(veiculo, adapter.buscarPorId(veiculo.id))
    }

    @Test
    fun `buscarPorPlaca deve mapear resultado`() {
        `when`(jpaRepository.findByPlaca("ABC1D23")).thenReturn(jpa)

        assertEquals(veiculo, adapter.buscarPorPlaca("ABC1D23"))
    }

    @Test
    fun `buscarPorMotorista deve mapear lista`() {
        `when`(jpaRepository.findByMotoristaId(cliente.id.valor)).thenReturn(listOf(jpa))

        assertEquals(listOf(veiculo), adapter.buscarPorMotorista(cliente.id))
    }

    @Test
    fun `existePorPlaca deve delegar ao jpa`() {
        `when`(jpaRepository.existsByPlaca("ABC1D23")).thenReturn(true)

        assertTrue(adapter.existePorPlaca("ABC1D23"))
    }
}
