plugins {
    kotlin("jvm")
    application
}

val java24Launcher = javaToolchains.launcherFor {
    languageVersion.set(JavaLanguageVersion.of(24))
}

val unsafeAccessJvmArgs = listOf("--sun-misc-unsafe-memory-access=allow")

dependencies {
    implementation(project(":core:core-api"))
    testImplementation(project(":core:core-utils"))
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
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(24)
}

application {
    mainClass.set("ru.itis.MainKt")
    applicationDefaultJvmArgs = unsafeAccessJvmArgs
}

tasks.named<JavaExec>("run") {
    javaLauncher.set(java24Launcher)
    workingDir = rootProject.projectDir
    jvmArgs(unsafeAccessJvmArgs)
}

tasks.test {
    javaLauncher.set(java24Launcher)
    useJUnitPlatform()
    jvmArgs(unsafeAccessJvmArgs)
}
