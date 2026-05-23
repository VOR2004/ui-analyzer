package ru.itis.compose.source.parser

import java.io.File
import ru.itis.analyzer.utils.SourceTextUtils
import ru.itis.compose.source.model.ComposeFunction
import ru.itis.compose.source.model.ComposeFunctionConstants

class ComposeFunctionParser {

    fun parse(file: File): List<ComposeFunction> {
        val source = file.readText()
        val functions = mutableListOf<ComposeFunction>()
        var index = 0

        while (index < source.length) {
            val annotationIndex = source.indexOf(ComposeFunctionConstants.COMPOSABLE_ANNOTATION, index)
            if (annotationIndex < 0) break

            val function = parseFunctionAfterAnnotation(source, annotationIndex, file.absolutePath)
            if (function == null) {
                index = annotationIndex + ComposeFunctionConstants.COMPOSABLE_ANNOTATION.length
            } else {
                functions += function.value
                index = function.nextIndex
            }
        }

        return functions
    }

    private fun parseFunctionAfterAnnotation(
        source: String,
        annotationIndex: Int,
        filePath: String
    ): ParsedFunction? {
        val funIndex = source.indexOf(
            FUN_KEYWORD,
            annotationIndex + ComposeFunctionConstants.COMPOSABLE_ANNOTATION.length
        )
        if (funIndex < 0) return null
        val modifiers = parseFunctionModifiers(source, annotationIndex, funIndex)

        val nameStart = skipWhitespace(source, funIndex + FUN_KEYWORD.length)
        if (nameStart >= source.length || !source[nameStart].isIdentifierStart()) return null

        var nameEnd = nameStart + 1
        while (nameEnd < source.length && source[nameEnd].isIdentifierPart()) {
            nameEnd++
        }

        val name = source.substring(nameStart, nameEnd)
        val openParen = skipWhitespace(source, nameEnd).takeIf { index ->
            index < source.length && source[index] == '('
        } ?: return null

        val closeParen = SourceTextUtils.findMatchingDelimiter(source, openParen, '(', ')') ?: return null
        val parameters = source.substring(openParen + 1, closeParen)
        val bodyStart = skipWhitespace(source, closeParen + 1)
        val body = if (bodyStart < source.length && source[bodyStart] == '{') {
            val bodyEnd = SourceTextUtils.findMatchingDelimiter(source, bodyStart, '{', '}')
            if (bodyEnd != null) source.substring(bodyStart + 1, bodyEnd) else ""
        } else {
            ""
        }

        return ParsedFunction(
            value = ComposeFunction(
                name = name,
                parameters = parseParameters(parameters),
                filePath = filePath,
                modifiers = modifiers,
                body = body
            ),
            nextIndex = closeParen + 1
        )
    }

    private fun parseFunctionModifiers(
        source: String,
        annotationIndex: Int,
        funIndex: Int
    ): Set<String> {
        val beforeAnnotation = source.substring(findLineStart(source, annotationIndex), annotationIndex)
        val betweenAnnotationAndFun = source.substring(
            annotationIndex + ComposeFunctionConstants.COMPOSABLE_ANNOTATION.length,
            funIndex
        )

        return ("$beforeAnnotation $betweenAnnotationAndFun")
            .split(Regex("""\s+"""))
            .map { token -> token.trim() }
            .filter { token -> token in knownFunctionModifiers }
            .toSet()
    }

    private fun findLineStart(source: String, index: Int): Int {
        val lineStart = source.lastIndexOf('\n', startIndex = index)
        return if (lineStart < 0) 0 else lineStart + 1
    }

    private fun parseParameters(rawParameters: String): List<String> {
        val result = mutableListOf<String>()
        var start = 0
        var parenDepth = 0
        var angleDepth = 0
        var inString = false

        for (index in rawParameters.indices) {
            val char = rawParameters[index]
            when {
                char == '"' -> inString = !inString
                !inString && char == '(' -> parenDepth++
                !inString && char == ')' -> parenDepth--
                !inString && char == '<' -> angleDepth++
                !inString && char == '>' && angleDepth > 0 -> angleDepth--
                !inString && char == ',' && parenDepth == 0 && angleDepth == 0 -> {
                    result += rawParameters.substring(start, index).trim()
                    start = index + 1
                }
            }
        }

        result += rawParameters.substring(start).trim()
        return result.filter { it.isNotBlank() }
    }

    private fun skipWhitespace(source: String, startIndex: Int): Int {
        var index = startIndex
        while (index < source.length && source[index].isWhitespace()) {
            index++
        }
        return index
    }

    private fun Char.isIdentifierStart(): Boolean {
        return isLetter() || this == '_'
    }

    private fun Char.isIdentifierPart(): Boolean {
        return isLetterOrDigit() || this == '_'
    }

    private data class ParsedFunction(
        val value: ComposeFunction,
        val nextIndex: Int
    )

    private companion object {
        const val FUN_KEYWORD = "fun"

        val knownFunctionModifiers = setOf(
            ComposeFunctionConstants.PRIVATE_MODIFIER,
            "internal",
            "public"
        )
    }
}
