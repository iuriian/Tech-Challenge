package br.com.fiap.oficina.infrastructure.persistence.repository

import br.com.fiap.oficina.anyObject
import br.com.fiap.oficina.domain.entity.Funcionario
import br.com.fiap.oficina.domain.enum.Cargo
import br.com.fiap.oficina.domain.valueobject.Id
import br.com.fiap.oficina.infrastructure.persistence.entity.FuncionarioEntity
import br.com.fiap.oficina.infrastructure.persistence.jpa.FuncionarioJpaRepository
import br.com.fiap.oficina.infrastructure.persistence.mapper.toEntity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.util.Optional

@ExtendWith(MockitoExtension::class)
class FuncionarioRepositoryImplTest {
    @Mock
    lateinit var jpaRepository: FuncionarioJpaRepository

    private lateinit var adapter: FuncionarioRepositoryImpl
    private lateinit var funcionario: Funcionario
    private lateinit var entity: FuncionarioEntity

    @BeforeEach
    fun setup() {
        adapter = FuncionarioRepositoryImpl(jpaRepository)
        funcionario =
            Funcionario(
                id = Id.generate(),
                nome = "João",
                cargo = Cargo.ATENDENTE,
            )
        entity = funcionario.toEntity()
    }

    @Test
    fun `salvar deve persistir e mapear de volta`() {
        `when`(jpaRepository.save(anyObject())).thenAnswer { it.getArgument<FuncionarioEntity>(0) }

        assertEquals(funcionario, adapter.salvar(funcionario))
    }

    @Test
    fun `listarTodos deve mapear resultados`() {
        `when`(jpaRepository.findAll()).thenReturn(listOf(entity))

        assertEquals(listOf(funcionario), adapter.listarTodos())
    }

    @Test
    fun `buscarPorId deve mapear resultado`() {
        `when`(jpaRepository.findById(funcionario.id.valor)).thenReturn(Optional.of(entity))

        assertEquals(funcionario, adapter.buscarPorId(funcionario.id))
    }

    @Test
    fun `buscarPorId deve retornar null quando nao encontrado`() {
        `when`(jpaRepository.findById(funcionario.id.valor)).thenReturn(Optional.empty())

        assertNull(adapter.buscarPorId(funcionario.id))
    }

    @Test
    fun `buscarPorNome deve mapear resultado`() {
        `when`(jpaRepository.findByNome("João")).thenReturn(entity)

        assertEquals(funcionario, adapter.buscarPorNome("João"))
    }

    @Test
    fun `editar deve atualizar entidade existente`() {
        val atualizado = funcionario.copy(nome = "Maria", cargo = Cargo.MECANICO)
        `when`(jpaRepository.findById(funcionario.id.valor)).thenReturn(Optional.of(entity))
        `when`(jpaRepository.save(entity)).thenReturn(entity)

        val resultado = adapter.editar(atualizado)

        assertEquals("Maria", entity.nome)
        assertEquals(Cargo.MECANICO.id, entity.cargo)
        assertEquals(atualizado.nome, resultado.nome)
        verify(jpaRepository, times(1)).save(entity)
    }

    @Test
    fun `deletar deve delegar ao jpa`() {
        adapter.deletar(funcionario.id)

        verify(jpaRepository, times(1)).deleteById(funcionario.id.valor)
    }
}
