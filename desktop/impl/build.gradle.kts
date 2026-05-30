plugins {
    kotlin("jvm")
    kotlin("plugin.compose")
    id("org.jetbrains.compose")
    application
}

val java24Launcher = javaToolchains.launcherFor {
    languageVersion.set(JavaLanguageVersion.of(24))
}

val desktopJvmArgs = listOf(
    "--sun-misc-unsafe-memory-access=allow",
    "--enable-native-access=ALL-UNNAMED"
)

dependencies {
    implementation(project(":desktop:desktop-api"))
    implementation(compose.desktop.currentOs)
    implementation(libs.compose.material3)
    implementation(libs.filekit.dialogs.compose)
    implementation(project(":core:core-api"))
    implementation(project(":core:core-utils"))
    implementation(project(":analyzer:analyzer-api"))
    implementation(project(":analyzer:analyzer-impl"))
    implementation(project(":android-runtime:android-runtime-api"))
    implementation(project(":android-runtime:android-runtime-impl"))
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
    applicationDefaultJvmArgs = desktopJvmArgs
}

tasks.named<JavaExec>("run") {
    javaLauncher.set(java24Launcher)
    workingDir = rootProject.projectDir
    jvmArgs(desktopJvmArgs)
}
