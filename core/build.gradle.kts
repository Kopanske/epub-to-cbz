plugins {
    id("kotlin-conventions")
}

dependencies {
    implementation(libs.arrow.core)
    testImplementation(kotlin("test"))
    testImplementation(libs.kotest.framework.engine)
    testImplementation(libs.kotest.runner)
}
