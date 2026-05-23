package ru.itis.android.runtime.uiautomator

import ru.itis.android.runtime.uiautomator.parser.UiAutomatorDumpParser
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import ru.itis.model.SourceType

class UiAutomatorDumpParserTest {

    @Test
    fun `parses uiautomator dump into android runtime components`() {
        val file = createTempDump(
            """
            <?xml version='1.0' encoding='UTF-8' standalone='yes' ?>
            <hierarchy rotation="0">
              <node
                  index="0"
                  text=""
                  resource-id="com.example:id/root"
                  class="android.widget.FrameLayout"
                  package="com.example"
                  content-desc=""
                  clickable="false"
                  bounds="[0,0][1080,2400]">
                <node
                    index="0"
                    text="Save"
                    resource-id="com.example:id/save"
                    class="android.widget.Button"
                    package="com.example"
                    content-desc="Save board"
                    clickable="true"
                    bounds="[24,48][224,144]" />
              </node>
            </hierarchy>
            """.trimIndent()
        )

        val root = UiAutomatorDumpParser().parse(file).single()
        val button = root.children.single()

        assertEquals(SourceType.ANDROID_RUNTIME, root.sourceType)
        assertEquals("android.widget.FrameLayout", root.type)
        assertEquals("com.example:id/root", root.id)
        assertEquals("/android.widget.FrameLayout[0]", root.treePath)
        assertEquals("1080px", root.properties.width)
        assertEquals("2400px", root.properties.height)
        assertFalse(root.properties.isClickable)

        assertEquals(SourceType.ANDROID_RUNTIME, button.sourceType)
        assertEquals("android.widget.Button", button.type)
        assertEquals("com.example:id/save", button.id)
        assertEquals("Save", button.properties.text)
        assertEquals("Save board", button.properties.contentDescription)
        assertEquals("200px", button.properties.width)
        assertEquals("96px", button.properties.height)
        assertEquals(24f, button.properties.bounds?.x)
        assertEquals(48f, button.properties.bounds?.y)
        assertTrue(button.properties.isClickable)
        assertEquals("com.example", button.properties.rawAttributes["package"])
    }

    private fun createTempDump(content: String): File {
        return kotlin.io.path.createTempFile(suffix = ".xml").toFile()
            .apply { writeText(content) }
    }
}
