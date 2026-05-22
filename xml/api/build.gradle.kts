plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(":common:common-api"))
}

kotlin {
    jvmToolchain(24)
}
