plugins {
    kotlin("jvm")
    kotlin("plugin.compose")
    id("org.jetbrains.compose")
    application
}

fun unsafeAccessJvmArgs(): List<String> {
    return if (JavaVersion.current().majorVersion.toInt() >= 24) {
        listOf("--sun-misc-unsafe-memory-access=allow")
    } else {
        emptyList()
    }
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(compose.material3)
    implementation("io.github.vinceglb:filekit-dialogs-compose:0.14.1")
    implementation(project(":core:core-api"))
    implementation(project(":core:core-utils"))
    implementation(project(":analyzer:analyzer-api"))
    implementation(project(":analyzer:analyzer-impl"))
    implementation(project(":android:android-api"))
    implementation(project(":android:android-impl"))
    implementation(project(":compose:compose-api"))
    implementation(project(":compose:compose-impl"))
    implementation(project(":report:report-api"))
    implementation(project(":report:report-impl"))
    implementation(project(":xml:xml-api"))
    implementation(project(":xml:xml-impl"))
    implementation(kotlin("stdlib"))
}

kotlin {
    jvmToolchain(24)
}

application {
    mainClass.set("ru.itis.desktop.DesktopMainKt")
    applicationDefaultJvmArgs = unsafeAccessJvmArgs()
}

tasks.named<JavaExec>("run") {
    workingDir = rootProject.projectDir
}
