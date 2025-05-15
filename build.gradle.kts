import java.io.ByteArrayOutputStream

plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "me.thigorigonatti"
version = "1.1"
description = "NuBank challenge, CLI tax calculator on buying and selling shares."

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation("com.fasterxml.jackson.core:jackson-databind:2.19.0")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<Jar> {
    manifest.attributes["Main-Class"] = "me.thiagorigonatti.capitalgains.CapitalGainsCalculator"
    archiveFileName = "CapitalGainsCalculator.jar"
}

tasks.register<Exec>("createCustomJRE") {
    dependsOn("shadowJar")

    val jdkPath = System.getenv("JAVA_HOME") ?: throw GradleException("JAVA_HOME is not defined")
    val outputDir = layout.buildDirectory.dir("custom-jre").get().asFile

    doFirst {
        val jarFile = layout.buildDirectory.file("libs/CapitalGainsCalculator.jar").get().asFile

        if (!jarFile.exists()) throw GradleException("JAR not found: ${jarFile.absolutePath}")

        if (outputDir.exists()) {
            logger.lifecycle("Custom-JRE already exists at: ${outputDir.absolutePath}. Ignoring...")
            throw StopExecutionException("Custom-JRE already exists")
        }

        val jdepsOutput = ByteArrayOutputStream()
        exec {
            commandLine(
                "$jdkPath/bin/jdeps",
                "--print-module-deps",
                "--ignore-missing-deps",
                "--multi-release", "21",
                jarFile.absolutePath
            )
            standardOutput = jdepsOutput
        }

        val modules = jdepsOutput.toString().trim()

        commandLine(
            "$jdkPath/bin/jlink",
            "--module-path", "$jdkPath/jmods",
            "--add-modules", modules,
            "--output", outputDir.absolutePath,
            "--compress", "zip-6",
            "--no-header-files",
            "--no-man-pages"
        )

        logger.lifecycle("Detected modules for creating Custom-JRE are: $modules")
    }

    doLast {
        println("Custom-JRE created at: ${outputDir.absolutePath}")
    }
}



tasks.named("shadowJar") {
    dependsOn("test")
}


tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Test> {
    systemProperty("file.encoding", "UTF-8")

    testLogging {
        events("passed", "skipped", "failed")
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        showStandardStreams = true
    }
}


tasks.withType<Javadoc> {
    options.encoding = "UTF-8"
    classpath += sourceSets["test"].compileClasspath
    source += sourceSets["test"].allJava

    exclude("**/CapitalGainsCalculator.java")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }

    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}


tasks.register("buildAll") {
    dependsOn("javadoc", "test", "shadowJar", "createCustomJRE")
}
