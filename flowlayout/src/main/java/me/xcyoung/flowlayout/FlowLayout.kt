package me.xcyoung.flowlayout

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop

class FlowLayout(context: Context, attributeSet: AttributeSet) :
    @kotlin.jvm.JvmOverloads ViewGroup(context, attributeSet) {

    private val rowViewList = mutableListOf<List<View>>()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        rowViewList.clear()

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        val widthSize = MeasureSpec.getSize(widthMeasureSpec) - paddingLeft - paddingRight
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        var measureWidthSize = 0
        var measureHeightSize = 0

        var lineWidth = 0
        var lineHeight = 0

        var currentLineViewList = mutableListOf<View>()
        for (i in 0 until childCount) {
            val childView = getChildAt(i)
            measureChildWithMargins(
                childView,
                widthMeasureSpec,
                0,
                heightMeasureSpec,
                0
            )

            val childWidth = childView.measuredWidth
            val childHeight = childView.measuredHeight

            val childRealWidth =
                childWidth + childView.marginLeft + childView.marginRight
            val childRealHeight =
                childHeight + childView.marginTop + childView.marginBottom

            //  超过宽度，需要换行
            if (lineWidth + childRealWidth > widthSize) {
                measureWidthSize = Math.max(measureWidthSize, lineWidth)
                measureHeightSize += lineHeight

                rowViewList.add(currentLineViewList)

                currentLineViewList = mutableListOf()
                currentLineViewList.add(childView)

                // 因为需要换行，所以下一行的lineWidth和lineHeight至少会是当前子View的宽高？
                lineWidth = childRealWidth
                lineHeight = childRealHeight
            } else {
                //  不考虑换行的话，当前一行的宽度lineWidth为叠加子View，一行的高度lineHeight为子View中最大
                lineWidth += childRealWidth
                lineHeight = Math.max(lineHeight, childRealHeight)

                currentLineViewList.add(childView)
            }

            if (i == childCount - 1) {
                measureWidthSize =
                    Math.max(measureWidthSize, lineWidth) + paddingLeft + paddingRight
                measureHeightSize += lineHeight

                rowViewList.add(currentLineViewList)
            }
        }

        if (widthMode == MeasureSpec.EXACTLY) {
            measureWidthSize = widthSize + paddingLeft + paddingRight
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            measureHeightSize = heightSize
        } else {
            measureHeightSize += paddingTop + paddingBottom
        }

        setMeasuredDimension(measureWidthSize, measureHeightSize)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var left: Int
        var top: Int
        var right: Int
        var bottom: Int

        var curTop = paddingTop
        var curLeft = paddingLeft

        rowViewList.forEachIndexed { index, viewList ->
            var height = 0
            viewList.forEach { view ->
                left = curLeft + view.marginLeft
                top = curTop + view.marginTop
                right = view.measuredWidth + left
                bottom = view.measuredHeight + top

                view.layout(left, top, right, bottom)

                curLeft += view.measuredWidth + view.marginLeft + view.marginRight
                height = Math.max(view.measuredHeight + view.marginTop + view.marginBottom, height)
            }

            curLeft = paddingLeft
            curTop += height
        }

        rowViewList.clear()
    }

    /**
     * 必须重写该方法才能获取子View的margin
     * LayoutInflater中解析xml有这样的逻辑，通过父View获取子View的LayoutParams
     *  ViewGroup.LayoutParams params = root.generateLayoutParams(attrs);
        if (attachToRoot) {
            root.addView(view, params);
        } else {
            view.setLayoutParams(params);
        }
     */
    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return MarginLayoutParams(context, attrs)
    }
}