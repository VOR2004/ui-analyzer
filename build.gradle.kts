plugins {
    kotlin("jvm") version "2.3.20"
    kotlin("plugin.serialization") version "2.3.20"
    application
}

group = "ru.itis"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlin:kotlin-compiler-embeddable:2.3.20")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(24)
}

application {
    mainClass.set("ru.itis.MainKt")
    applicationDefaultJvmArgs = listOf("--sun-misc-unsafe-memory-access=allow")
}

tasks.test {
    useJUnitPlatform()
    jvmArgs("--sun-misc-unsafe-memory-access=allow")
}
