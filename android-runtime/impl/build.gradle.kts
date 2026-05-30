plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(":android-runtime:android-runtime-api"))
    implementation(project(":core:core-api"))
    implementation(project(":xml:xml-api"))
    implementation(project(":xml:xml-impl"))
    implementation(kotlin("stdlib"))
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(24)
}

tasks.test {
    useJUnitPlatform()
}
