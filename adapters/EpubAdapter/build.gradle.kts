plugins {
    id("kotlin-conventions")
}

dependencies {
    api(project(":core"))
    implementation(libs.arrow.core)
    testImplementation(kotlin("test"))
    testImplementation(libs.kotest.framework.engine)
    testImplementation(libs.kotest.runner)
}


tasks.test {
    useJUnitPlatform()
}
