plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

dependencies {
    implementation(project(":compose:compose-api"))
    implementation(project(":core:core-api"))
    implementation(project(":core:core-utils"))
    implementation(project(":analyzer:analyzer-api"))
    implementation(project(":xml:xml-api"))
    implementation(project(":xml:xml-impl"))
    implementation(kotlin("stdlib"))
    implementation(libs.kotlin.compiler.embeddable)
    implementation(libs.kotlinx.serialization.json)
}

kotlin {
    jvmToolchain(24)
}
