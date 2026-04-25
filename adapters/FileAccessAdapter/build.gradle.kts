plugins {
    id("kotlin-conventions")
}

dependencies {
    api(project(":core"))
    testImplementation(kotlin("test"))
}
