package me.xcyoung.seekbar

import android.content.res.Resources

val Number.dp: Int get() = (Resources.getSystem().displayMetrics.density * toInt()).toInt()

val Number.sp: Int get() = (Resources.getSystem().displayMetrics.scaledDensity * toInt()).toInt()

val Number.px: Int get() = toInt()