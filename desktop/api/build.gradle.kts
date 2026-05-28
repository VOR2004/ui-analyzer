plugins {
    kotlin("jvm")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":core:core-api"))
}

kotlin {
    jvmToolchain(24)
}
