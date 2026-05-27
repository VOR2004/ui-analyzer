package ru.itis.compose.source.psi

import kotlin.math.max
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType
import ru.itis.analyzer.config.components.ComponentTypes

internal class ComposePsiCallExtractor {

    fun extract(file: KtFile): List<KtCallExpression> {
        return extractComposableFunctions(file)
            .flatMap { function -> rootCalls(extractComposeCalls(function)) }
    }

    fun extractComposableFunctions(file: KtFile): List<KtNamedFunction> {
        return file.collectDescendantsOfType<KtNamedFunction>()
            .filter { function -> function.isComposableFunction() }
    }

    fun namedFunctionContainers(file: KtFile): List<KtNamedFunction> {
        return file.collectDescendantsOfType<KtNamedFunction>()
            .filter { function -> !function.name.isNullOrBlank() }
    }

    fun extractComposeCalls(function: KtNamedFunction): List<KtCallExpression> {
        return function.collectDescendantsOfType<KtCallExpression>()
            .filter { call -> call.composeNameOrNull() != null }
    }

    fun rootCalls(calls: List<KtCallExpression>): List<KtCallExpression> {
        return calls.filter { call -> nearestComposeCallContainer(call, calls) == null }
    }

    fun directChildren(
        call: KtCallExpression,
        calls: List<KtCallExpression>
    ): List<KtCallExpression> {
        return calls.filter { child ->
            child != call && nearestComposeCallContainer(child, calls) == call
        }
    }

    fun nearestComposableFunctionName(call: KtCallExpression): String? {
        return call.nearestComposableFunction()?.name
    }

    fun nearestNamedFunctionName(call: KtCallExpression): String? {
        return call.nearestNamedFunction()?.name?.takeIf { name -> name.isNotBlank() }
    }

    fun KtCallExpression.composeNameOrNull(): String? {
        val name = when (val callee = calleeExpression) {
            is KtNameReferenceExpression -> callee.getReferencedName()
            else -> callee?.text
        }

        return name?.takeIf { value -> value in composeFunctionNames }
    }

    private fun PsiElement.isInsideComposableFunction(): Boolean {
        return nearestComposableFunction() != null
    }

    private fun PsiElement.nearestComposableFunction(): KtNamedFunction? {
        var current = parent
        while (current != null) {
            if (current is KtNamedFunction && current.isComposableFunction()) {
                return current
            }
            current = current.parent
        }
        return null
    }

    private fun PsiElement.nearestNamedFunction(): KtNamedFunction? {
        var current = parent
        while (current != null) {
            if (current is KtNamedFunction && !current.name.isNullOrBlank()) {
                return current
            }
            current = current.parent
        }
        return null
    }

    private fun KtNamedFunction.isComposableFunction(): Boolean {
        return annotationEntries.any { annotation ->
            annotation.shortName?.asString() == COMPOSABLE_ANNOTATION_NAME
        } || hasLeadingComposableAnnotation()
    }

    private fun KtNamedFunction.hasLeadingComposableAnnotation(): Boolean {
        val source = containingFile.text
        val functionStart = textRange.startOffset
        val leadingStart = max(0, functionStart - LEADING_ANNOTATION_LOOKBACK)
        val leadingBlock = source
            .substring(leadingStart, functionStart)
            .substringAfterLast(DOUBLE_LINE_BREAK)

        return COMPOSABLE_ANNOTATION_PATTERN.containsMatchIn(leadingBlock)
    }

    private fun PsiElement.nearestComposeCallAncestor(): KtCallExpression? {
        var current = parent
        while (current != null) {
            if (current is KtCallExpression && current.composeNameOrNull() != null) {
                return current
            }
            current = current.parent
        }
        return null
    }

    private fun nearestComposeCallContainer(
        call: KtCallExpression,
        calls: List<KtCallExpression>
    ): KtCallExpression? {
        return calls
            .asSequence()
            .filter { candidate -> candidate != call }
            .filter { candidate -> candidate.textRange.containsRange(call.textRange) }
            .minByOrNull { candidate -> candidate.textRange.length }
            ?: call.nearestComposeCallAncestor()
                ?.takeIf { ancestor -> ancestor in calls }
    }

    private fun org.jetbrains.kotlin.com.intellij.openapi.util.TextRange.containsRange(
        other: org.jetbrains.kotlin.com.intellij.openapi.util.TextRange
    ): Boolean {
        return startOffset <= other.startOffset && endOffset >= other.endOffset
    }

    private companion object {
        const val COMPOSABLE_ANNOTATION_NAME = "Composable"
        const val LEADING_ANNOTATION_LOOKBACK = 500
        const val DOUBLE_LINE_BREAK = "\n\n"
        val COMPOSABLE_ANNOTATION_PATTERN = Regex("""@\s*(?:[\w.]+\.)?Composable\b""")

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
    }
}
