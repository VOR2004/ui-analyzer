plugins {
    kotlin("jvm") version "2.3.20" apply false
    kotlin("plugin.compose") version "2.3.20" apply false
    kotlin("plugin.serialization") version "2.3.20" apply false
    id("org.jetbrains.compose") version "1.10.1" apply false
}

allprojects {
    group = "ru.itis"
    version = "1.0-SNAPSHOT"

    repositories {
        google()
        mavenCentral()
    }
}
