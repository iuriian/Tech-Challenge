package br.com.fiap.oficina.servico.infrastructure.persistence.repositories

import jakarta.persistence.EntityManager
import jakarta.persistence.Query
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class SequenciaOrdemServicoRepositoryImplTest {
    @Mock
    lateinit var entityManager: EntityManager

    @Mock
    lateinit var query: Query

    private lateinit var repository: SequenciaOrdemServicoRepositoryImpl

    @BeforeEach
    fun setup() {
        repository =
            SequenciaOrdemServicoRepositoryImpl(
                entityManager,
            )
    }

    @Test
    fun `deve obter proximo valor da sequencia de ordem de servico`() {
        `when`(
            entityManager.createNativeQuery(
                "SELECT nextval('ordem_servico_numero_seq')",
            ),
        ).thenReturn(query)

        `when`(query.singleResult).thenReturn(123L)

        val resultado = repository.obterProximoValor()

        assertEquals(123L, resultado)

        verify(entityManager).createNativeQuery(
            "SELECT nextval('ordem_servico_numero_seq')",
        )
        verify(query).singleResult
    }
}
