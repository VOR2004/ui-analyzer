plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(":common:common-api"))
    implementation(project(":compose:compose-api"))
    implementation(project(":xml:xml-api"))
}

kotlin {
    jvmToolchain(24)
}
