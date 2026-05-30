plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

dependencies {
    implementation(project(":core:core-api"))
    implementation(kotlin("stdlib"))
    implementation(libs.kotlinx.serialization.core)
}

kotlin {
    jvmToolchain(24)
}
