import nl.littlerobots.vcu.plugin.resolver.VersionSelectors

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.version.catalog.update)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.shadow)
    application
}

group = "com.github.kopanske"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation(libs.kotest.framework.engine)
    testImplementation(libs.kotest.property)
    testImplementation(libs.kotest.runner)
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}

versionCatalogUpdate {
    versionSelector(VersionSelectors.STABLE)
}

application {
    mainClass = "com.github.kopanske.epubtopdf.MainKt"
}

tasks.shadowJar {
    archiveBaseName.set("ePubToCbz")
    archiveVersion.set("1.0.2")
    archiveClassifier.set("all")

    mergeServiceFiles()
}
