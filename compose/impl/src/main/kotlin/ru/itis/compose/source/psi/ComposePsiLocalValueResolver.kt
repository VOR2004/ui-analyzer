package ru.itis.compose.source.psi

import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtNamedDeclaration
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.psiUtil.collectDescendantsOfType

internal class ComposePsiLocalValueResolver {

    fun resolveSimpleValue(call: KtCallExpression, expression: String?): String? {
        val raw = expression?.trim() ?: return null
        if (!raw.isSimpleIdentifier()) return raw

        return findVisibleLocalValues(call)[raw] ?: raw
    }

    private fun findVisibleLocalValues(call: KtCallExpression): Map<String, String> {
        val scope = call.containingDeclarationOrFile()
        return scope.collectDescendantsOfType<KtProperty>()
            .filter { property -> property.textRange.startOffset < call.textRange.startOffset }
            .mapNotNull { property ->
                val name = property.name ?: return@mapNotNull null
                val initializer = property.initializer?.text?.trim() ?: return@mapNotNull null
                name to initializer
            }
            .toMap()
    }

    private fun KtCallExpression.containingDeclarationOrFile(): PsiElement {
        var current: PsiElement = this
        while (current.parent != null) {
            val parent = current.parent
            if (parent is KtNamedDeclaration) {
                return parent
            }
            current = parent
        }
        return containingFile
    }

    private fun String.isSimpleIdentifier(): Boolean {
        return matches(Regex("""[A-Za-z_][A-Za-z0-9_]*"""))
    }
}
