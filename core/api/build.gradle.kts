plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(libs.kotlinx.serialization.core)
}

kotlin {
    jvmToolchain(24)
}
