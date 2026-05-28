package br.com.fiap.oficina.domain.repository

import br.com.fiap.oficina.domain.entity.Servico
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ServicoRepository : JpaRepository<Servico, Long>