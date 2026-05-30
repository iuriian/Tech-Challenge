package br.com.fiap.oficina.infrastructure.config

import org.springframework.core.convert.converter.Converter
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import java.util.stream.Stream

class KeycloakJwtRoleConverter : Converter<Jwt, MutableCollection<GrantedAuthority>> {
    override fun convert(jwt: Jwt): MutableCollection<GrantedAuthority> {
        return Stream.concat(
            extractRealmRoles(jwt).stream(),
            extractClientRoles(jwt).stream()
        )
            .distinct()
            .map {  SimpleGrantedAuthority("ROLE_$it") }
            .map { GrantedAuthority::class.java.cast(it) }
            .toList()
    }

    private fun extractRealmRoles(jwt: Jwt): MutableList<String> {
        val realmAccess: Map<String?, Any> = jwt.getClaimAsMap("realm_access")?:return  mutableListOf()
        val roles = realmAccess["roles"]
        if (roles is MutableList<*>) {
            return roles.filterIsInstance<String>().toMutableList()
        }
        return mutableListOf()
    }

    private fun extractClientRoles(jwt: Jwt): MutableList<String> {
        val clientId = jwt.getClaimAsString("azp") ?: return mutableListOf()

        val resourceAccess: Map<String, Any> = jwt.getClaimAsMap("resource_access") ?: return mutableListOf()

        val roles = (resourceAccess[clientId] as? MutableMap<*, *>)?.get("roles")
        return (roles as? MutableList<*>)?.filterIsInstance<String>()?.toMutableList() ?: mutableListOf()
    }

}