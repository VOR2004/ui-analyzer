plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(":core:core-api"))
    implementation(kotlin("stdlib"))
}

kotlin {
    jvmToolchain(24)
}
