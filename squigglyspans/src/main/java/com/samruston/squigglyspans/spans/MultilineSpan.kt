package com.samruston.squigglyspans.spans

import android.graphics.Canvas

/**
 * Used by [SquigglyTextView] to allow drawing squiggles across multiple lines. The parameters are the
 * bounds of the text that it is attached to.
 */
internal interface MultilineSpan {
    fun drawLine(
        canvas: Canvas,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int
    )
}