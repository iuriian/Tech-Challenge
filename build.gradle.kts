plugins {
	kotlin("jvm") version "2.2.21"
	kotlin("plugin.spring") version "2.2.21"
	kotlin("plugin.jpa") version "2.2.21"
	kotlin("kapt") version "2.2.21"
	id("org.springframework.boot") version "3.4.0"
	id("io.spring.dependency-management") version "1.1.6"
	jacoco
	id("org.sonarqube") version "6.0.1.5171"
}

group = "br.com.fiap.oficina"
version = "0.0.1"

val mapstructVersion = "1.6.3"
val openapiVersion  = "2.8.5"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.mapstruct:mapstruct:$mapstructVersion")
	implementation("org.springframework.boot:spring-boot-starter-actuator")

	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:$openapiVersion")
	"developmentOnly"("org.springframework.boot:spring-boot-devtools")
	kapt("org.mapstruct:mapstruct-processor:$mapstructVersion")
	runtimeOnly("org.postgresql:postgresql")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
	}
}

tasks.withType<JavaExec>().configureEach {
	jvmArgs("--enable-native-access=ALL-UNNAMED")
}

tasks.named<org.springframework.boot.gradle.tasks.run.BootRun>("bootRun") {
	jvmArgs("--enable-native-access=ALL-UNNAMED")
}

tasks.bootRun {
	jvmArgs("--enable-native-access=ALL-UNNAMED")
}
tasks.withType<Test> {
	useJUnitPlatform()
}

val jacocoTestReport by tasks.getting(JacocoReport::class) {
	dependsOn(tasks.test)
	reports {
		xml.required.set(true)
		html.required.set(true)
	}
	classDirectories.setFrom(
		files(classDirectories.files.map {
			fileTree(it) {
				exclude("**/OfficinaApplication*")
			}
		})
	)
}

tasks.test {
	finalizedBy(jacocoTestReport)
}

sonar {
	properties {
		property("sonar.projectKey", "br.com.fiap.oficina:tech-challenge")
		property("sonar.projectName", "Tech-Challenge")
		property("sonar.host.url", "http://localhost:9000") // URL padrão
		property("sonar.language", "kotlin")
		property("sonar.sourceEncoding", "UTF-8")
		property("sonar.coverage.jacoco.xmlReportPaths", layout.buildDirectory.file("reports/jacoco/test/jacocoTestReport.xml").get().asFile.absolutePath)
		property("sonar.exclusions", "**/OfficinaApplication.kt")
	}
}
