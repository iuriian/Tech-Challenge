package br.com.fiap.oficina.domain.usecase.veiculo

import br.com.fiap.oficina.anyObject
import br.com.fiap.oficina.domain.entity.Cliente
import br.com.fiap.oficina.domain.entity.Veiculo
import br.com.fiap.oficina.domain.exception.VeiculoNaoEncontradoException
import br.com.fiap.oficina.domain.repository.VeiculoRepository
import br.com.fiap.oficina.domain.valueobject.Documento
import br.com.fiap.oficina.domain.valueobject.Id
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class RemoverVeiculoUseCaseTest {
    @Mock
    lateinit var veiculoRepository: VeiculoRepository

    @InjectMocks
    lateinit var useCase: RemoverVeiculoUseCase

    private lateinit var veiculo: Veiculo

    @BeforeEach
    fun setUp() {
        val motorista =
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
                motorista = motorista,
            )
    }

    @Test
    fun `deve remover veiculo existente`() {
        `when`(veiculoRepository.buscarPorId(veiculo.id)).thenReturn(veiculo)

        useCase.executar(veiculo.id)

        verify(veiculoRepository).remover(veiculo.id)
    }

    @Test
    fun `deve lancar excecao ao remover veiculo inexistente`() {
        val idInexistente = Id.generate()
        `when`(veiculoRepository.buscarPorId(idInexistente)).thenReturn(null)

        val exception =
            assertThrows(VeiculoNaoEncontradoException::class.java) {
                useCase.executar(idInexistente)
            }

        assertTrue(exception.message!!.contains("não encontrado"))
        verify(veiculoRepository, never()).remover(anyObject())
    }
}
