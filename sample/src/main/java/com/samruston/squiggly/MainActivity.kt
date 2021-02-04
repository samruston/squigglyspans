package com.samruston.squiggly

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spanned
import android.util.TypedValue
import android.widget.TextView
import androidx.core.text.buildSpannedString
import androidx.core.text.inSpans
import com.samruston.squigglyspans.spans.DefaultSquigglySpan

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val simpleTextView = findViewById<TextView>(R.id.simple)
        val advancedTextView = findViewById<TextView>(R.id.advanced)

        simpleTextView.text = buildSimple()
        advancedTextView.text = buildAdvanced()
    }

    private fun buildSimple(): Spanned {

        val span = DefaultSquigglySpan(this)

        return buildSpannedString {
            append("Hello ")
            inSpans(span) {
                append("Squiggly World")
            }
        }
    }

    private fun buildAdvanced(): Spanned {
        val span = RainbowRoadSpan(this, 0f).apply {
            setDuration(200)
        }

        val secondSpan = RainbowRoadSpan(this, 180f).apply {
            setDuration(200)
            setBaselineOffset(dp(4))
            setDefaultWaveOffset(Math.PI.toFloat()/6)
        }

        return buildSpannedString {
            append("Hello ")
            inSpans(span) {
                inSpans(secondSpan) {
                    append("Rainbow Road")
                }
            }
        }
    }

    private fun Context.dp(dp: Int): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), resources.displayMetrics)
    }
}