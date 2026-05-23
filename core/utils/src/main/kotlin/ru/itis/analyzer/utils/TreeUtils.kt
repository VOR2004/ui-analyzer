package ru.itis.analyzer.utils

import ru.itis.model.TreeNodeContext
import ru.itis.model.UiComponent

object TreeUtils {

    fun traverse(
        roots: List<UiComponent>,
        action: (TreeNodeContext) -> Unit
    ) {
        roots.forEach { root ->
            traverseNode(
                component = root,
                parent = null,
                depth = 0,
                path = listOf(root),
                action = action
            )
        }
    }

    private fun traverseNode(
        component: UiComponent,
        parent: UiComponent?,
        depth: Int,
        path: List<UiComponent>,
        action: (TreeNodeContext) -> Unit
    ) {
        action(
            TreeNodeContext(
                component = component,
                parent = parent,
                depth = depth,
                path = path
            )
        )

        component.children.forEach { child ->
            traverseNode(
                component = child,
                parent = component,
                depth = depth + 1,
                path = path + child,
                action = action
            )
        }
    }
}