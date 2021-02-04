package com.samruston.squigglyspans.spans

import android.graphics.*
import androidx.annotation.ColorInt
import com.samruston.squigglyspans.TWO_PI
import kotlin.math.ceil
import kotlin.math.sin

/**
 * A span that draws a squiggle under the text it is placed on.
 * See [DefaultSquigglySpan]
 */
abstract class SquigglySpan : MultilineSpan, AnimatingSpan {

    private val SEGMENTS_PER_PERIOD = 10
    private val path = Path()
    private var lastWavePeriod: Float? = null

    private val paint = Paint().apply {
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        isAntiAlias = true
    }

    @ColorInt
    abstract fun getColor(): Int
    abstract fun getLineWidth(): Float
    abstract fun getWavePeriod(): Float
    abstract fun getAmplitude(): Float
    abstract fun getPaddingLeft(): Float
    abstract fun getPaddingRight(): Float
    abstract fun getBaselineOffset(): Float
    abstract fun getWaveOffset(): Float

    private fun setupPaint() {
        with(paint) {
            color = this@SquigglySpan.getColor()
            strokeWidth = getLineWidth()

            val nextPeriod = getWavePeriod()

            if(lastWavePeriod != nextPeriod) {
                lastWavePeriod = nextPeriod
                pathEffect = CornerPathEffect(nextPeriod)
            }
        }
    }

    private fun calculateOffsetRadians(
        lineStart: Float,
        pointX: Float
    ): Float {
        val proportionOfPeriod = (pointX - lineStart)/getWavePeriod()
        return proportionOfPeriod * TWO_PI
    }

    override fun drawLine(canvas: Canvas, left: Int, top: Int, right: Int, bottom: Int) {

        // calculate bounds of path
        val lineStart = left + getPaddingLeft() + getLineWidth()/2f
        val lineEnd = right + getPaddingRight() - getLineWidth()/2f

        val baseline = bottom.toFloat() + getBaselineOffset()

        // we need a point at least every segmentWidth, this defines the point
        val segmentWidth = getWavePeriod()/SEGMENTS_PER_PERIOD
        val amountPoints = ceil((lineEnd-lineStart) / segmentWidth).toInt() + 1

        var pointX = lineStart

        setupPaint()
        path.reset()

        for(i in 0 until amountPoints) {

            val radiansX = calculateOffsetRadians(lineStart, pointX) + getWaveOffset()
            val offsetY = sin(radiansX) * getAmplitude()
            val y = baseline + offsetY

            if(i == 0) {
                path.moveTo(pointX, y)
            } else {
                path.lineTo(pointX, y)
            }

            pointX = pointX
                .plus(segmentWidth)
                .coerceAtMost(lineEnd)
        }

        canvas.drawPath(path,paint)
    }
}