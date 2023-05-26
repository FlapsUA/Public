package com.golden13way.indigofilms.classes

import android.content.Context
import android.util.AttributeSet
import android.widget.ScrollView
import android.widget.Scroller

class CustomScrollView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ScrollView(context, attrs, defStyleAttr) {
    private var scroller: Scroller? = null

    init {
        scroller = Scroller(getContext())
    }

    override fun computeScroll() {
        if (scroller!!.computeScrollOffset()) {
            scrollTo(0, scroller!!.currY)
            postInvalidate()
        }
    }

    fun customSmoothScrollTo(destY: Int, duration: Int) {
        scroller!!.startScroll(0, scrollY, 0, destY - scrollY, duration)
        invalidate()
    }
}