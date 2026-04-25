import nl.littlerobots.vcu.plugin.resolver.VersionSelectors

plugins {
    id("kotlin-conventions")
    alias(libs.plugins.version.catalog.update)

}

versionCatalogUpdate {
    versionSelector(VersionSelectors.STABLE)
}


tasks.named("build") {
    dependsOn(":app:build")
}

