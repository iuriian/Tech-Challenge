package br.com.fiap.oficina.infrastructure.persistence.repository

import br.com.fiap.oficina.infrastructure.persistence.entity.ServicoCatalogoJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ServicoJpaRepository :
    JpaRepository<ServicoCatalogoJpaEntity, UUID>
