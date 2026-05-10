package ru.itis.compose.source.psi

import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType
import ru.itis.analyzer.config.ComponentTypes

internal class ComposePsiCallExtractor {

    fun extract(file: KtFile): List<KtCallExpression> {
        return file.collectDescendantsOfType<KtCallExpression>()
            .filter { call -> call.composeNameOrNull() != null }
            .filter { call -> call.isInsideComposableFunction() }
            .filter { call -> call.nearestComposeCallAncestor() == null }
    }

    fun extractDirectChildren(call: KtCallExpression): List<KtCallExpression> {
        val lambdaBodies = call.lambdaArguments.mapNotNull { argument ->
            argument.getLambdaExpression()?.bodyExpression
        }

        return lambdaBodies.flatMap { body ->
            body.collectDescendantsOfType<KtCallExpression>()
                .filter { child -> child.composeNameOrNull() != null }
                .filter { child -> child.isInsideComposableFunction() }
                .filter { child -> child.nearestComposeCallAncestor() == call }
        }
    }

    fun KtCallExpression.composeNameOrNull(): String? {
        val name = when (val callee = calleeExpression) {
            is KtNameReferenceExpression -> callee.getReferencedName()
            else -> callee?.text
        }

        return name?.takeIf { value -> value in composeFunctionNames }
    }

    private fun PsiElement.isInsideComposableFunction(): Boolean {
        var current = parent
        while (current != null) {
            if (current is KtNamedFunction) {
                return current.annotationEntries.any { annotation ->
                    annotation.shortName?.asString() == COMPOSABLE_ANNOTATION_NAME
                }
            }
            current = current.parent
        }
        return false
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

    private companion object {
        const val COMPOSABLE_ANNOTATION_NAME = "Composable"

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
