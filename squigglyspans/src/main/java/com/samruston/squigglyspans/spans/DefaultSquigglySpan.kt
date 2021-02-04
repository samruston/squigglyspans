package com.samruston.squigglyspans.spans

import android.content.Context
import android.graphics.Color
import androidx.annotation.ColorInt
import com.samruston.squigglyspans.TWO_PI
import com.samruston.squigglyspans.dp

/**
 * The [SquigglySpan] implementation you'll want to use.
 */
open class DefaultSquigglySpan constructor(
    context: Context
): SquigglySpan() {

    private var color: Int = Color.RED
    private var lineWidthPx: Float = context.dp(4)
    private var wavePeriodPx: Float = context.dp(48)
    private var amplitudePx: Float = context.dp(4)
    private var leftPadding: Float = 0f
    private var rightPadding: Float = 0f
    private var baselineOffsetPx: Float = 0f
    private var duration: Int = 1_000
    private var defaultWaveOffsetRadians: Float = 0f

    /**
     * Set the color of the squiggle.
     */
    fun setColor(@ColorInt color: Int) {
        this.color = color
    }

    /**
     * Set the stroke width of the squiggle.
     */
    fun setLineWidth(width: Float) {
        this.lineWidthPx = width
    }

    /**
     * Set the period of the wave in pixels. This is the horizontal distance
     * of one complete cycle.
     */
    fun setWavePeriod(period: Float) {
        this.wavePeriodPx = period
    }

    /**
     * Set the maximum amplitude of the wave in pixels. The wave will be at most this many pixels
     * above the baseline and this many pixels below the baseline. So the height of the wave will
     * be 2x this value.
     */
    fun setAmplitude(amplitude: Float) {
        this.amplitudePx = amplitude
    }

    /**
     * The padding (in px) before the squiggle starts on the text it is applied to.
     */
    fun setPaddingLeft(padding: Float) {
        this.leftPadding = padding
    }

    /**
     * The padding (in px) after the squiggle ends on the text it is applied to.
     */
    fun setPaddingRight(padding: Float) {
        this.rightPadding = padding
    }

    /**
     * The vertical offset from the baseline in px. A positive number will move the squiggle lower.
     */
    fun setBaselineOffset(offset: Float) {
        this.baselineOffsetPx = offset
    }

    /**
     * The time taken to complete one full cycle in milliseconds.
     */
    fun setDuration(duration: Int) {
        this.duration = duration
    }

    /**
     * How much the wave is shifted in radians, for example set to Math.PI to shift by half a cycle.
     * This is really only useful if you're drawing multiple squiggles on the same text and want
     * to offset one of them.
     */
    fun setDefaultWaveOffset(offset: Float) {
        this.defaultWaveOffsetRadians = offset
    }

    override fun getColor(): Int {
        return color
    }

    override fun getLineWidth(): Float {
        return lineWidthPx
    }

    override fun getWavePeriod(): Float {
        return wavePeriodPx
    }

    override fun getAmplitude(): Float {
        return amplitudePx
    }

    override fun getPaddingLeft(): Float {
        return leftPadding
    }

    override fun getPaddingRight(): Float {
        return rightPadding
    }

    override fun getBaselineOffset(): Float {
        return baselineOffsetPx
    }

    fun getDuration(): Int {
        return duration
    }

    fun getDefaultWaveOffset(): Float {
        return defaultWaveOffsetRadians
    }

    fun getAnimationProgress(): Float {
        return (System.currentTimeMillis() % duration)/duration.toFloat()
    }

    override fun getWaveOffset(): Float {
        val animationProgress = getAnimationProgress()
        val timeOffsetRadians = TWO_PI * animationProgress
        return timeOffsetRadians + defaultWaveOffsetRadians
    }
}