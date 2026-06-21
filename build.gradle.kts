import org.jetbrains.dokka.DokkaConfiguration.Visibility
import org.jetbrains.dokka.gradle.DokkaTask

plugins {
	kotlin("jvm") version "2.3.21"
	kotlin("plugin.spring") version "2.3.21"
	kotlin("plugin.jpa") version "2.3.21"
	kotlin("kapt") version "2.3.21"

	id("org.jetbrains.dokka") version "2.0.0"
	id("org.springframework.boot") version "3.4.0"
	id("io.spring.dependency-management") version "1.1.6"
	jacoco
	id("org.sonarqube") version "7.3.1.8318"
	id("dev.detekt") version("2.0.0-alpha.3")
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
	classDirectories.setFrom(
		files(classDirectories.files.map {
			fileTree(it) {
				exclude("**/OfficinaApplication*")
			}
		})
	)
}

tasks.named("check") {
	dependsOn("jacocoTestCoverageVerification")
}


tasks.withType<DokkaTask>().configureEach {
	dokkaSourceSets.configureEach {
		documentedVisibilities.set(
			setOf(
				Visibility.PUBLIC,
				Visibility.PROTECTED,
				Visibility.PRIVATE,
				Visibility.PACKAGE,
				Visibility.INTERNAL
			)
		)

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
	// Version of detekt that will be used. When unspecified the latest detekt
	// version found will be used. Override to stay on the same version.
	toolVersion = "2.0.0-alpha.3"
	basePath.set(projectDir)

	// The directories where detekt looks for source files.
	// Defaults to `files("src/main/java", "src/test/java", "src/main/kotlin", "src/test/kotlin")`.
	source.setFrom("src/main/java", "src/main/kotlin")

	// Builds the AST in parallel. Rules are always executed in parallel.
	// Can lead to speedups in larger projects. `false` by default.
	parallel = false

	// Define the detekt configuration(s) you want to use.
	// Defaults to the default detekt configuration.
	config.setFrom("./conf/detekt/detekt.yml")

	// Applies the config files on top of detekt's default config file. `false` by default.
	buildUponDefaultConfig = false

	// Turns on all the rules. `false` by default.
	allRules = false

	// Specifying a baseline file. All findings stored in this file in subsequent runs of detekt.
	baseline = file("path/to/baseline.xml")

	// Disables all default detekt rulesets and will only run detekt with custom rules
	// defined in plugins passed in with `detektPlugins` configuration. `false` by default.
	disableDefaultRuleSets = false

	// Adds debug output during task execution. `false` by default.
	debug = false

	// If set to `true` the build does not fail when there are any issues.
	// Defaults to `false`.
	ignoreFailures = false

	// The build fails when there is at least one issue with this severity (or above).
	// If set ot `Never`, the task will not fail regardless of the number of issues and their severities.
	// If `ignoreFailures` is set to `true`, the value of this property is ignored.
	// Defaults to `Error`
	failOnSeverity = dev.detekt.gradle.extensions.FailOnSeverity.Error

	// Android: Don't create tasks for the specified build types (e.g. "release")
	ignoredBuildTypes = listOf("release")

	// Android: Don't create tasks for the specified build flavor (e.g. "production")
	ignoredFlavors = listOf("production")

	// Android: Don't create tasks for the specified build variants (e.g. "productionRelease")
	ignoredVariants = listOf("productionRelease")

	// Specify the base path for file paths in the formatted reports.
	// If not set, all file paths reported will be absolute file path.
	basePath.set(projectDir)
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
		property("sonar.coverage.jacoco.xmlReportPaths", layout.buildDirectory.file("reports/jacoco/test/jacocoTestReport.xml").get().asFile.absolutePath)
		property("sonar.exclusions", "**/OfficinaApplication.kt,**/config/**,**/dto/**")
		property("sonar.coverage.exclusions", "**/OfficinaApplication.kt,**/config/**,**/dto/**")
		property("sonar.kotlin.detekt.reportPaths", layout.buildDirectory.file("reports/detekt/detekt.xml").get().asFile.absolutePath)
	}
}
