package ru.itis.compose.source.psi

import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import ru.itis.analyzer.config.components.ComponentTypes
import ru.itis.analyzer.utils.ComponentUtils

class ComposePsiLayoutParserTest {

    @Test
    fun `parses compose tree and resolves local modifier value`() {
        val file = createTempKotlinFile(
            """
            import androidx.compose.foundation.background
            import androidx.compose.foundation.layout.Box
            import androidx.compose.foundation.layout.padding
            import androidx.compose.material3.Text
            import androidx.compose.runtime.Composable
            import androidx.compose.ui.Modifier
            import androidx.compose.ui.graphics.Color
            import androidx.compose.ui.unit.dp

            @Composable
            fun Demo() {
                val cardModifier = Modifier
                    .padding(16.dp)
                    .background(Color(0xFFFFFFFF))

                Box(modifier = cardModifier) {
                    Text(
                        text = "Hello",
                        color = Color(0xFF111111)
                    )
                }
            }
            """.trimIndent()
        )

        val components = ComposePsiLayoutParser().parse(file)
        val box = components.single()
        val text = ComponentUtils.findTextViews(components).single()

        assertEquals(ComponentTypes.COMPOSE_BOX, box.type)
        assertEquals("16.dp", box.properties.padding)
        assertEquals("Color(0xFFFFFFFF)", box.properties.backgroundColor)
        assertEquals("Hello", text.properties.text)
        assertEquals("Color(0xFF111111)", text.properties.textColor)
        assertTrue(text.treePath?.contains("/Box[0]/Text[0]") == true)
    }

    @Test
    fun `extracts compose button colors from psi parsed nested argument call`() {
        val file = createTempKotlinFile(
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

        val components = ComposePsiLayoutParser().parse(file)
        val button = ComponentUtils.flattenAll(components)
            .first { component -> component.type == ComponentTypes.COMPOSE_BUTTON }

        assertEquals("Color(0xFF000000)", button.properties.backgroundColor)
        assertEquals("Color(0xFFFFFFFF)", button.properties.textColor)
    }

    private fun createTempKotlinFile(source: String): File {
        return kotlin.io.path.createTempFile(suffix = ".kt").toFile()
            .apply { writeText(source) }
    }
}
