plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

dependencies {
    implementation(project(":core:core-api"))
    implementation(project(":report:report-api"))
    implementation(kotlin("stdlib"))
    implementation(libs.kotlinx.serialization.json)
}

kotlin {
    jvmToolchain(24)
}
