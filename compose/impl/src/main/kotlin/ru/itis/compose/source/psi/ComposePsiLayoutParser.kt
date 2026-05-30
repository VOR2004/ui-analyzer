package ru.itis.compose.source.psi

import java.io.File
import ru.itis.analyzer.config.components.ComponentTypes
import ru.itis.analyzer.utils.SourceTextUtils
import ru.itis.model.SourceType
import ru.itis.model.UiComponent
import ru.itis.model.UiProperties
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtNamedFunction

class ComposePsiLayoutParser {
    private val callExtractor = ComposePsiCallExtractor()
    private val argumentExtractor = ComposePsiArgumentExtractor()
    private val localValueResolver = ComposePsiLocalValueResolver()

    fun parse(file: File): List<UiComponent> {
        return runCatching {
            val source = file.readText()
            val ktFile = ComposePsiEnvironment.createKtFile(
                fileName = file.name,
                source = source
            )
            val namedFunctionContainers = callExtractor.namedFunctionContainers(ktFile)
            val sourceFunctionContainers = findSourceFunctionContainers(source)

            callExtractor.extractComposableFunctions(ktFile).flatMap { function ->
                val functionName = function.name.orEmpty()
                val calls = callExtractor.extractComposeCalls(function)
                callExtractor.rootCalls(calls).mapIndexed { index, call ->
                    buildComponent(
                        call = call,
                        filePath = file.absolutePath,
                        parentPath = functionName.takeIf { name -> name.isNotBlank() }?.let { name -> "/$name" }.orEmpty(),
                        siblingIndex = index,
                        composeFunctionName = functionName.takeIf { name -> name.isNotBlank() },
                        composeCalls = calls,
                        namedFunctionContainers = namedFunctionContainers,
                        sourceFunctionContainers = sourceFunctionContainers
                    )
                }
            }
        }.getOrDefault(emptyList())
    }

    private fun buildComponent(
        call: KtCallExpression,
        filePath: String,
        parentPath: String,
        siblingIndex: Int,
        composeFunctionName: String?,
        composeCalls: List<KtCallExpression>,
        namedFunctionContainers: List<KtNamedFunction>,
        sourceFunctionContainers: List<SourceFunctionContainer>
    ): UiComponent {
        val type = requireNotNull(callExtractor.run { call.composeNameOrNull() })
        val effectiveComposeFunctionName = composeFunctionName
            ?: nearestSourceFunctionContainer(call, sourceFunctionContainers)?.name
            ?: callExtractor.nearestNamedFunctionName(call)
            ?: nearestNamedFunctionContainer(call, namedFunctionContainers)?.name
        val effectiveParentPath = if (parentPath.isBlank() && effectiveComposeFunctionName != null) {
            "/$effectiveComposeFunctionName"
        } else {
            parentPath
        }
        val rawArguments = argumentExtractor.extract(call)
        val resolvedArguments = rawArguments.mapValues { (_, value) ->
            localValueResolver.resolveSimpleValue(call, value) ?: value
        }
        val treePath = "$effectiveParentPath/$type[$siblingIndex]"

        val children = callExtractor.directChildren(call, composeCalls).mapIndexed { index, child ->
            buildComponent(
                call = child,
                filePath = filePath,
                parentPath = treePath,
                siblingIndex = index,
                composeFunctionName = effectiveComposeFunctionName,
                composeCalls = composeCalls,
                namedFunctionContainers = namedFunctionContainers,
                sourceFunctionContainers = sourceFunctionContainers
            )
        }

        return UiComponent(
            id = resolvedArguments[TEST_TAG_ARGUMENT]?.trimStringLiteralIfNeeded(),
            type = type,
            sourceType = SourceType.COMPOSE,
            filePath = filePath,
            treePath = treePath,
            properties = buildProperties(type, resolvedArguments, effectiveComposeFunctionName),
            children = children
        )
    }

    private fun nearestNamedFunctionContainer(
        call: KtCallExpression,
        namedFunctionContainers: List<KtNamedFunction>
    ): KtNamedFunction? {
        return namedFunctionContainers
            .asSequence()
            .filter { function ->
                function.textRange.startOffset <= call.textRange.startOffset &&
                    function.textRange.endOffset >= call.textRange.endOffset
            }
            .minByOrNull { function -> function.textRange.length }
    }

