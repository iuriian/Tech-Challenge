package br.com.fiap.oficina.domain.repository

import br.com.fiap.oficina.domain.entity.Cliente
import br.com.fiap.oficina.domain.entity.Servico
import org.springframework.data.jpa.repository.JpaRepository

interface ServicoRepository : JpaRepository<Servico, Long> {}