plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(":analyzer:analyzer-api"))
    implementation(project(":core:core-api"))
    implementation(project(":compose:compose-api"))
    implementation(project(":xml:xml-api"))
    implementation(project(":xml:xml-impl"))
    implementation(kotlin("stdlib"))
}

kotlin {
    jvmToolchain(24)
}
