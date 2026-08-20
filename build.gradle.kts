import org.jetbrains.dokka.gradle.engine.parameters.VisibilityModifier

plugins {
    kotlin("jvm") version "2.3.21"
    kotlin("plugin.spring") version "2.3.21"
    kotlin("plugin.jpa") version "2.3.21"
    kotlin("kapt") version "2.3.21"

    id("org.jetbrains.dokka") version "2.2.0"
    id("org.springframework.boot") version "3.4.0"
    id("io.spring.dependency-management") version "1.1.6"
    jacoco
    id("org.sonarqube") version "7.3.1.8318"
    id("dev.detekt") version ("2.0.0-alpha.3")

    id("com.diffplug.spotless") version "7.0.2"
}

group = "br.com.fiap.oficina"
version = "0.0.1"

val mapstructVersion = "1.6.3"
val openapiVersion = "2.8.5"

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
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")

    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:$openapiVersion")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")

    "developmentOnly"("org.springframework.boot:spring-boot-devtools")
    kapt("org.mapstruct:mapstruct-processor:$mapstructVersion")
    runtimeOnly("org.postgresql:postgresql")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")

    // Testes de integração com banco PostgreSQL real via Testcontainers
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
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

    if (System.getenv("API_VERSION") == null) {
        environment("API_VERSION", "1.43")
    }
    jvmArgs("-Dapi.version=1.43")
}

// Jacoco
val coverageExclusions = listOf("**/OfficinaApplication*", "**/config/**")

tasks.withType<JacocoReportBase>().configureEach {
    classDirectories.setFrom(
        files(
            classDirectories.files.map {
                fileTree(it) {
                    exclude(coverageExclusions)
                }
            },
        ),
    )
}

val jacocoTestReport =
    tasks.named<JacocoReport>("jacocoTestReport") {
        dependsOn(tasks.named<Test>("test"))
        reports {
            xml.required.set(true)
            html.required.set(true)
        }
    }

tasks.named<Test>("test") {
    finalizedBy(jacocoTestReport)
}

tasks.named<JacocoCoverageVerification>("jacocoTestCoverageVerification") {
    dependsOn(jacocoTestReport)
    violationRules {
        rule {
            limit {
                counter = "INSTRUCTION"
                value = "COVEREDRATIO"
                minimum = "0.80".toBigDecimal()
            }
            limit {
                counter = "BRANCH"
                value = "COVEREDRATIO"
                minimum = "0.75".toBigDecimal()
            }
            limit {
                counter = "LINE"
                value = "COVEREDRATIO"
                minimum = "0.80".toBigDecimal()
            }
        }
    }
}

tasks.named("check") {
    dependsOn("jacocoTestCoverageVerification")
}

val dokkaVisibility =
    setOf(
        VisibilityModifier.Public,
        VisibilityModifier.Protected,
        VisibilityModifier.Private,
        VisibilityModifier.Package,
        VisibilityModifier.Internal,
    )

dokka {
    dokkaSourceSets.configureEach {
        documentedVisibilities.set(dokkaVisibility)
        perPackageOption {
            matchingRegex.set(".*internal.*")
            suppress.set(true)
        }
    }
}

configurations.matching { it.name.startsWith("dokka") }.configureEach {
    resolutionStrategy.eachDependency {
        if (requested.group.startsWith("com.fasterxml.jackson")) {
            useVersion("2.15.3")
        }
    }
}

detekt {
    toolVersion = "2.0.0-alpha.3"
    config.setFrom("conf/detekt/detekt.yml")
    source.setFrom("src/main/kotlin")
    baseline = file("detekt-baseline.xml")
    failOnSeverity = dev.detekt.gradle.extensions.FailOnSeverity.Warning
}

sonar {
    properties {
        property("sonar.projectKey", "br.com.fiap.oficina:tech-challenge")
        property("sonar.projectName", "Tech-Challenge")
        property("sonar.host.url", System.getenv("SONAR_HOST_URL") ?: "http://localhost:9000")
        property("sonar.login", System.getenv("SONAR_LOGIN") ?: "admin")
        property("sonar.password", System.getenv("SONAR_PASSWORD") ?: "c0cada")
        property("sonar.sourceEncoding", "UTF-8")
        property("sonar.sources", "src/main/kotlin")
        property("sonar.tests", "src/test/kotlin")
        property(
            "sonar.coverage.jacoco.xmlReportPaths",
            layout.buildDirectory
                .file("reports/jacoco/test/jacocoTestReport.xml")
                .get()
                .asFile.absolutePath,
        )
        property("sonar.exclusions", "**/OfficinaApplication.kt,**/config/**,**/dto/**")
        property("sonar.coverage.exclusions", "**/OfficinaApplication.kt,**/config/**,**/dto/**")
        property(
            "sonar.kotlin.detekt.reportPaths",
            layout.buildDirectory
                .file("reports/detekt/detekt.xml")
                .get()
                .asFile.absolutePath,
        )
    }
}

spotless {
    kotlin {
        target("src/**/*.kt")
        targetExclude("**/build/**")

        ktlint("1.5.0")
            .editorConfigOverride(
                mapOf(
                    "max_line_length" to "120",
                    "indent_size" to "4",
                    "ij_kotlin_allow_trailing_comma" to "true",
                    "ij_kotlin_allow_trailing_comma_on_call_site" to "true",
                ),
            )

        trimTrailingWhitespace()
        endWithNewline()
    }

    kotlinGradle {
        target("*.gradle.kts")
        ktlint("1.5.0")
            .editorConfigOverride(
                mapOf(
                    "max_line_length" to "120",
                ),
            )
        endWithNewline()
    }
}
