plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jmailen.kotlinter")
}

kotlin {
    jvmToolchain(21)
}

repositories {
    mavenCentral()
}