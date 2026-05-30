plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.compose) apply false
}

allprojects {
    group = "ru.itis"
    version = "1.0-SNAPSHOT"

    repositories {
        google()
        mavenCentral()
    }
}
