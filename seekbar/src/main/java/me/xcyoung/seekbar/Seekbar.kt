package me.xcyoung.seekbar

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import kotlin.math.abs

class Seekbar : View {
    constructor(context: Context) : super(context) {
        progressAttributes = ProgressAttributes(min = 0, max = 100, step = 1, current = 0)
    }

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        val typeArray = context.obtainStyledAttributes(attributeSet, R.styleable.Seekbar)
        val min = typeArray.getInteger(R.styleable.Seekbar_min, 0)
        val max = typeArray.getInteger(R.styleable.Seekbar_max, 100)
        val step = typeArray.getInteger(R.styleable.Seekbar_step, 1)
        val current = typeArray.getInteger(R.styleable.Seekbar_current, 0)
        progressAttributes =
            ProgressAttributes(min = min, max = max, step = step, current = current)
        typeArray.recycle()
    }

    private val totalProgressRectF: RectF = RectF()
    private val progressRectF: RectF = RectF()

    private val progressAttributes: ProgressAttributes

    //  thumb
    private val thumbRect: Rect = Rect()
    private var thumbWidth: Float = 0f
    private var thumbHeight: Float = 0f

    //  每一刻度的高度，譬如区间[1, 100]，preValueHeight = [1, 2]的距离
    private var preValueHeight: Float = 0f

    private val paint: Paint = Paint().let {
        it.isAntiAlias = true
        it
    }
    private val textPaint = TextPaint().let {
        it.isAntiAlias = true
        it
    }
    private val progressPaint = Paint().let {
        it.isAntiAlias = true
        it
    }

    private var lastUpdateY = -1f   //  用于记录上一次更新的滑块y轴

    var onSeekBarChangeListener: OnSeekBarChangeListener? = null

    init {
        setBackgroundColor(ContextCompat.getColor(context, R.color.cardview_dark_background))
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val preWidth = w / 3
        val rectF = RectF(
            preWidth.toFloat(),
            height.toFloat() * 0.1f,
            2 * preWidth.toFloat(),
            height.toFloat() * 0.95f
        )
        this.totalProgressRectF.left = rectF.left + rectF.width() / 3
        this.totalProgressRectF.top = rectF.top
        this.totalProgressRectF.right = rectF.right - rectF.width() / 3
        this.totalProgressRectF.bottom = rectF.bottom

        this.preValueHeight = totalProgressRectF.height() / progressAttributes.length()

        this.updateProgressRectF()

        this.thumbWidth = this.totalProgressRectF.width() + 4.dp
        this.thumbHeight = this.totalProgressRectF.height() / 20
        this.updateThumb()
    }

    /**
     * MARK: 刻度横线与数字位置计算
     * - 无论什么区间都均分成十份，整个区间的长度为max - min + 1
     * 如：[1, 100]，长度为100 - 1 + 1 = 100；[0, 100]，长度为100 - 0 + 1 = 101。
     * ps：有除不尽的情况，但最终的效果是最底下是min，最顶上是max。
     * - 每个刻度y的计算可参考 {@link #updateProgressRectF}
     */
    override fun onDraw(canvas: Canvas?) {
        canvas ?: return
        super.onDraw(canvas)

        canvas.drawText(progressAttributes.current.toString(), width * 0.5f, height * 0.05f,
            textPaint.let {
                it.color = ContextCompat.getColor(context, R.color.white)
                it.textSize = 20.sp.toFloat()
                it.textAlign = Paint.Align.CENTER
                it
            })

        val preValue = progressAttributes.length() / 10f
        var value = progressAttributes.min.toFloat()
        val lineWidth = width.toFloat() / 9
        for (i in 0..10) {
            val y =
                totalProgressRectF.top + (totalProgressRectF.height() - (value.toInt() - progressAttributes.min + 1) * this.preValueHeight)
            canvas.drawLine(lineWidth * 2, y, lineWidth * 3, y,
                paint.let {
                    it.style = Paint.Style.FILL
                    it.color = Color.WHITE
                    it
                })
            canvas.drawText("${value.toInt()}",
                2 * width.toFloat() / 3,
                y,
                textPaint.let {
                    it.color = Color.WHITE
                    it.textSize = 12.sp.toFloat()
                    it.textAlign = Paint.Align.LEFT
                    it
                })

            value += preValue
            if (value > progressAttributes.max) value = progressAttributes.max.toFloat()
        }

        canvas.drawRoundRect(this.totalProgressRectF, 4.dp.toFloat(), 4.dp.toFloat(),
            paint.let {
                it.style = Paint.Style.FILL
                it.color = Color.parseColor("#121f29")
                it
            })

        canvas.drawRoundRect(this.totalProgressRectF, 4.dp.toFloat(), 4.dp.toFloat(),
            paint.let {
                it.style = Paint.Style.STROKE
                it.color = Color.parseColor("#dcdce1")
                it
            })

        canvas.drawRoundRect(
            this.progressRectF,
            4.dp.toFloat(), 4.dp.toFloat(),
            progressPaint.let {
                it.style = Paint.Style.FILL
                it.color = ContextCompat.getColor(context, R.color.teal_200)
                it
            }
        )

        canvas.drawCircle(
            thumbRect.centerX().toFloat(),
            thumbRect.centerY().toFloat(),
            (thumbRect.height() / 2).toFloat(),
            progressPaint.let {
                it.style = Paint.Style.FILL
                it.color = ContextCompat.getColor(context, R.color.purple_200)
                it
            })
    }

    /**
     * MARK: 进度条的top计算
     * - progressAttributes.current - progressAttributes.min + 1: 计算出current在[min, max]的第几位。
     * 如：区间[1，134]，2在该区间的第(2 - 1) + 1 = 2位；区间[0，100]，50在该区间的第(50 - 0) + 1位。
     * - (progressAttributes.current - progressAttributes.min + 1) * preValueHeight: 截止到current位置
     * 占整个矩形的高度。
     * - totalProgressRectF.height() - (progressAttributes.current - progressAttributes.min + 1) * preValueHeight:
     * 因为从下到上以此变大，需要进行高度修正
     * - totalProgressRectF.top +: 补全矩形上方到0的距离
     * */
    private fun updateProgressRectF() {
        this.progressRectF.left = this.totalProgressRectF.left
        this.progressRectF.top = totalProgressRectF.top + (totalProgressRectF.height()
                - (progressAttributes.current - progressAttributes.min + 1) * preValueHeight)
        this.progressRectF.right = this.totalProgressRectF.right
        this.progressRectF.bottom = this.totalProgressRectF.bottom
    }

    private fun updateThumb() {
        thumbRect.left = (totalProgressRectF.left - 4.dp).toInt()
        thumbRect.top = (progressRectF.top - thumbWidth / 2).toInt()
        thumbRect.right = (totalProgressRectF.right + 4.dp).toInt()
        thumbRect.bottom = (progressRectF.top - thumbWidth / 2 + thumbHeight).toInt()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event ?: return false

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (thumbRect.contains(event.x.toInt(), event.y.toInt())) {
                    lastUpdateY = event.y
                    return true
                }
            }
            MotionEvent.ACTION_MOVE -> {
                val old = progressAttributes.current
                val moveH = this.preValueHeight * progressAttributes.step
                val dy = event.y - lastUpdateY
                if (abs(dy) > moveH) {
                    var top = progressRectF.top
                    var currentV = progressAttributes.current
                    when {
                        dy > 0 -> {  // 向下移
                            top += moveH
                            currentV -= progressAttributes.step
                        }
                        dy < 0 -> {    // 向上移
                            top -= moveH
                            currentV += progressAttributes.step
                        }
                        else -> {
                            return true
                        }
                    }
                    when {
                        top <= totalProgressRectF.top ||
                                currentV > progressAttributes.max -> {
                            progressRectF.top = totalProgressRectF.top
                            progressAttributes.current = progressAttributes.max
                        }
                        top >= totalProgressRectF.bottom ||
                                currentV < progressAttributes.min -> {
                            progressRectF.top = totalProgressRectF.bottom
                            progressAttributes.current = progressAttributes.min
                        }
                        else -> {
                            progressRectF.top = top
                            progressAttributes.current = currentV
                        }
                    }
                    lastUpdateY = event.y
                    updateThumb()
                    invalidate()
                    onSeekBarChangeListener?.onProgressChanged(progressAttributes.current, old)
                }
            }
            MotionEvent.ACTION_UP -> {
                lastUpdateY = -1f
                return thumbRect.contains(event.x.toInt(), event.y.toInt())
            }
        }

        return super.onTouchEvent(event)
    }

    data class ProgressAttributes(
        val max: Int = 100,
        val min: Int = 0,
        val step: Int = 1,
        var current: Int = 0
    ) {
        fun length(): Int = max - min + 1
    }

    interface OnSeekBarChangeListener {
        fun onProgressChanged(current: Int, old: Int)
    }
}