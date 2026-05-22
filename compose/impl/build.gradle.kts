plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

dependencies {
    implementation(project(":compose:compose-api"))
    implementation(project(":common:common-api"))
    implementation(project(":analyzer:analyzer-api"))
    implementation(project(":xml:xml-api"))
    implementation(project(":xml:xml-impl"))
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlin:kotlin-compiler-embeddable:2.3.20")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
}

kotlin {
    jvmToolchain(24)
}
