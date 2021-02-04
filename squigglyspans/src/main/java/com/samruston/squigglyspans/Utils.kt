package com.samruston.squigglyspans

import android.content.Context
import android.util.TypedValue

internal val TWO_PI = 2 * Math.PI.toFloat()

internal fun Context.dp(dp: Int): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), resources.displayMetrics)
}