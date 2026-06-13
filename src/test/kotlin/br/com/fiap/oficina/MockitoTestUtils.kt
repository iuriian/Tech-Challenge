package br.com.fiap.oficina

import org.mockito.Mockito

/**
 * Wrapper para [Mockito.any] que evita o `NullPointerException` "any(...) must not be null"
 * em parâmetros não-nulos do Kotlin. O parâmetro de tipo sem limite permite que o `null`
 * retornado pelo matcher seja propagado sem o check de nulidade gerado pelo compilador.
 */
fun <T> anyObject(): T = Mockito.any()
