package br.com.fiap.oficina.infrastructure

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["br.com.fiap.oficina"])
class OfficinaApplication

fun main(args: Array<String>) {
	runApplication<OfficinaApplication>(*args)
}
