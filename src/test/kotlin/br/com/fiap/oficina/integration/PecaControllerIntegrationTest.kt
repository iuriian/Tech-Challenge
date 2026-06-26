package br.com.fiap.oficina.integration

import br.com.fiap.oficina.presentation.dto.PecaAtualizacaoDto
import br.com.fiap.oficina.presentation.dto.PecaDto
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.patch
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import java.math.BigDecimal

/**
 * Testes de integração do fluxo de peças/estoque. Peças de seed usadas:
 * PEC001 (Filtro de Óleo, venda 45.00, estoque 50) e PEC003 (Pastilha, estoque 20).
 */
class PecaControllerIntegrationTest : AbstractIntegrationTest() {

    private fun novaPecaJson(codigo: String = "PEC999", nome: String = "Peça de Teste") =
        objectMapper.writeValueAsString(
            PecaDto(
                codigo = codigo,
                nome = nome,
                descricao = "Peça criada em teste de integração",
                fabricante = "ACME",
                fornecedor = "Fornecedor Teste",
                precoDeCompra = BigDecimal("10.00"),
                precoDeVenda = BigDecimal("25.00"),
                qtdEstoque = 30
            )
        )

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `deve criar peca e busca-la por codigo`() {
        mockMvc.post("/pecas") {
            contentType = MediaType.APPLICATION_JSON
            content = novaPecaJson()
        }.andExpect {
            status { isCreated() }
            jsonPath("$.id") { exists() }
            jsonPath("$.codigo") { value("PEC999") }
            jsonPath("$.ativo") { value(true) }
        }

        mockMvc.get("/pecas/codigo/{codigo}", "PEC999")
            .andExpect {
                status { isOk() }
                jsonPath("$.nome") { value("Peça de Teste") }
            }
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `deve retornar 409 ao criar peca com codigo ja existente`() {
        mockMvc.post("/pecas") {
            contentType = MediaType.APPLICATION_JSON
            content = novaPecaJson(codigo = "PEC001") // já existe no seed
        }.andExpect { status { isConflict() } }
    }

    @Test
    @WithMockUser(roles = ["ATENDENTE"])
    fun `deve retornar 403 quando atendente tenta criar peca`() {
        mockMvc.post("/pecas") {
            contentType = MediaType.APPLICATION_JSON
            content = novaPecaJson()
        }.andExpect { status { isForbidden() } }
    }

    @Test
    @WithMockUser(roles = ["MECANICO"])
    fun `deve listar pecas ativas do seed`() {
        mockMvc.get("/pecas")
            .andExpect {
                status { isOk() }
                jsonPath("$") { isArray() }
                jsonPath("$[?(@.codigo == 'PEC001')]") { isNotEmpty() }
            }
    }

    @Test
    @WithMockUser(roles = ["MECANICO"])
    fun `deve retirar pecas do estoque`() {
        mockMvc.patch("/pecas/{codigo}/estoque/retirar", "PEC001") {
            param("qtd", "5")
        }.andExpect {
            status { isOk() }
            jsonPath("$.qtdEstoque") { value(45) } // 50 - 5
        }
    }

    @Test
    @WithMockUser(roles = ["MECANICO"])
    fun `deve retornar 400 ao retirar mais pecas do que existe em estoque`() {
        mockMvc.patch("/pecas/{codigo}/estoque/retirar", "PEC003") {
            param("qtd", "9999")
        }.andExpect { status { isBadRequest() } }
    }

    @Test
    @WithMockUser(roles = ["ATENDENTE"])
    fun `deve repor pecas no estoque`() {
        mockMvc.patch("/pecas/{codigo}/estoque/repor", "PEC001") {
            param("qtd", "10")
        }.andExpect {
            status { isOk() }
            jsonPath("$.qtdEstoque") { value(60) } // 50 + 10
        }
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `deve atualizar dados de uma peca`() {
        val atualizacao = objectMapper.writeValueAsString(
            PecaAtualizacaoDto(
                nome = "Filtro de Óleo Premium",
                descricao = "Atualizado em teste",
                fabricante = "Bosch",
                fornecedor = "AutoParts Ltda",
                precoDeCompra = BigDecimal("28.00"),
                precoDeVenda = BigDecimal("50.00")
            )
        )

        mockMvc.put("/pecas/{codigo}", "PEC001") {
            contentType = MediaType.APPLICATION_JSON
            content = atualizacao
        }.andExpect {
            status { isOk() }
            jsonPath("$.nome") { value("Filtro de Óleo Premium") }
            jsonPath("$.precoDeVenda") { value(50.00) }
        }
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `deve desativar peca e remove-la da listagem de ativos`() {
        mockMvc.delete("/pecas/{codigo}", "PEC005")
            .andExpect { status { isNoContent() } }

        mockMvc.get("/pecas")
            .andExpect {
                status { isOk() }
                jsonPath("$[?(@.codigo == 'PEC005')]") { isEmpty() }
            }
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `deve reativar peca previamente desativada`() {
        mockMvc.delete("/pecas/{codigo}", "PEC006")
            .andExpect { status { isNoContent() } }

        mockMvc.patch("/pecas/{codigo}/reativar", "PEC006")
            .andExpect {
                status { isOk() }
                content { string("true") }
            }

        mockMvc.get("/pecas")
            .andExpect {
                jsonPath("$[?(@.codigo == 'PEC006')]") { isNotEmpty() }
            }
    }
}
