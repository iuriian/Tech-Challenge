package br.com.fiap.oficina.infrastructure.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class OpenApiConfig {
    @Value("\${KEYCLOAK_URL:http://localhost:8081}")
    private val keycloakUrl: String? = null

    @Value("\${KEYCLOAK_REALM:fiap}")
    private val keycloakRealm: String? = null

    @Value("\${KEYCLOAK_CLIENT_ID:techchallenge}")
    private val keycloakClientId: String? = null

    @Bean
    fun openAPI(): OpenAPI? {
        val tokenUrl = "$keycloakUrl/realms/$keycloakRealm/protocol/openid-connect/token"

        return OpenAPI()
            .info(
                Info()
                    .title("TechChallenge API")
                    .description("API de gerenciamento de oficina mecânica. Use o botão **Authorize** e informe usuário e senha para autenticar.")
                    .version("1.0.0")
            )
            .addSecurityItem(SecurityRequirement()
                .addList(OAUTH2_SCHEME, mutableListOf<String?>("openid", "profile")))
            .components(
                Components()
                    .addSecuritySchemes(
                        OAUTH2_SCHEME, SecurityScheme()
                            .name(OAUTH2_SCHEME)
                            .type(SecurityScheme.Type.OAUTH2)
                            .description("Informe usuário e senha. Client: **$keycloakClientId**(público, sem secret).")
                            .flows(
                                OAuthFlows()
                                    .password(
                                        OAuthFlow()
                                            .tokenUrl(tokenUrl)
                                            .scopes(
                                                Scopes()
                                                    .addString("openid", "OpenID Connect")
                                                    .addString("profile", "Perfil do usuário")
                                            )
                                    )
                            )
                    )
            )
    }

    companion object {
        private const val OAUTH2_SCHEME = "oauth2"
    }
}