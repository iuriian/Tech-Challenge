package br.com.fiap.oficina.infrastructure.persistence.entity

import br.com.fiap.oficina.domain.valueobject.TipoPessoa
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated

@Embeddable
class DocumentoEmbeddable {
    @Column(name = "documento_numero", nullable = false, unique = true)
    lateinit var numero: String

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_pessoa", nullable = false)
    lateinit var tipoPessoa: TipoPessoa
}
