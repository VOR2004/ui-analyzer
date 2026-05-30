package ru.itis.compose.source.importer

import java.io.File
import kotlin.test.Test
import kotlin.test.assertTrue

class ComposeProjectImporterTest {

    @Test
    fun `finds kotlin files with composable ui`() {
        val projectRoot = File("src/test/resources/demo-project")

        val files = ComposeProjectImporter().findComposeKotlinFiles(projectRoot)

        assertTrue(
            files.any { file -> file.name == "ComposeDemo.kt" },
            "Expected importer to find Compose demo file"
        )
    }
}
