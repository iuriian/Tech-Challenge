package br.com.fiap.oficina.infrastructure.persistence.repository

import br.com.fiap.oficina.anyObject
import br.com.fiap.oficina.domain.entity.Cliente
import br.com.fiap.oficina.domain.valueobject.Documento
import br.com.fiap.oficina.domain.valueobject.Id
import br.com.fiap.oficina.infrastructure.persistence.entity.ClienteJpaEntity
import br.com.fiap.oficina.infrastructure.persistence.mapper.ClientePersistenceMapper
import br.com.fiap.oficina.infrastructure.persistence.jpa.ClienteJpaRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.util.Optional
import kotlin.test.assertEquals
import kotlin.test.assertNull

@ExtendWith(MockitoExtension::class)
class ClienteRepositoryImplTest {
    @Mock
    lateinit var jpaRepository: ClienteJpaRepository

    private val mapper = ClientePersistenceMapper()
    private lateinit var adapter: ClienteRepositoryImpl
    private lateinit var cliente: Cliente
    private lateinit var jpa: ClienteJpaEntity

    @BeforeEach
    fun setup() {
        adapter = ClienteRepositoryImpl(jpaRepository, mapper)
        cliente =
            Cliente(
                id = Id.generate(),
                nome = "João",
                documento = Documento.cpf("39053344705"),
                email = "joao@example.com",
            )
        jpa = mapper.toJpa(cliente)
    }

    @Test
    fun `salvar deve persistir e mapear de volta`() {
        `when`(jpaRepository.save(anyObject())).thenAnswer { it.getArgument<ClienteJpaEntity>(0) }

        assertEquals(cliente, adapter.salvar(cliente))
    }

    @Test
    fun `buscarPorId deve mapear quando presente`() {
        `when`(jpaRepository.findById(cliente.id.valor)).thenReturn(Optional.of(jpa))

        assertEquals(cliente, adapter.buscarPorId(cliente.id))
    }

    @Test
    fun `buscarPorId deve retornar null quando ausente`() {
        val id = Id.generate()
        `when`(jpaRepository.findById(id.valor)).thenReturn(Optional.empty())

        assertNull(adapter.buscarPorId(id))
    }

    @Test
    fun `buscarPorDocumento deve mapear resultado`() {
        `when`(jpaRepository.findByDocumentoNumero("39053344705")).thenReturn(jpa)

        assertEquals(cliente, adapter.buscarPorDocumento("39053344705"))
    }

    @Test
    fun `buscarPorNome deve mapear resultado`() {
        `when`(jpaRepository.findByNome("João")).thenReturn(jpa)

        assertEquals(cliente, adapter.buscarPorNome("João"))
    }

    @Test
    fun `remover deve delegar deleteById`() {
        adapter.remover(cliente.id)

        verify(jpaRepository).deleteById(cliente.id.valor)
    }
}
