plugins {
    kotlin("jvm") version "2.3.20" apply false
    kotlin("plugin.serialization") version "2.3.20" apply false
}

allprojects {
    group = "ru.itis"
    version = "1.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}
