dependencyResolutionManagement {
    pluginManagement {
        repositories {
            gradlePluginPortal()
            mavenCentral()
        }
    }

    // Reuse the version catalog from the main build.
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}

rootProject.name = "buildSrc"
