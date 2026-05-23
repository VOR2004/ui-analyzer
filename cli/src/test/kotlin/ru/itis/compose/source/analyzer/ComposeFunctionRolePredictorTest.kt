package ru.itis.compose.source.analyzer

import ru.itis.compose.source.role.DefaultComposeFunctionRolePredictor
import kotlin.test.Test
import kotlin.test.assertEquals
import ru.itis.compose.source.model.ComposeFunction
import ru.itis.compose.source.model.ComposeFunctionRole

class ComposeFunctionRolePredictorTest {

    private val predictor = DefaultComposeFunctionRolePredictor()

    @Test
    fun `predicts reusable component by component suffix`() {
        val function = ComposeFunction(
            name = "UserCard",
            parameters = listOf("user: User"),
            filePath = "demo"
        )

        assertEquals(ComposeFunctionRole.REUSABLE_COMPONENT, predictor.predict(function))
    }

    @Test
    fun `predicts theme wrapper by composable content slot`() {
        val function = ComposeFunction(
            name = "UniboardTheme",
            parameters = listOf("content: @Composable () -> Unit"),
            filePath = "demo"
        )

        assertEquals(ComposeFunctionRole.SLOT_WRAPPER, predictor.predict(function))
    }

    @Test
    fun `predicts private function as internal section`() {
        val function = ComposeFunction(
            name = "CollapsedBottomBar",
            parameters = listOf("state: BoardScreenState"),
            filePath = "demo",
            modifiers = setOf("private")
        )

        assertEquals(ComposeFunctionRole.INTERNAL_SECTION, predictor.predict(function))
    }

    @Test
    fun `predicts screen by suffix`() {
        val function = ComposeFunction(
            name = "BoardScreen",
            parameters = listOf("state: BoardScreenState"),
            filePath = "demo"
        )

        assertEquals(ComposeFunctionRole.SCREEN, predictor.predict(function))
    }

    @Test
    fun `predicts theme wrapper by material theme body`() {
        val function = ComposeFunction(
            name = "AppStyle",
            parameters = emptyList(),
            filePath = "demo",
            body = "MaterialTheme(content = {})"
        )

        assertEquals(ComposeFunctionRole.THEME_OR_PROVIDER, predictor.predict(function))
    }

    @Test
    fun `predicts screen by scaffold body`() {
        val function = ComposeFunction(
            name = "Board",
            parameters = emptyList(),
            filePath = "demo",
            body = "Scaffold { Text(\"Hello\") }"
        )

        assertEquals(ComposeFunctionRole.SCREEN, predictor.predict(function))
    }
}
