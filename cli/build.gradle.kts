plugins {
    kotlin("jvm")
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
    applicationDefaultJvmArgs = unsafeAccessJvmArgs()
}

tasks.named<JavaExec>("run") {
    workingDir = rootProject.projectDir
}

tasks.test {
    useJUnitPlatform()
    jvmArgs(unsafeAccessJvmArgs())
}
