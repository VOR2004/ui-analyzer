plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(":xml:xml-api"))
    implementation(project(":core:core-api"))
    implementation(project(":core:core-utils"))
    implementation(project(":analyzer:analyzer-api"))
    implementation(kotlin("stdlib"))
}

kotlin {
    jvmToolchain(24)
}
