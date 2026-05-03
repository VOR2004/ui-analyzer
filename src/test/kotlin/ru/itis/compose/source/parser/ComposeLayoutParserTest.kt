package ru.itis.compose.source.parser

import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import ru.itis.analyzer.config.ComponentTypes
import ru.itis.analyzer.utils.ComponentUtils
import ru.itis.model.SourceType

class ComposeLayoutParserTest {

    @Test
    fun `parses compose ui tree into ui components`() {
        val file = File("src/test/resources/demo-project/app/src/main/java/ru/itis/demo/ComposeDemo.kt")

        val components = ComposeLayoutParser().parse(file)
        val root = components.singleOrNull()

        assertNotNull(root)
        assertEquals(ComponentTypes.COMPOSE_COLUMN, root.type)
        assertEquals(SourceType.COMPOSE, root.sourceType)
        assertEquals("16.dp", root.properties.padding)
        assertTrue(root.treePath?.contains("/Column[0]") == true)

        val textComponents = ComponentUtils.findTextViews(components)
        assertTrue(
            textComponents.any { text ->
                text.properties.text == "Compose title" &&
                    text.properties.textSize == "24.sp" &&
                    text.properties.textColor == "Color.Black"
            },
            "Expected parser to extract Compose Text properties"
        )

        val images = ComponentUtils.findImageComponents(components)
        assertTrue(
            images.any { image -> image.contentDescriptionOrNull() == "Demo image" },
            "Expected parser to extract Image contentDescription"
        )
        assertTrue(
            images.any { image ->
                image.type == ComponentTypes.COMPOSE_ICON &&
                    image.properties.contentDescription == "null"
            },
            "Expected parser to keep explicit null contentDescription for Icon"
        )
    }

    @Test
    fun `does not treat dynamic text expressions as hardcoded text`() {
        val file = kotlin.io.path.createTempFile(suffix = ".kt").toFile()
        file.writeText(
            """
            import androidx.compose.material3.Text
            import androidx.compose.runtime.Composable

            @Composable
            fun Demo(index: Int, text: String) {
                Text(index.toString())
                Text(text = text)
                Text(text = "Real literal")
            }
            """.trimIndent()
        )

        val components = ComposeLayoutParser().parse(file)
        val texts = ComponentUtils.findTextViews(components)

        assertTrue(
            texts.any { text -> text.properties.rawAttributes["text"] == "text" },
            "Expected dynamic named argument to stay in rawAttributes"
        )
        assertTrue(
            texts.any { text -> text.properties.text == "Real literal" },
            "Expected real string literal to be extracted"
        )
        assertEquals(1, texts.count { text -> text.properties.text != null })
        assertNull(texts.first { text -> text.properties.rawAttributes["text"] == "text" }.properties.text)
    }

    @Test
    fun `extracts colors from compose button colors argument`() {
        val file = kotlin.io.path.createTempFile(suffix = ".kt").toFile()
        file.writeText(
            """
            import androidx.compose.material3.Button
            import androidx.compose.material3.ButtonDefaults
            import androidx.compose.material3.Text
            import androidx.compose.runtime.Composable
            import androidx.compose.ui.graphics.Color

            @Composable
            fun Demo() {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF000000),
                        contentColor = Color(0xFFFFFFFF)
                    ),
                    onClick = {}
                ) {
                    Text("Open")
                }
            }
            """.trimIndent()
        )

        val components = ComposeLayoutParser().parse(file)
        val button = ComponentUtils.flattenAll(components)
            .first { component -> component.type == ComponentTypes.COMPOSE_BUTTON }

        assertEquals("Color(0xFF000000)", button.properties.backgroundColor)
        assertEquals("Color(0xFFFFFFFF)", button.properties.textColor)
    }

    @Test
    fun `extracts compose text typography style`() {
        val file = kotlin.io.path.createTempFile(suffix = ".kt").toFile()
        file.writeText(
            """
            import androidx.compose.material3.MaterialTheme
            import androidx.compose.material3.Text
            import androidx.compose.runtime.Composable

            @Composable
            fun Demo() {
                Text(
                    text = "Title",
                    style = MaterialTheme.typography.titleLarge
                )
            }
            """.trimIndent()
        )

        val text = ComponentUtils.findTextViews(ComposeLayoutParser().parse(file)).single()

        assertEquals("MaterialTheme.typography.titleLarge", text.properties.typographyStyle)
    }

    @Test
    fun `extracts clickable modifier`() {
        val file = kotlin.io.path.createTempFile(suffix = ".kt").toFile()
        file.writeText(
            """
            import androidx.compose.foundation.clickable
            import androidx.compose.foundation.layout.size
            import androidx.compose.material3.Text
            import androidx.compose.runtime.Composable
            import androidx.compose.ui.Modifier
            import androidx.compose.ui.unit.dp

            @Composable
            fun Demo(onClick: () -> Unit) {
                Text(
                    text = "Open",
                    modifier = Modifier
                        .size(40.dp)
                        .clickable { onClick() }
                )
            }
            """.trimIndent()
        )

        val text = ComponentUtils.findTextViews(ComposeLayoutParser().parse(file)).single()

        assertEquals("40.dp", text.properties.width)
        assertEquals("40.dp", text.properties.height)
        assertTrue(text.properties.isClickable)
    }

    private fun ru.itis.model.UiComponent.contentDescriptionOrNull(): String? {
        return properties.contentDescription
    }
}
