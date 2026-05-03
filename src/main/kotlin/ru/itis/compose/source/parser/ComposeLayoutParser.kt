package ru.itis.compose.source.parser

import java.io.File
import ru.itis.analyzer.config.ComponentTypes
import ru.itis.model.SourceType
import ru.itis.model.UiComponent
import ru.itis.model.UiProperties

class ComposeLayoutParser {

    fun parse(file: File): List<UiComponent> {
        val source = file.readText()
        return parseComponents(
            source = source,
            startIndex = 0,
            endIndex = source.length,
            filePath = file.absolutePath,
            parentPath = ""
        ).components
    }

    private fun parseComponents(
        source: String,
        startIndex: Int,
        endIndex: Int,
        filePath: String,
        parentPath: String
    ): ParseResult {
        val components = mutableListOf<UiComponent>()
        var index = startIndex
        while (index < endIndex) {
            val match = findNextComposableCall(source, index, endIndex)
            if (match == null) {
                index = endIndex
                continue
            }

            val call = parseCall(source, match, endIndex, filePath, parentPath, components.size)
            if (call == null) {
                index = match.nameEnd
                continue
            }

            components += call.component
            index = call.endIndex
        }

        return ParseResult(components = components, endIndex = index)
    }

    private fun parseCall(
        source: String,
        match: CallMatch,
        endIndex: Int,
        filePath: String,
        parentPath: String,
        siblingIndex: Int
    ): ParsedCall? {
        val openParen = source.indexOfSkippingWhitespace(match.nameEnd, endIndex, '(') ?: return null
        val closeParen = findMatching(source, openParen, '(', ')', endIndex) ?: return null
        val arguments = source.substring(openParen + 1, closeParen)
        val rawAttributes = parseNamedArguments(arguments)
        val treePath = "$parentPath/${match.name}[$siblingIndex]"

        val openBrace = source.indexOfSkippingWhitespace(closeParen + 1, endIndex, '{')
        val closeBrace = openBrace?.let { findMatching(source, it, '{', '}', endIndex) }
        val children = if (openBrace != null && closeBrace != null) {
            parseComponents(
                source = source,
                startIndex = openBrace + 1,
                endIndex = closeBrace,
                filePath = filePath,
                parentPath = treePath
            ).components
        } else {
            emptyList()
        }

        return ParsedCall(
            component = UiComponent(
                id = rawAttributes[TEST_TAG_ARGUMENT]?.trimStringLiteralIfNeeded(),
                type = match.name,
                sourceType = SourceType.COMPOSE,
                filePath = filePath,
                treePath = treePath,
                properties = buildProperties(match.name, arguments, rawAttributes),
                children = children
            ),
            endIndex = closeBrace?.plus(1) ?: (closeParen + 1)
        )
    }

    private fun buildProperties(
        type: String,
        arguments: String,
        rawAttributes: Map<String, String>
    ): UiProperties {
        val modifier = rawAttributes[MODIFIER_ARGUMENT].orEmpty()
        val colors = rawAttributes[COLORS_ARGUMENT].orEmpty()
        val fontWeight = rawAttributes[FONT_WEIGHT_ARGUMENT]
        val fontStyle = rawAttributes[FONT_STYLE_ARGUMENT]

        return UiProperties(
            width = extractModifierDimension(modifier, WIDTH_MODIFIER)
                ?: extractModifierDimension(modifier, SIZE_MODIFIER),
            height = extractModifierDimension(modifier, HEIGHT_MODIFIER)
                ?: extractModifierDimension(modifier, SIZE_MODIFIER),
            padding = extractModifierDimension(modifier, PADDING_MODIFIER),
            backgroundColor = extractModifierValue(modifier, BACKGROUND_MODIFIER)
                ?: rawAttributes[CONTAINER_COLOR_ARGUMENT]
                ?: extractNamedArgumentValue(colors, CONTAINER_COLOR_ARGUMENT),
            textColor = rawAttributes[COLOR_ARGUMENT]
                ?: rawAttributes[CONTENT_COLOR_ARGUMENT]
                ?: extractNamedArgumentValue(colors, CONTENT_COLOR_ARGUMENT),
            textSize = rawAttributes[FONT_SIZE_ARGUMENT],
            typographyStyle = rawAttributes[STYLE_ARGUMENT],
            fontFamily = rawAttributes[FONT_FAMILY_ARGUMENT],
            textStyle = listOfNotNull(fontWeight, fontStyle).joinToString(" ").ifBlank { null },
            text = extractComposeText(type, arguments, rawAttributes),
            contentDescription = rawAttributes[CONTENT_DESCRIPTION_ARGUMENT]?.trimStringLiteralIfNeeded(),
            isClickable = modifier.containsModifier(CLICKABLE_MODIFIER),
            rawAttributes = rawAttributes
        )
    }

