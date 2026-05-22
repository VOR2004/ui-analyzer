package ru.itis.android.project

import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertEquals

class AndroidProjectPackageResolverTest {

    private val resolver = AndroidProjectPackageResolver()

    @Test
    fun `resolves application id from kotlin gradle file`() {
        val projectRoot = Files.createTempDirectory("android-project").toFile()
        val appDir = projectRoot.resolve("app").also { directory -> directory.mkdirs() }
        appDir.resolve("build.gradle.kts").writeText(
            """
            android {
                namespace = "com.example.namespace"
                defaultConfig {
                    applicationId = "com.example.app"
                }
            }
            """.trimIndent()
        )

        val packageName = resolver.resolve(projectRoot)

        assertEquals("com.example.app", packageName)
    }

    @Test
    fun `falls back to namespace when application id is absent`() {
        val projectRoot = Files.createTempDirectory("android-project").toFile()
        val appDir = projectRoot.resolve("app").also { directory -> directory.mkdirs() }
        appDir.resolve("build.gradle.kts").writeText(
            """
            android {
                namespace = "com.example.namespace"
            }
            """.trimIndent()
        )

        val packageName = resolver.resolve(projectRoot)

        assertEquals("com.example.namespace", packageName)
    }
}
