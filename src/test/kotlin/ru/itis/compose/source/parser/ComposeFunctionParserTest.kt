package ru.itis.compose.source.parser

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ComposeFunctionParserTest {

    @Test
    fun `parses modifier after function type parameter`() {
        val file = kotlin.io.path.createTempFile(suffix = ".kt").toFile()
        file.writeText(
            """
            import androidx.compose.runtime.Composable
            import androidx.compose.ui.Modifier

            @Composable
            fun TextOptions(
                selectedMode: BoardToolMode.Text,
                onSelect: (BoardToolMode.Text) -> Unit,
                modifier: Modifier = Modifier,
                padding: PaddingValues = PaddingValues()
            ) {
            }
            """.trimIndent()
        )

        val function = ComposeFunctionParser().parse(file).single()

        assertTrue(function.hasModifierParameter)
    }

    @Test
    fun `parses private modifier before composable function`() {
        val file = kotlin.io.path.createTempFile(suffix = ".kt").toFile()
        file.writeText(
            """
            import androidx.compose.runtime.Composable

            @Composable
            private fun CollapsedBottomBar() {
            }
            """.trimIndent()
        )

        val function = ComposeFunctionParser().parse(file).single()

        assertEquals(setOf("private"), function.modifiers)
        assertTrue(function.isPrivate)
    }

    @Test
    fun `keeps reporting function without modifier`() {
        val file = kotlin.io.path.createTempFile(suffix = ".kt").toFile()
        file.writeText(
            """
            import androidx.compose.runtime.Composable

            @Composable
            fun TextOptions(
                selectedMode: BoardToolMode.Text,
                onSelect: (BoardToolMode.Text) -> Unit
            ) {
            }
            """.trimIndent()
        )

        val function = ComposeFunctionParser().parse(file).single()

        assertFalse(function.hasModifierParameter)
    }

    @Test
    fun `parses composable function body`() {
        val file = kotlin.io.path.createTempFile(suffix = ".kt").toFile()
        file.writeText(
            """
            import androidx.compose.runtime.Composable

            @Composable
            fun UniboardTheme(content: @Composable () -> Unit) {
                MaterialTheme(
                    content = content
                )
            }
            """.trimIndent()
        )

        val function = ComposeFunctionParser().parse(file).single()

        assertTrue(function.body.contains("MaterialTheme"))
    }
}