    private fun extractNamedArgumentValue(arguments: String, name: String): String? {
        val normalizedArguments = unwrapCallArguments(arguments) ?: arguments
        return parseNamedArguments(normalizedArguments)[name]
    }

    private fun unwrapCallArguments(value: String): String? {
        val openParen = value.indexOf('(')
        if (openParen < 0) return null

        val closeParen = findMatching(value, openParen, '(', ')', value.length) ?: return null
        return value.substring(openParen + 1, closeParen)
    }

    private fun extractComposeText(
        type: String,
        arguments: String,
        rawAttributes: Map<String, String>
    ): String? {
        if (type != ComponentTypes.COMPOSE_TEXT && type != ComponentTypes.COMPOSE_BUTTON) {
            return null
        }

        rawAttributes[TEXT_ARGUMENT]
            ?.takeIf { value -> value.isStringLiteral() }
            ?.let { value -> return value.trimStringLiteral() }

        val firstArgument = splitTopLevel(arguments, ',').firstOrNull() ?: return null
        return firstArgument
            .takeIf { value -> value.isStringLiteral() }
            ?.trimStringLiteral()
    }

    private fun parseNamedArguments(arguments: String): Map<String, String> {
        return splitTopLevel(arguments, ',')
            .mapNotNull { argument ->
                val equalsIndex = indexOfTopLevel(argument, '=')
                if (equalsIndex == null) {
                    null
                } else {
                    val name = argument.substring(0, equalsIndex).trim()
                    val value = argument.substring(equalsIndex + 1).trim()
                    if (name.isBlank() || value.isBlank()) null else name to value
                }
            }
            .toMap()
    }

    private fun findNextComposableCall(source: String, startIndex: Int, endIndex: Int): CallMatch? {
        var index = startIndex
        while (index < endIndex) {
            if (!source[index].isIdentifierStart()) {
                index++
                continue
            }

            val nameStart = index
            index++
            while (index < endIndex && source[index].isIdentifierPart()) {
                index++
            }

            val name = source.substring(nameStart, index)
            if (name in composeFunctionNames &&
                source.indexOfSkippingWhitespace(index, endIndex, '(') != null
            ) {
                return CallMatch(name = name, nameEnd = index)
            }
        }

        return null
    }

    private fun findMatching(
        source: String,
        openIndex: Int,
        openChar: Char,
        closeChar: Char,
        endIndex: Int
    ): Int? {
        var depth = 0
        var index = openIndex
        var inString = false
        var escaped = false

        while (index < endIndex) {
            val char = source[index]
            if (inString) {
                escaped = char == '\\' && !escaped
                if (char == '"') {
                    inString = false
                } else if (char != '\\') {
                    escaped = false
                }
                index++
                continue
            }

            when (char) {
                '"' -> inString = true
                openChar -> depth++
                closeChar -> {
                    depth--
                    if (depth == 0) return index
                }
            }
            index++
        }

        return null
    }

    private fun splitTopLevel(value: String, separator: Char): List<String> {
        val result = mutableListOf<String>()
        var start = 0
        var parenDepth = 0
        var braceDepth = 0
        var stringDepth = false
        var index = 0

        while (index < value.length) {
            val char = value[index]
            when {
                char == '"' -> stringDepth = !stringDepth
                !stringDepth && char == '(' -> parenDepth++
                !stringDepth && char == ')' -> parenDepth--
                !stringDepth && char == '{' -> braceDepth++
                !stringDepth && char == '}' -> braceDepth--
                !stringDepth && char == separator && parenDepth == 0 && braceDepth == 0 -> {
                    result += value.substring(start, index).trim()
                    start = index + 1
                }
            }
            index++
        }

        result += value.substring(start).trim()
        return result.filter { it.isNotBlank() }
    }

    private fun indexOfTopLevel(value: String, target: Char): Int? {
        var parenDepth = 0
        var braceDepth = 0
        var inString = false
        for (index in value.indices) {
            val char = value[index]
            when {
                char == '"' -> inString = !inString
                !inString && char == '(' -> parenDepth++
                !inString && char == ')' -> parenDepth--
                !inString && char == '{' -> braceDepth++
                !inString && char == '}' -> braceDepth--
                !inString && char == target && parenDepth == 0 && braceDepth == 0 -> return index
            }
        }
        return null
    }

