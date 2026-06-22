package br.com.fiap.oficina.application.service

import br.com.fiap.oficina.domain.entity.Cliente
import br.com.fiap.oficina.domain.entity.Veiculo
import br.com.fiap.oficina.domain.repository.ClienteRepository
import br.com.fiap.oficina.domain.repository.VeiculoRepository
import br.com.fiap.oficina.domain.valueobject.Documento
import org.springframework.stereotype.Service
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class VeiculoServiceTest {

    @Mock
    lateinit var repositoryVeiculo: VeiculoRepository

    @Mock
    lateinit var repositoryCliente: ClienteRepository

    @InjectMocks
    lateinit var serviceVeiculo: VeiculoService

    @InjectMocks
    lateinit var serviceCliente: ClienteService

    private lateinit var veiculo: Veiculo
    private lateinit var cliente: Cliente

    @BeforeEach
    fun testSetup(){
        cliente = Cliente().apply {
            id = 1L
            nome = "Senninha"
            documento = Documento.cpf("12345678909")
            email = "senna@email.com"
        }

        //Placeholder
        veiculo = Veiculo().apply {
            idVeiculo = 1L
            marca = "Nissan"
            modelo = "March"
            ano = "2014"
            placa = "abc1234"
            motorista = cliente
        }
    }

    @Test
    fun `deve salvar veiculo com sucesso`() {
        `when`(repositoryVeiculo.salvar(veiculo)).thenReturn(veiculo)

        val resultado = serviceVeiculo.salvarVeiculo(veiculo)

        assertNotNull(resultado)
        assertEquals(veiculo.idVeiculo, resultado.idVeiculo)
        assertEquals(veiculo.marca, resultado.marca)
        assertEquals(veiculo.placa, resultado.placa)
        assertEquals(veiculo.motorista, resultado.motorista)
        verify(repositoryVeiculo, times(1)).salvar(veiculo)
    }

    @Test
    fun `deve buscar por idveiculo com sucesso`() {
        val id = 1L
        `when`(repositoryVeiculo.buscarPorId(id)).thenReturn(veiculo)

        val resultado = serviceVeiculo.buscarPorId(id)

        assertNotNull(resultado)
        assertEquals(veiculo.idVeiculo, resultado?.idVeiculo)
        assertEquals(veiculo.placa, resultado?.placa)
        assertEquals(veiculo.motorista, resultado?.motorista)
        verify(repositoryVeiculo, times(1)).buscarPorId(id)
    }

    @Test
    fun `deve buscar por idveiculo e retornar nao encontrado`() {
        val id = 8001L
        `when`(repositoryVeiculo.buscarPorId(id)).thenReturn(null)

        val resultado = serviceVeiculo.buscarPorId(id)

        assertNull(resultado)
        verify(repositoryVeiculo, times(1)).buscarPorId(id)
    }

    @Test
    fun `deve buscar por placa com sucesso`() {
        val placa = "abc1234"
        `when`(repositoryVeiculo.buscarPorPlaca(placa)).thenReturn(veiculo)

        val resultado = serviceVeiculo.buscarPorPlaca(placa)

        assertNotNull(resultado)
        assertEquals(veiculo.idVeiculo, resultado?.idVeiculo)
        assertEquals(veiculo.placa, resultado?.placa)
        assertEquals(veiculo.motorista, resultado?.motorista)
        verify(repositoryVeiculo, times(1)).buscarPorPlaca(placa)
    }

    @Test
    fun `deve buscar por motorista com sucesso`() {
        val motorista = cliente
        `when`(repositoryVeiculo.buscarPorMotorista(motorista)).thenReturn(listOf(veiculo))

        val resultado = serviceVeiculo.buscarPorMotorista(motorista)

        assertNotNull(resultado)
        assertEquals(veiculo.idVeiculo, resultado[0].idVeiculo)
        assertEquals(veiculo.placa, resultado[0].placa)
        assertEquals(veiculo.motorista, resultado[0].motorista)
        verify(repositoryVeiculo, times(1)).buscarPorMotorista(motorista)
    }

}