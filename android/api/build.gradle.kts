plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(":common:common-api"))
    implementation(kotlin("stdlib"))
}

kotlin {
    jvmToolchain(24)
}
