plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "epub-to-cbz"

include(
    "core",
    "adapters:EpubAdapter",
    "adapters:FileAccessAdapter",
    "adapters:TerminalOutputAdapter"
)

include("app")
include("adapters:CliAdapter")