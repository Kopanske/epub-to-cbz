import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.kotlin
import org.gradle.kotlin.dsl.`kotlin-dsl`


plugins {
    `kotlin-dsl`
}

kotlin {
    jvmToolchain(21)
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    // Kotlin Gradle Plugin (liefert u.a. org.jetbrains.kotlin.jvm)
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:${libs.versions.kotlin.get()}")

    // Kotlinter Plugin
    implementation("org.jmailen.gradle:kotlinter-gradle:${libs.versions.kotlinter.get()}")
}
