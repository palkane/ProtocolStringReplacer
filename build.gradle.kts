import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm") version "2.3.10"
    id("java")
    id("com.gradleup.shadow") version "9.2.2"
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.21" apply false
}

repositories {
    mavenCentral()
}

group = "io.github.rothes"
version = rootProject.property("versionName").toString()

tasks.register<Copy>("createJars") {
    from(project(":bukkit").tasks.named("shadowJar"))
    into(layout.buildDirectory.dir("allJars"))
}


allprojects {
    apply(plugin = "java")
    apply(plugin = "kotlin")
    apply(plugin = "com.gradleup.shadow")

    group = "io.github.rothes.protocolstringreplacer"
    version = rootProject.property("versionName").toString()

    val javaVer = JavaVersion.VERSION_17

    java {
        disableAutoTargetJvm()
        toolchain.languageVersion.set(JavaLanguageVersion.of(25))
        sourceCompatibility = javaVer
        targetCompatibility = javaVer
        withSourcesJar()
        withJavadocJar()
    }

    tasks.compileJava {
        options.encoding = "UTF-8"
        options.release.set(javaVer.majorVersion.toInt())
    }

    kotlin {
        compilerOptions.jvmTarget.set(JvmTarget.fromTarget(javaVer.toString()))
    }

    tasks.javadoc {
        options.encoding = "UTF-8"
    }
}
