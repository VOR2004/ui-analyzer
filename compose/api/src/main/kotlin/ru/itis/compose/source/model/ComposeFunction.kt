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
            parameter.substringBefore(":").trim() == ComposeFunctionConstants.MODIFIER_PARAMETER_NAME
        }

    val isPrivate: Boolean
        get() = ComposeFunctionConstants.PRIVATE_MODIFIER in modifiers

    val hasComposableContentParameter: Boolean
        get() = parameters.any { parameter ->
            parameter.substringBefore(":").trim() == ComposeFunctionConstants.CONTENT_PARAMETER_NAME &&
                parameter.contains(ComposeFunctionConstants.COMPOSABLE_ANNOTATION)
        }

    val normalizedParameterText: String
        get() = parameters.joinToString(" ")

    val normalizedBodyText: String
        get() = body.replace(Regex("""\s+"""), " ")
}
