plugins {
    `kotlin-dsl`
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
