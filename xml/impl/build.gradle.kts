plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(":xml:xml-api"))
    implementation(project(":common:common-api"))
    implementation(project(":analyzer:analyzer-api"))
    implementation(kotlin("stdlib"))
}

kotlin {
    jvmToolchain(24)
}