    private fun nearestSourceFunctionContainer(
        call: KtCallExpression,
        sourceFunctionContainers: List<SourceFunctionContainer>
    ): SourceFunctionContainer? {
        return sourceFunctionContainers
            .asSequence()
            .filter { function ->
                function.startOffset <= call.textRange.startOffset &&
                    function.endOffset >= call.textRange.endOffset
            }
            .minByOrNull { function -> function.endOffset - function.startOffset }
    }

    private fun findSourceFunctionContainers(source: String): List<SourceFunctionContainer> {
        return FUNCTION_DECLARATION_PATTERN.findAll(source)
            .mapNotNull { match ->
                val openBrace = source.indexOf('{', startIndex = match.range.last + 1)
                    .takeIf { index -> index >= 0 }
                    ?: return@mapNotNull null
                val closeBrace = SourceTextUtils.findMatchingDelimiter(source, openBrace, '{', '}')
                    ?: return@mapNotNull null

                SourceFunctionContainer(
                    name = match.groupValues[1],
                    startOffset = match.range.first,
                    endOffset = closeBrace
                )
            }
            .toList()
    }

    private fun buildProperties(
        type: String,
        rawArguments: Map<String, String>,
        composeFunctionName: String?
    ): UiProperties {
        val modifier = rawArguments[MODIFIER_ARGUMENT].orEmpty()
        val colors = rawArguments[COLORS_ARGUMENT].orEmpty()
        val fontWeight = rawArguments[FONT_WEIGHT_ARGUMENT]
        val fontStyle = rawArguments[FONT_STYLE_ARGUMENT]

        return UiProperties(
            width = extractModifierDimension(modifier, WIDTH_MODIFIER)
                ?: extractModifierDimension(modifier, SIZE_MODIFIER),
            height = extractModifierDimension(modifier, HEIGHT_MODIFIER)
                ?: extractModifierDimension(modifier, SIZE_MODIFIER),
            padding = extractModifierDimension(modifier, PADDING_MODIFIER),
            backgroundColor = extractModifierValue(modifier, BACKGROUND_MODIFIER)
                ?: rawArguments[CONTAINER_COLOR_ARGUMENT]
                ?: extractNamedArgumentValue(colors, CONTAINER_COLOR_ARGUMENT),
            textColor = rawArguments[COLOR_ARGUMENT]
                ?: rawArguments[CONTENT_COLOR_ARGUMENT]
                ?: extractNamedArgumentValue(colors, CONTENT_COLOR_ARGUMENT),
            textSize = rawArguments[FONT_SIZE_ARGUMENT],
            typographyStyle = rawArguments[STYLE_ARGUMENT],
            fontFamily = rawArguments[FONT_FAMILY_ARGUMENT],
            textStyle = listOfNotNull(fontWeight, fontStyle).joinToString(" ").ifBlank { null },
            text = extractComposeText(type, rawArguments),
            contentDescription = rawArguments[CONTENT_DESCRIPTION_ARGUMENT]?.trimStringLiteralIfNeeded(),
            isClickable = modifier.containsModifier(CLICKABLE_MODIFIER),
            rawAttributes = buildRawAttributes(type, rawArguments, composeFunctionName)
        )
    }

    private fun buildRawAttributes(
        type: String,
        rawArguments: Map<String, String>,
        composeFunctionName: String?
    ): Map<String, String> {
        val attributes = rawArguments
            .filterKeys { key -> !key.startsWith(POSITIONAL_ARGUMENT_PREFIX) }
            .toMutableMap()

        if (composeFunctionName != null) {
            attributes[COMPOSE_FUNCTION_ATTRIBUTE] = composeFunctionName
        }

        if (type == ComponentTypes.COMPOSE_ICON || type == ComponentTypes.COMPOSE_IMAGE) {
            val firstArgument = argumentExtractor.firstPositionalArgument(rawArguments)
            if (firstArgument != null &&
                IMAGE_VECTOR_ARGUMENT !in attributes &&
                PAINTER_ARGUMENT !in attributes
            ) {
                attributes[VISUAL_SOURCE_ARGUMENT] = firstArgument
            }
        }

        return attributes
    }

