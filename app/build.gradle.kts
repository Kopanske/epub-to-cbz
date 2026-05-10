import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar.Companion.shadowJar

plugins {
    id("kotlin-conventions")
    alias(libs.plugins.shadow)
    application
}

group = "com.github.kopanske"

dependencies {
    implementation(project(":core"))
    implementation(project(":adapters:CliAdapter"))
    implementation(project(":adapters:EpubReadAdapter"))
    implementation(project(":adapters:FileAccessAdapter"))
    implementation(project(":adapters:TerminalOutputAdapter"))
    implementation(project(":adapters:ZipWriter"))
}

tasks.shadowJar {
    archiveBaseName.set("ePubToCbz")
    archiveClassifier.set("all")

    mergeServiceFiles()

    manifest {
        attributes(
            "Main-Class" to "com.github.kopanske.epubtopdf.app.AppKt"
        )
    }
}
