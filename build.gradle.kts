import nl.littlerobots.vcu.plugin.resolver.VersionSelectors

plugins {
    id("kotlin-conventions")
    alias(libs.plugins.version.catalog.update)
    alias(libs.plugins.shadow)
    application
}

group = "com.github.kopanske"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.arrow.core)
    testImplementation(kotlin("test"))
    testImplementation(libs.kotest.framework.engine)
    testImplementation(libs.kotest.runner)
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
    archiveClassifier.set("all")

    mergeServiceFiles()
}
