package ru.itis.compose.source.psi

import org.jetbrains.kotlin.psi.KtCallExpression

internal class ComposePsiArgumentExtractor {

    fun extract(call: KtCallExpression): Map<String, String> {
        val result = linkedMapOf<String, String>()
        var positionalIndex = 0

        call.valueArguments.forEach { argument ->
            val name = argument.getArgumentName()?.asName?.asString()
            val expression = argument.getArgumentExpression()?.text?.trim()
                ?: return@forEach

            if (name == null) {
                result[positionalKey(positionalIndex)] = expression
                positionalIndex++
            } else {
                result[name] = expression
            }
        }

        return result
    }

    fun firstPositionalArgument(arguments: Map<String, String>): String? {
        return arguments[POSITIONAL_ARGUMENT_PREFIX + 0]
    }

    private fun positionalKey(index: Int): String {
        return "$POSITIONAL_ARGUMENT_PREFIX$index"
    }

    private companion object {
        const val POSITIONAL_ARGUMENT_PREFIX = "__positional"
    }
}
