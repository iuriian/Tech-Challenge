package br.com.fiap.oficina.infrastructure.config

import org.springframework.core.convert.converter.Converter
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import java.util.stream.Stream

class KeycloakJwtRoleConverter : Converter<Jwt?, MutableCollection<GrantedAuthority?>?> {
    override fun convert(jwt: Jwt): MutableCollection<GrantedAuthority?> {
        return Stream.concat<String?>(
            extractRealmRoles(jwt).stream(),
            extractClientRoles(jwt).stream()
        )
            .distinct()
            .map { role: String? -> SimpleGrantedAuthority("ROLE_" + role) }
            .map { obj: SimpleGrantedAuthority? -> GrantedAuthority::class.java.cast(obj) }
            .toList()
    }


    private fun extractRealmRoles(jwt: Jwt): MutableList<String?> {
        val realmAccess = jwt.getClaimAsMap("realm_access")
        if (realmAccess == null) return mutableListOf<String?>()
        val roles = realmAccess.get("roles")
        if (roles is MutableList<*>) {
            return roles.stream().filter { obj: Any? -> String::class.java.isInstance(obj) }
                .map { obj: Any? -> String::class.java.cast(obj) }.toList()
        }
        return mutableListOf<String?>()
    }


    private fun extractClientRoles(jwt: Jwt): MutableList<String?> {
        val clientId = jwt.getClaimAsString("azp") ?: return mutableListOf<String?>()

        val resourceAccess = jwt.getClaimAsMap("resource_access")
        if (resourceAccess == null) return mutableListOf<String?>()

        val clientAccess = resourceAccess.get(clientId)
        if (clientAccess !is MutableMap<*, *>) return mutableListOf<String?>()

        val roles = clientAccess.get("roles")
        if (roles is MutableList<*>) {
            return roles.stream().filter { obj: Any? -> String::class.java.isInstance(obj) }
                .map{ obj: Any? -> String::class.java.cast(obj) }.toList()
        }
        return mutableListOf()
    }
}