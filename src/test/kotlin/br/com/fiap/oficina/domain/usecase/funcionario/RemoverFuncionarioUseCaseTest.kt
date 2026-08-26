package br.com.fiap.oficina.domain.usecase.funcionario

import br.com.fiap.oficina.domain.exception.FuncionarioNaoEncontradoException
import br.com.fiap.oficina.domain.repository.FuncionarioRepository
import br.com.fiap.oficina.domain.valueobject.Id
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.never
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class RemoverFuncionarioUseCaseTest {
    @Mock
    lateinit var funcionarioRepository: FuncionarioRepository

    @InjectMocks
    lateinit var useCase: RemoverFuncionarioUseCase

    @Test
    fun `deve remover funcionario com sucesso`() {
        val id = Id.generate()
        `when`(funcionarioRepository.buscarPorId(id)).thenReturn(
            br.com.fiap.oficina.domain.entity.Funcionario.criar(nome = "João", cargo = "ATENDENTE").copy(id = id),
        )

        useCase.executar(id)

        verify(funcionarioRepository, times(1)).buscarPorId(id)
        verify(funcionarioRepository, times(1)).deletar(id)
    }

    @Test
    fun `deve lancar excecao quando funcionario nao encontrado`() {
        val id = Id.generate()
        `when`(funcionarioRepository.buscarPorId(id)).thenReturn(null)

        assertThrows<FuncionarioNaoEncontradoException> {
            useCase.executar(id)
        }

        verify(funcionarioRepository, times(1)).buscarPorId(id)
        verify(funcionarioRepository, never()).deletar(id)
    }
}
