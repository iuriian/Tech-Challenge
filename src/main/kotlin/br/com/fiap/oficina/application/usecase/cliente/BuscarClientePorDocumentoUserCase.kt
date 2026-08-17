package br.com.fiap.oficina.application.usecase.cliente
import br.com.fiap.oficina.application.port.out.ClienteRepository
import br.com.fiap.oficina.domain.entity.Cliente
import org.springframework.stereotype.Service

@Service
class BuscarClientePorDocumentoUseCase(
    private val clienteRepository: ClienteRepository
) {
    fun executar(documento: Documento): Cliente? = clienteRepository.buscarPorDocumento(documento)
}
