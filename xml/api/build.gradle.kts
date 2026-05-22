plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(":core:core-api"))
    implementation(project(":core:core-utils"))
}

kotlin {
    jvmToolchain(24)
}