    private fun extractNamedArgumentValue(arguments: String, name: String): String? {
        val openParen = arguments.indexOf('(')
        val closeParen = if (openParen >= 0) {
            SourceTextUtils.findMatchingDelimiter(arguments, openParen, '(', ')')
        } else {
            null
        }
        val normalizedArguments = if (openParen >= 0 && closeParen != null) {
            arguments.substring(openParen + 1, closeParen)
        } else {
            arguments
        }

        return parseNamedArguments(normalizedArguments)[name]
    }

    private fun extractComposeText(
        type: String,
        rawArguments: Map<String, String>
    ): String? {
        if (type != ComponentTypes.COMPOSE_TEXT && type != ComponentTypes.COMPOSE_BUTTON) {
            return null
        }

        rawArguments[TEXT_ARGUMENT]
            ?.takeIf { value -> value.isStringLiteral() }
            ?.let { value -> return value.trimStringLiteral() }

        val firstArgument = argumentExtractor.firstPositionalArgument(rawArguments) ?: return null
        return firstArgument
            .takeIf { value -> value.isStringLiteral() }
            ?.trimStringLiteral()
    }

    private fun extractModifierDimension(modifier: String, modifierName: String): String? {
        return extractModifierValue(modifier, modifierName)
            ?.let { value -> DIMENSION_PATTERN.find(value)?.value }
    }

    private fun extractModifierValue(modifier: String, modifierName: String): String? {
        val start = modifier.indexOf(".$modifierName(")
        if (start < 0) return null

        val openParen = modifier.indexOf('(', start)
        val closeParen = SourceTextUtils.findMatchingDelimiter(modifier, openParen, '(', ')') ?: return null
        return modifier.substring(openParen + 1, closeParen).trim()
    }

    private fun parseNamedArguments(arguments: String): Map<String, String> {
        return splitTopLevelArguments(arguments)
            .mapNotNull { argument ->
                val equalsIndex = indexOfTopLevelEquals(argument)
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

    private fun splitTopLevelArguments(value: String): List<String> {
        val result = mutableListOf<String>()
        var start = 0
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
                !inString && char == ARGUMENT_SEPARATOR && parenDepth == 0 && braceDepth == 0 -> {
                    result += value.substring(start, index).trim()
                    start = index + 1
                }
            }
        }

        result += value.substring(start).trim()
        return result.filter { item -> item.isNotBlank() }
    }

    private fun indexOfTopLevelEquals(value: String): Int? {
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
                !inString && char == NAMED_ARGUMENT_SEPARATOR && parenDepth == 0 && braceDepth == 0 -> return index
            }
        }

        return null
    }

    private fun String.containsModifier(modifierName: String): Boolean {
        return contains(".$modifierName(") || contains(".$modifierName {")
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
        const val IMAGE_VECTOR_ARGUMENT = "imageVector"
        const val PAINTER_ARGUMENT = "painter"
        const val VISUAL_SOURCE_ARGUMENT = "visualSource"
        const val COMPOSE_FUNCTION_ATTRIBUTE = "compose:function"
        const val POSITIONAL_ARGUMENT_PREFIX = "__positional"

        const val SIZE_MODIFIER = "size"
        const val WIDTH_MODIFIER = "width"
        const val HEIGHT_MODIFIER = "height"
        const val PADDING_MODIFIER = "padding"
        const val BACKGROUND_MODIFIER = "background"
        const val CLICKABLE_MODIFIER = "clickable"
        const val ARGUMENT_SEPARATOR = ','
        const val NAMED_ARGUMENT_SEPARATOR = '='

        val DIMENSION_PATTERN = Regex("""\d+(?:\.\d+)?\.(?:dp|sp)""")
        val FUNCTION_DECLARATION_PATTERN = Regex("""\bfun\s+(?:[A-Za-z_][\w]*\.)*([A-Za-z_][\w]*)\s*\(""")
    }

    private data class SourceFunctionContainer(
        val name: String,
        val startOffset: Int,
        val endOffset: Int
    )
}
