plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(":android:android-api"))
    implementation(project(":common:common-api"))
    implementation(project(":xml:xml-api"))
    implementation(kotlin("stdlib"))
}

kotlin {
    jvmToolchain(24)
}
