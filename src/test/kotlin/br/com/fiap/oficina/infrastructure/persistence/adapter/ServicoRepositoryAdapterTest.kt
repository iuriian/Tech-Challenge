package br.com.fiap.oficina.infrastructure.persistence.adapter

import br.com.fiap.oficina.anyObject
import br.com.fiap.oficina.domain.entity.Servico
import br.com.fiap.oficina.domain.valueobject.Id
import br.com.fiap.oficina.infrastructure.persistence.entity.ServicoCatalogoJpaEntity
import br.com.fiap.oficina.infrastructure.persistence.mapper.ServicoCatalogoPersistenceMapper
import br.com.fiap.oficina.infrastructure.persistence.repository.ServicoJpaRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.math.BigDecimal

@ExtendWith(MockitoExtension::class)
class ServicoRepositoryAdapterTest {
    @Mock
    lateinit var jpaRepository: ServicoJpaRepository

    private val mapper = ServicoCatalogoPersistenceMapper()
    private lateinit var adapter: ServicoRepositoryAdapter

    @BeforeEach
    fun setup() {
        adapter = ServicoRepositoryAdapter(jpaRepository, mapper)
    }

    @Test
    fun `salvar deve persistir e mapear servico de catalogo`() {
        val servico =
            Servico(
                id = Id.fromString("00000000-0000-0000-0000-000000000020"),
                descricao = "Troca de óleo",
                valor = BigDecimal("150.00"),
            )

        `when`(jpaRepository.save(anyObject()))
            .thenAnswer { invocation ->
                invocation.getArgument<ServicoCatalogoJpaEntity>(0)
            }

        val resultado = adapter.salvar(servico)

        assertEquals(servico, resultado)
        verify(jpaRepository).save(anyObject())
    }
}
