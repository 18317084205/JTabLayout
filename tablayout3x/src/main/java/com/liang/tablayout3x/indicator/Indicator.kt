package com.liang.tablayout3x.indicator

import android.graphics.RectF
import android.graphics.drawable.Drawable

abstract class Indicator {
    var widthScale = 0.5f
    var width = 0
    var height = 0
    var color = 0
    var animationDuration = 0
    var margin = 0
    var gravity = 0
    var isTransitionScroll = false
    abstract fun getDrawable(): Drawable?

    fun setGravity(gravity: Int): Indicator {
        this.gravity = gravity
        return this
    }

    fun widthScale(widthScale: Float): Indicator {
        this.widthScale = widthScale
        return this
    }

    fun width(width: Int): Indicator {
        this.width = width
        return this
    }

    fun height(height: Int): Indicator {
        this.height = height
        return this
    }

    fun color(color: Int): Indicator {
        this.color = color
        return this
    }

    fun animationDuration(animationDuration: Int): Indicator {
        this.animationDuration = animationDuration
        return this
    }

    fun margin(margin: Int): Indicator {
        this.margin = margin
        return this
    }

    fun transitionScroll(transitionScroll: Boolean): Indicator {
        isTransitionScroll = transitionScroll
        return this
    }

    val isFullWidth: Boolean
        get() = !(widthScale > 0 || width > 0)

    companion object {
        const val INDICATOR_GRAVITY_BOTTOM = 0
        const val INDICATOR_GRAVITY_CENTER = 1
        const val INDICATOR_GRAVITY_TOP = 2
        const val INDICATOR_GRAVITY_STRETCH = 3
    }
}


class IndicatorPoint {

    @JvmField
    var left = 0f
    @JvmField
    var right = 0f
    @JvmField
    var top = 0f
    @JvmField
    var bottom = 0f

    companion object {
        fun create(rectF: RectF) = IndicatorPoint().apply {
            left = rectF.left
            right = rectF.right
            top = rectF.top
            bottom = rectF.bottom
        }
    }

    fun set(point: IndicatorPoint): IndicatorPoint = this.apply {
        left = point.left
        right = point.right
        top = point.top
        bottom = point.bottom
    }

    override fun equals(other: Any?): Boolean {
        if (other !is IndicatorPoint) {
            return false
        }
        return (other.left == left && other.top == top) || (other.bottom == bottom && other.right == right)
    }

    override fun hashCode(): Int {
        var result = left.hashCode()
        result = 31 * result + right.hashCode()
        result = 31 * result + top.hashCode()
        result = 31 * result + bottom.hashCode()
        return result
    }
}