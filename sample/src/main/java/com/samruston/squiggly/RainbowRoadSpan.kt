package com.samruston.squiggly

import android.content.Context
import android.graphics.Color
import com.samruston.squigglyspans.spans.DefaultSquigglySpan

class RainbowRoadSpan(
    context: Context,
    private val hueOffset: Float,
): DefaultSquigglySpan(context) {

    private val colors = floatArrayOf(
        0f,
        1f,
        1f
    )

    override fun getColor(): Int {
        colors[0] = (getAnimationProgress()*360f + hueOffset) % 360f
        return Color.HSVToColor(colors)
    }
}