plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(":core:core-api"))
    implementation(project(":core:core-utils"))
    implementation(project(":compose:compose-api"))
    implementation(project(":xml:xml-api"))
}

kotlin {
    jvmToolchain(24)
}
