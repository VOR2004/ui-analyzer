package ru.itis.compose.source.model

data class ComposeFunction(
    val name: String,
    val parameters: List<String>,
    val filePath: String,
    val modifiers: Set<String> = emptySet(),
    val body: String = ""
) {
    val hasModifierParameter: Boolean
        get() = parameters.any { parameter ->
            parameter.substringBefore(":").trim() == MODIFIER_PARAMETER_NAME
        }

    val isPrivate: Boolean
        get() = PRIVATE_MODIFIER in modifiers

    val hasComposableContentParameter: Boolean
        get() = parameters.any { parameter ->
            parameter.substringBefore(":").trim() == CONTENT_PARAMETER_NAME &&
                parameter.contains(COMPOSABLE_ANNOTATION)
        }

    val normalizedParameterText: String
        get() = parameters.joinToString(" ")

    val normalizedBodyText: String
        get() = body.replace(Regex("""\s+"""), " ")

    private companion object {
        const val MODIFIER_PARAMETER_NAME = "modifier"
        const val PRIVATE_MODIFIER = "private"
        const val CONTENT_PARAMETER_NAME = "content"
        const val COMPOSABLE_ANNOTATION = "@Composable"
    }
}
