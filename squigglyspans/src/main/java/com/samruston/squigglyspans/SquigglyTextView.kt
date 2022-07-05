package com.samruston.squigglyspans

import android.content.Context
import android.graphics.Canvas
import android.text.Layout
import android.text.Spanned
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.graphics.withTranslation
import com.samruston.squigglyspans.spans.AnimatingSpan
import com.samruston.squigglyspans.spans.MultilineSpan

/**
 * A [TextView] that redraws every frame whenever there is an [AnimatingSpan] in its text.
 * It draws the spans itself on top the text, mostly taken from
 * https://github.com/android/user-interface-samples/blob/081f33f53e/TextRoundedBackgroundKotlin
 */
open class SquigglyTextView : AppCompatTextView, Runnable {

    private val DEFAULT_LINESPACING_EXTRA = 0f
    private val DEFAULT_LINESPACING_MULTIPLIER = 1f
    private var hasAnimatingSpans = false
    
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    
    override fun onTextChanged(
        text: CharSequence?,
        start: Int,
        lengthBefore: Int,
        lengthAfter: Int
    ) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
        hasAnimatingSpans = checkAnimatingSpans()
        post(this)
    }

    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        super.onWindowFocusChanged(hasWindowFocus)
        post(this)
    }

    private fun checkAnimatingSpans(): Boolean {
        val spannable = text as? Spanned ?: return false
        val spans = spannable.getSpans(0, spannable.length, AnimatingSpan::class.java)
        return spans.isNotEmpty()
    }

    override fun run() {
        if(!hasAnimatingSpans) return
        if(!hasWindowFocus()) return
        postInvalidateOnAnimation()
        postOnAnimation(this)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (text is Spanned && layout != null) {
            canvas.withTranslation(totalPaddingLeft.toFloat(), totalPaddingTop.toFloat()) {
                drawSpans(canvas, text as Spanned, layout)
            }
        }
    }

    private fun drawSpans(canvas: Canvas, text: Spanned, layout: Layout) {
        val spans = text.getSpans(0, text.length, MultilineSpan::class.java)
        spans.forEach { span ->
            val spanStart = text.getSpanStart(span)
            val spanEnd = text.getSpanEnd(span)
            val startLine = layout.getLineForOffset(spanStart)
            val endLine = layout.getLineForOffset(spanEnd)

            // start can be on the left or on the right depending on the language direction.
            val startOffset = layout.getPrimaryHorizontal(spanStart).toInt()
            // end can be on the left or on the right depending on the language direction.
            val endOffset = layout.getPrimaryHorizontal(spanEnd).toInt()

            render(span, canvas, layout, startLine, endLine, startOffset, endOffset)
        }
    }

    private fun getLineTop(layout: Layout, line: Int): Int {
        return layout.getLineTopWithoutPadding(line)
    }

    private fun getLineBottom(layout: Layout, line: Int): Int {
        return layout.getLineBottomWithoutPadding(line)
    }

    private fun Layout.getLineHeight(line: Int): Int {
        return getLineTop(line + 1) - getLineTop(line)
    }

    private fun Layout.getLineBottomWithoutPadding(line: Int): Int {
        var lineBottom = getLineBottomWithoutSpacing(line)
        if (line == lineCount - 1) {
            lineBottom -= bottomPadding
        }
        return lineBottom
    }

    private fun Layout.getLineTopWithoutPadding(line: Int): Int {
        var lineTop = getLineTop(line)
        if (line == 0) {
            lineTop -= topPadding
        }
        return lineTop
    }

    private fun Layout.getLineBottomWithoutSpacing(line: Int): Int {
        val lineBottom = getLineBottom(line)
        val isLastLine = line == lineCount - 1

        val lineBottomWithoutSpacing: Int
        val lineSpacingExtra = spacingAdd
        val lineSpacingMultiplier = spacingMultiplier
        val hasLineSpacing = lineSpacingExtra != DEFAULT_LINESPACING_EXTRA
                || lineSpacingMultiplier != DEFAULT_LINESPACING_MULTIPLIER

        if (!hasLineSpacing || isLastLine) {
            lineBottomWithoutSpacing = lineBottom
        } else {
            val extra: Float
            if (lineSpacingMultiplier.compareTo(DEFAULT_LINESPACING_MULTIPLIER) != 0) {
                val lineHeight = getLineHeight(line)
                extra = lineHeight - (lineHeight - lineSpacingExtra) / lineSpacingMultiplier
            } else {
                extra = lineSpacingExtra
            }

            lineBottomWithoutSpacing = (lineBottom - extra).toInt()
        }

        return lineBottomWithoutSpacing
    }

    private fun render(
        span: MultilineSpan,
        canvas: Canvas,
        layout: Layout,
        startLine: Int,
        endLine: Int,
        startOffset: Int,
        endOffset: Int
    ) {

        if(startLine == endLine) {
            val lineTop = getLineTop(layout, startLine)
            val lineBottom = getLineBottom(layout, startLine)
            val left = kotlin.math.min(startOffset, endOffset)
            val right = kotlin.math.max(startOffset, endOffset)
            drawLine(span, canvas, left, lineTop, right, lineBottom)
            return
        }

        // draw the first line
        val paragDir = layout.getParagraphDirection(startLine)
        val lineEndOffset = if (paragDir == Layout.DIR_RIGHT_TO_LEFT) {
            layout.getLineLeft(startLine)
        } else {
            layout.getLineRight(startLine)
        }.toInt()

        var lineBottom = getLineBottom(layout, startLine)
        var lineTop = getLineTop(layout, startLine)

        drawLine(span, canvas, startOffset, lineTop, lineEndOffset, lineBottom)

        // for the lines in the middle draw the mid drawable
        for (line in startLine + 1 until endLine) {
            lineTop = getLineTop(layout, line)
            lineBottom = getLineBottom(layout, line)

            drawLine(
                span,
                canvas,
                (layout.getLineLeft(line).toInt()),
                lineTop,
                (layout.getLineRight(line).toInt()),
                lineBottom
            )
        }

        val lineStartOffset = if (paragDir == Layout.DIR_RIGHT_TO_LEFT) {
            layout.getLineRight(startLine)
        } else {
            layout.getLineLeft(startLine)
        }.toInt()

        // draw the last line
        lineBottom = getLineBottom(layout, endLine)
        lineTop = getLineTop(layout, endLine)

        drawLine(span, canvas, lineStartOffset, lineTop, endOffset, lineBottom)
    }

    private fun drawLine(span: MultilineSpan, canvas: Canvas, start: Int, top: Int, end: Int, bottom: Int) {
        if (start > end) {
            span.drawLine(canvas, end, top, start, bottom)
        } else {
            span.drawLine(canvas, start, top, end, bottom)
        }
    }
}
