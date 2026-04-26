plugins {
    id("kotlin-conventions")
}

dependencies {
    api(project(":core"))
    testImplementation(kotlin("test"))
    testImplementation(libs.kotest.framework.engine)
    testImplementation(libs.kotest.runner)
}
