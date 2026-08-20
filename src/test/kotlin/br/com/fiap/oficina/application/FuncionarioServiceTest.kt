package br.com.fiap.oficina.application

import br.com.fiap.oficina.application.service.FuncionarioService
import br.com.fiap.oficina.domain.entity.Funcionario
import br.com.fiap.oficina.domain.repository.FuncionarioRepository
import br.com.fiap.oficina.domain.valueobject.Id
import br.com.fiap.oficina.presentation.dto.FuncionarioDto
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class FuncionarioServiceTest {
    private class FakeRepository : FuncionarioRepository {
        val storage = mutableListOf<Funcionario>()
        var lastSaved: Funcionario? = null
        var lastEdited: Funcionario? = null
        var lastDeleted: Id? = null

        override fun salvar(funcionario: Funcionario): Funcionario {
            lastSaved = funcionario
            storage.add(funcionario)
            return funcionario
        }

        override fun listarTodos(): List<Funcionario> = storage.toList()

        override fun buscarPorId(id: Id): Funcionario? = storage.firstOrNull { it.id == id }

        override fun buscarPorNome(nome: String): Funcionario? = storage.firstOrNull { it.nome == nome }

        override fun editar(funcionario: Funcionario): Funcionario {
            lastEdited = funcionario
            val idx = storage.indexOfFirst { it.id == funcionario.id }
            if (idx >= 0) storage[idx] = funcionario else storage.add(funcionario)
            return funcionario
        }

        override fun deletar(id: Id) {
            lastDeleted = id
            storage.removeIf { it.id == id }
        }
    }

    private val repository = FakeRepository()
    private val service = FuncionarioService(repository)

    @Test
    fun `deve cadastrar funcionario com sucesso`() {
        val dto = FuncionarioDto(nome = "João", cargo = "ATENDENTE")

        val result = service.cadastrar(dto)

        assertNotNull(result.id)
        assertEquals(dto.nome, result.nome)
        assertEquals(dto.cargo, result.cargo)
        assertNotNull(repository.lastSaved)
        assertEquals(repository.lastSaved?.nome, dto.nome)
    }

    @Test
    fun `deve listar todos os funcionarios`() {
        repository.storage.clear()
        repository.storage.add(Funcionario.criar(nome = "A", cargo = "ATENDENTE"))
        repository.storage.add(Funcionario.criar(nome = "B", cargo = "MECANICO"))

        val list = service.listarTodos()

        assertEquals(2, list.size)
        assertTrue(list.any { it.nome == "A" })
        assertTrue(list.any { it.nome == "B" })
    }

    @Test
    fun `deve buscar por id quando existir`() {
        val f = Funcionario.criar(nome = "X", cargo = "ATENDENTE")
        repository.storage.clear()
        repository.storage.add(f)

        val found = service.buscarPorId(f.id.valor.toString())

        assertNotNull(found)
        assertEquals(f.nome, found.nome)
    }

    @Test
    fun `buscar por id deve retornar nulo quando nao existir`() {
        repository.storage.clear()

        val found = service.buscarPorId(Id.generate().valor.toString())

        assertNull(found)
    }

    @Test
    fun `buscar por id com id invalido deve lancar excecao`() {
        assertThrows(IllegalArgumentException::class.java) {
            service.buscarPorId("invalid-uuid")
        }
    }

    @Test
    fun `deve buscar por nome quando existir`() {
        val f = Funcionario.criar(nome = "Nome1", cargo = "MECANICO")
        repository.storage.clear()
        repository.storage.add(f)

        val found = service.buscarPorNome("Nome1")

        assertNotNull(found)
        assertEquals(f.nome, found.nome)
    }

    @Test
    fun `buscar por nome deve retornar nulo quando nao existir`() {
        repository.storage.clear()

        val found = service.buscarPorNome("NaoExiste")

        assertNull(found)
    }

    @Test
    fun `deve editar funcionario com sucesso`() {
        val original = Funcionario.criar(nome = "Orig", cargo = "ATENDENTE")
        repository.storage.clear()
        repository.storage.add(original)

        val dto = FuncionarioDto(id = original.id.valor.toString(), nome = "Novo", cargo = "MECANICO")

        val updated = service.editar(original.id.valor.toString(), dto)

        assertEquals(dto.nome, updated.nome)
        assertEquals(dto.cargo, updated.cargo)
        assertNotNull(repository.lastEdited)
        assertEquals(repository.lastEdited?.nome, dto.nome)
    }

    @Test
    fun `editar com cargo invalido deve lancar excecao`() {
        val original = Funcionario.criar(nome = "Orig2", cargo = "ATENDENTE")
        repository.storage.clear()
        repository.storage.add(original)

        val dto = FuncionarioDto(id = original.id.valor.toString(), nome = "Novo", cargo = "INVALIDO")

        assertThrows(IllegalArgumentException::class.java) {
            service.editar(original.id.valor.toString(), dto)
        }
    }

    @Test
    fun `deve deletar funcionario`() {
        val f = Funcionario.criar(nome = "Del", cargo = "ATENDENTE")
        repository.storage.clear()
        repository.storage.add(f)

        service.deletar(f.id.valor.toString())

        assertNotNull(repository.lastDeleted)
        assertEquals(f.id, repository.lastDeleted)
        assertTrue(repository.storage.isEmpty())
    }

    @Test
    fun `cadastrar com cargo invalido deve lancar excecao`() {
        val dto = FuncionarioDto(nome = "Bad", cargo = "UNKNOWN")
        assertThrows(IllegalArgumentException::class.java) {
            service.cadastrar(dto)
        }
    }
}
