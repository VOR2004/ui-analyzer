package ru.itis.desktop.ui.window

import java.awt.Cursor
import java.awt.Dimension
import java.awt.Point
import java.awt.Rectangle
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent

internal fun installWindowResizeSupport(
    window: java.awt.Window,
    borderSizePx: Int = 7
): () -> Unit {
    val listener = WindowResizeMouseAdapter(window, borderSizePx)
    window.addMouseListener(listener)
    window.addMouseMotionListener(listener)

    return {
        window.removeMouseListener(listener)
        window.removeMouseMotionListener(listener)
    }
}

private class WindowResizeMouseAdapter(
    private val window: java.awt.Window,
    private val borderSizePx: Int
) : MouseAdapter() {

    private var activeEdge: ResizeEdge = ResizeEdge.NONE
    private var startPointer: Point? = null
    private var startBounds: Rectangle? = null

    override fun mouseMoved(event: MouseEvent) {
        window.cursor = Cursor.getPredefinedCursor(edgeAt(event).cursor)
    }

    override fun mouseExited(event: MouseEvent) {
        if (activeEdge == ResizeEdge.NONE) {
            window.cursor = Cursor.getDefaultCursor()
        }
    }

    override fun mousePressed(event: MouseEvent) {
        activeEdge = edgeAt(event)
        if (activeEdge == ResizeEdge.NONE) return

        startPointer = event.locationOnScreen
        startBounds = window.bounds
    }

    override fun mouseReleased(event: MouseEvent) {
        activeEdge = ResizeEdge.NONE
        startPointer = null
        startBounds = null
    }

    override fun mouseDragged(event: MouseEvent) {
        val edge = activeEdge
        val pointer = startPointer
        val bounds = startBounds
        if (edge == ResizeEdge.NONE || pointer == null || bounds == null) return

        val deltaX = event.locationOnScreen.x - pointer.x
        val deltaY = event.locationOnScreen.y - pointer.y
        val minimumSize = window.minimumSize ?: Dimension(0, 0)

        var x = bounds.x
        var y = bounds.y
        var width = bounds.width
        var height = bounds.height

        if (edge.left) {
            x = bounds.x + deltaX
            width = bounds.width - deltaX
            if (width < minimumSize.width) {
                width = minimumSize.width
                x = bounds.x + bounds.width - minimumSize.width
            }
        }
        if (edge.right) {
            width = (bounds.width + deltaX).coerceAtLeast(minimumSize.width)
        }
        if (edge.top) {
            y = bounds.y + deltaY
            height = bounds.height - deltaY
            if (height < minimumSize.height) {
                height = minimumSize.height
                y = bounds.y + bounds.height - minimumSize.height
            }
        }
        if (edge.bottom) {
            height = (bounds.height + deltaY).coerceAtLeast(minimumSize.height)
        }

        window.setBounds(x, y, width, height)
    }

    private fun edgeAt(event: MouseEvent): ResizeEdge {
        val left = event.x <= borderSizePx
        val right = event.x >= window.width - borderSizePx
        val top = event.y <= borderSizePx
        val bottom = event.y >= window.height - borderSizePx

        return when {
            top && left -> ResizeEdge.TOP_LEFT
            top && right -> ResizeEdge.TOP_RIGHT
            bottom && left -> ResizeEdge.BOTTOM_LEFT
            bottom && right -> ResizeEdge.BOTTOM_RIGHT
            top -> ResizeEdge.TOP
            bottom -> ResizeEdge.BOTTOM
            left -> ResizeEdge.LEFT
            right -> ResizeEdge.RIGHT
            else -> ResizeEdge.NONE
        }
    }
}

private enum class ResizeEdge(
    val cursor: Int,
    val left: Boolean = false,
    val right: Boolean = false,
    val top: Boolean = false,
    val bottom: Boolean = false
) {
    NONE(Cursor.DEFAULT_CURSOR),
    LEFT(Cursor.W_RESIZE_CURSOR, left = true),
    RIGHT(Cursor.E_RESIZE_CURSOR, right = true),
    TOP(Cursor.N_RESIZE_CURSOR, top = true),
    BOTTOM(Cursor.S_RESIZE_CURSOR, bottom = true),
    TOP_LEFT(Cursor.NW_RESIZE_CURSOR, left = true, top = true),
    TOP_RIGHT(Cursor.NE_RESIZE_CURSOR, right = true, top = true),
    BOTTOM_LEFT(Cursor.SW_RESIZE_CURSOR, left = true, bottom = true),
    BOTTOM_RIGHT(Cursor.SE_RESIZE_CURSOR, right = true, bottom = true)
}
