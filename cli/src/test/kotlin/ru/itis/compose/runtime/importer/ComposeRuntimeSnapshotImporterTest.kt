package ru.itis.compose.runtime.importer

import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import ru.itis.model.SourceType

class ComposeRuntimeSnapshotImporterTest {

    @Test
    fun `imports runtime snapshot as compose runtime components`() {
        val snapshotFile = createTempRuntimeSnapshot(
            """
            {
              "screen": "BoardScreen",
              "state": "content",
              "components": [
                {
                  "type": "Button",
                  "locator": "Button[text=Save]",
                  "text": "Save",
                  "isClickable": true,
                  "bounds": {
                    "x": 16,
                    "y": 24,
                    "width": 120,
                    "height": 48
                  },
                  "children": [
                    {
                      "type": "Text",
                      "text": "Save",
                      "textColor": "#FFFFFFFF",
                      "bounds": {
                        "x": 24,
                        "y": 36,
                        "width": 48,
                        "height": 20
                      }
                    }
                  ]
                }
              ]
            }
            """.trimIndent()
        )

        val components = ComposeRuntimeSnapshotImporter().import(snapshotFile)
        val button = components.single()
        val text = button.children.single()

        assertEquals(SourceType.COMPOSE_RUNTIME, button.sourceType)
        assertEquals("Button", button.type)
        assertEquals("/Button[0]", button.treePath)
        assertEquals("120px", button.properties.width)
        assertEquals("48px", button.properties.height)
        assertEquals(16f, button.properties.bounds?.x)
        assertTrue(button.properties.isClickable)
        assertEquals("BoardScreen", button.properties.rawAttributes["runtime:screen"])
        assertEquals("content", button.properties.rawAttributes["runtime:state"])
        assertEquals("Button[text=Save]", button.properties.rawAttributes["runtime:locator"])

        assertEquals(SourceType.COMPOSE_RUNTIME, text.sourceType)
        assertEquals("/Button[0]/Text[0]", text.treePath)
        assertEquals("Save", text.properties.text)
        assertEquals("#FFFFFFFF", text.properties.textColor)
    }

    private fun createTempRuntimeSnapshot(content: String): File {
        return kotlin.io.path.createTempFile(suffix = ".json").toFile()
            .apply { writeText(content) }
    }
}
