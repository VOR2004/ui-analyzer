plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(":core:core-api"))
}

kotlin {
    jvmToolchain(24)
}