    private fun extractModifierDimension(modifier: String, modifierName: String): String? {
        return extractModifierValue(modifier, modifierName)
            ?.let { extractFirstDimension(it) }
    }

    private fun extractModifierValue(modifier: String, modifierName: String): String? {
        val start = modifier.indexOf(".$modifierName(")
        if (start < 0) return null

        val openParen = modifier.indexOf('(', start)
        val closeParen = findMatching(modifier, openParen, '(', ')', modifier.length) ?: return null
        return modifier.substring(openParen + 1, closeParen).trim()
    }

    private fun String.containsModifier(modifierName: String): Boolean {
        return contains(".$modifierName(") || contains(".$modifierName {")
    }

    private fun extractFirstDimension(value: String): String? {
        return DIMENSION_PATTERN.find(value)?.value
    }

    private fun extractFirstStringLiteral(value: String): String? {
        return STRING_LITERAL_PATTERN.find(value)?.groupValues?.get(1)
    }

    private fun String.isStringLiteral(): Boolean {
        val trimmed = trim()
        return trimmed.length >= 2 && trimmed.first() == '"' && trimmed.last() == '"'
    }

    private fun String.trimStringLiteral(): String {
        return trim().removePrefix("\"").removeSuffix("\"")
    }

    private fun String.trimStringLiteralIfNeeded(): String {
        return if (isStringLiteral()) trimStringLiteral() else this
    }

    private fun CharSequence.indexOfSkippingWhitespace(
        startIndex: Int,
        endIndex: Int,
        target: Char
    ): Int? {
        var index = startIndex
        while (index < endIndex && this[index].isWhitespace()) {
            index++
        }
        return if (index < endIndex && this[index] == target) index else null
    }

    private fun Char.isIdentifierStart(): Boolean {
        return isLetter() || this == '_'
    }

    private fun Char.isIdentifierPart(): Boolean {
        return isLetterOrDigit() || this == '_'
    }

    private data class CallMatch(
        val name: String,
        val nameEnd: Int
    )

    private data class ParsedCall(
        val component: UiComponent,
        val endIndex: Int
    )

    private data class ParseResult(
        val components: List<UiComponent>,
        val endIndex: Int
    )

    private companion object {
        const val MODIFIER_ARGUMENT = "modifier"
        const val COLORS_ARGUMENT = "colors"
        const val TEST_TAG_ARGUMENT = "testTag"
        const val TEXT_ARGUMENT = "text"
        const val COLOR_ARGUMENT = "color"
        const val CONTENT_COLOR_ARGUMENT = "contentColor"
        const val CONTAINER_COLOR_ARGUMENT = "containerColor"
        const val FONT_SIZE_ARGUMENT = "fontSize"
        const val STYLE_ARGUMENT = "style"
        const val FONT_WEIGHT_ARGUMENT = "fontWeight"
        const val FONT_STYLE_ARGUMENT = "fontStyle"
        const val FONT_FAMILY_ARGUMENT = "fontFamily"
        const val CONTENT_DESCRIPTION_ARGUMENT = "contentDescription"

        const val SIZE_MODIFIER = "size"
        const val WIDTH_MODIFIER = "width"
        const val HEIGHT_MODIFIER = "height"
        const val PADDING_MODIFIER = "padding"
        const val BACKGROUND_MODIFIER = "background"
        const val CLICKABLE_MODIFIER = "clickable"

        val composeFunctionNames = setOf(
            ComponentTypes.COMPOSE_TEXT,
            ComponentTypes.COMPOSE_BUTTON,
            ComponentTypes.COMPOSE_ICON_BUTTON,
            ComponentTypes.COMPOSE_OUTLINED_BUTTON,
            ComponentTypes.COMPOSE_TEXT_BUTTON,
            ComponentTypes.COMPOSE_FLOATING_ACTION_BUTTON,
            ComponentTypes.COMPOSE_IMAGE,
            ComponentTypes.COMPOSE_ICON,
            ComponentTypes.COMPOSE_COLUMN,
            ComponentTypes.COMPOSE_ROW,
            ComponentTypes.COMPOSE_BOX,
            ComponentTypes.COMPOSE_LAZY_COLUMN,
            ComponentTypes.COMPOSE_LAZY_ROW,
            ComponentTypes.COMPOSE_SURFACE,
            ComponentTypes.COMPOSE_CARD,
            ComponentTypes.COMPOSE_SPACER
        )

        val DIMENSION_PATTERN = Regex("""\d+(?:\.\d+)?\.(?:dp|sp)""")
        val STRING_LITERAL_PATTERN = Regex(""""([^"]*)"""")
    }
}
