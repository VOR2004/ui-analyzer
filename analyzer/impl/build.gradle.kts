plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(":analyzer:analyzer-api"))
    implementation(project(":core:core-api"))
    testImplementation(project(":core:core-utils"))
    implementation(project(":compose:compose-api"))
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
