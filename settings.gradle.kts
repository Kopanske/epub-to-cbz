plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "epub-to-cbz"

include(
    "app",
    "core",
    "adapters:CliAdapter",
    "adapters:EpubAdapter",
    "adapters:FileAccessAdapter",
    "adapters:TerminalOutputAdapter",
)
