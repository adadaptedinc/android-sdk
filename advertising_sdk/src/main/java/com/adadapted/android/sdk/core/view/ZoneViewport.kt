package com.adadapted.android.sdk.core.view

import android.graphics.Rect
import android.view.View
import android.view.ViewTreeObserver

//Position only, so a view collapsed to zero size still counts when it sits on screen
internal fun View.isPositionedInViewport(): Boolean {
    if (width > 0 && height > 0) return getGlobalVisibleRect(Rect())
    val sizedAncestor = generateSequence(parent as? View) { it.parent as? View }
        .firstOrNull { it.width > 0 && it.height > 0 } ?: return false
    val viewport = Rect()
    if (!sizedAncestor.getGlobalVisibleRect(viewport)) return false
    val location = IntArray(2).also { getLocationInWindow(it) }
    return spans(location[0], location[0] + width, viewport.left, viewport.right) &&
        spans(location[1], location[1] + height, viewport.top, viewport.bottom)
}

private fun spans(start: Int, end: Int, visibleStart: Int, visibleEnd: Int): Boolean {
    if (start >= visibleEnd) return false
    return if (start == end) start >= visibleStart else end > visibleStart
}

internal class ViewportWatcher {
    private var observer: ViewTreeObserver? = null
    private var listener: ViewTreeObserver.OnScrollChangedListener? = null

    fun watch(view: View, onMove: () -> Unit) {
        if (listener != null) return
        val scrollListener = ViewTreeObserver.OnScrollChangedListener { onMove() }
        observer = view.viewTreeObserver.also { it.addOnScrollChangedListener(scrollListener) }
        listener = scrollListener
    }

    fun stop() {
        val scrollListener = listener ?: return
        observer?.takeIf { it.isAlive }?.removeOnScrollChangedListener(scrollListener)
        observer = null
        listener = null
    }
}
