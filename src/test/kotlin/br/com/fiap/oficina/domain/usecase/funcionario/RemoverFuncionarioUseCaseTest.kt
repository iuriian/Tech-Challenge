package br.com.fiap.oficina.domain.usecase.funcionario

import br.com.fiap.oficina.domain.repository.FuncionarioRepository
import br.com.fiap.oficina.domain.valueobject.Id
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
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

        useCase.executar(id.valor.toString())

        verify(funcionarioRepository, times(1)).deletar(id)
    }
}
