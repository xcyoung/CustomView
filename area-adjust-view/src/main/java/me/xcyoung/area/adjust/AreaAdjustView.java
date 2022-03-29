package me.xcyoung.area.adjust;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

public class AreaAdjustView extends View {
    private final static int LEFT_TOP = 0;
    private final static int RIGHT_TOP = 1;
    private final static int LEFT_BOTTOM = 2;
    private final static int RIGHT_BOTTOM = 3;
    private final static int CENTER = 4;

    private Paint pointPaint;
    private Paint linePaint;
    private Paint areaPaint;
    private final float[] areaPoint = new float[4]; // 0 left 1 top 2 right 3 bottom
    private final int minLineLength = (int) dp2px(100);
    private float areaPointRadius = dp2px(10);
    private int areaPointColor = Color.YELLOW;
    private int areaLineColor = Color.BLUE;
    private int currentTouchPos = -1;
    private Path areaPath;

    public AreaAdjustView(Context context) {
        super(context);
        init(null);
    }

    public AreaAdjustView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public AreaAdjustView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.AreaAdjustView);
            this.areaPointRadius = typedArray.getDimension(R.styleable.AreaAdjustView_areaPointRadius, dp2px(10));
            this.areaPointColor = typedArray.getColor(R.styleable.AreaAdjustView_areaPointColor, Color.YELLOW);
            this.areaLineColor = typedArray.getColor(R.styleable.AreaAdjustView_areaLineColor, Color.BLUE);
            typedArray.recycle();
        }

        Paint pointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pointPaint.setColor(this.areaPointColor);
        this.pointPaint = pointPaint;

        Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(this.areaLineColor);
//        linePaint.setPathEffect(new DashPathEffect(new float[]{dp2px(10), dp2px(10)}, 0));
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(dp2px(3));
        this.linePaint = linePaint;

        Paint areaPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        areaPaint.setColor(0xaaffffff);
        this.areaPaint = areaPaint;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        float areaLeft = (float) (w * 0.2);
        float areaTop = (float) (h * 0.2);
        float areaRight = w - areaLeft;
        float areaBottom = h - areaTop;

        this.areaPoint[0] = areaLeft;
        this.areaPoint[1] = areaTop;
        this.areaPoint[2] = areaRight;
        this.areaPoint[3] = areaBottom;
        resetAreaPath();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float areaLeft = areaPoint[0];
        float areaTop = areaPoint[1];
        float areaRight = areaPoint[2];
        float areaBottom = areaPoint[3];

        canvas.drawPath(this.areaPath, areaPaint);

        canvas.drawRect(
                areaLeft,
                areaTop,
                areaRight,
                areaBottom, linePaint);

        canvas.drawRect(areaLeft - this.areaPointRadius, areaTop - this.areaPointRadius,
                areaLeft + this.areaPointRadius, areaTop + this.areaPointRadius, pointPaint);

        canvas.drawRect(areaRight - this.areaPointRadius, areaTop - this.areaPointRadius,
                areaRight + this.areaPointRadius,
                areaTop + this.areaPointRadius, pointPaint);

        canvas.drawRect(areaLeft - this.areaPointRadius, areaBottom - this.areaPointRadius,
                areaLeft + this.areaPointRadius, areaBottom + this.areaPointRadius, pointPaint);

        canvas.drawRect(areaRight - this.areaPointRadius, areaBottom - this.areaPointRadius,
                areaRight + this.areaPointRadius, areaBottom + this.areaPointRadius, pointPaint);

        float centerX = areaRight - (areaRight - areaLeft) / 2f;
        float centerY = areaBottom - (areaBottom - areaTop) / 2f;
        canvas.drawRect(centerX - this.areaPointRadius, centerY - this.areaPointRadius,
                centerX + this.areaPointRadius, centerY + this.areaPointRadius, pointPaint);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int areaLeft = (int) areaPoint[0];
            int areaTop = (int) areaPoint[1];
            int areaRight = (int) areaPoint[2];
            int areaBottom = (int) areaPoint[3];
            int centerX = areaRight - (areaRight - areaLeft) / 2;
            int centerY = areaBottom - (areaBottom - areaTop) / 2;
            if (inRect(areaLeft, areaTop, x, y)) {
                Log.d(this.getClass().getSimpleName(), "ACTION_DOWN LEFT_TOP");
                this.currentTouchPos = LEFT_TOP;
                return true;
            } else if (inRect(areaRight, areaTop, x, y)) {
                Log.d(this.getClass().getSimpleName(), "ACTION_DOWN RIGHT_TOP");
                this.currentTouchPos = RIGHT_TOP;
                return true;
            } else if (inRect(areaLeft, areaBottom, x, y)) {
                Log.d(this.getClass().getSimpleName(), "ACTION_DOWN LEFT_BOTTOM");
                this.currentTouchPos = LEFT_BOTTOM;
                return true;
            } else if (inRect(areaRight, areaBottom, x, y)) {
                Log.d(this.getClass().getSimpleName(), "ACTION_DOWN RIGHT_BOTTOM");
                this.currentTouchPos = RIGHT_BOTTOM;
                return true;
            } else if (inRect(centerX, centerY, x, y)) {
                Log.d(this.getClass().getSimpleName(), "ACTION_DOWN CENTER");
                this.currentTouchPos = CENTER;
                return true;
            }
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (currentTouchPos == LEFT_TOP) {
                Log.d(this.getClass().getSimpleName(), "ACTION_MOVE handleLeftTopMove");
                handleLeftTopMove(x, y);
            } else if (currentTouchPos == RIGHT_TOP) {
                Log.d(this.getClass().getSimpleName(), "ACTION_MOVE handleRightTopMove");
                handleRightTopMove(x, y);
            } else if (currentTouchPos == LEFT_BOTTOM) {
                Log.d(this.getClass().getSimpleName(), "ACTION_MOVE handleLeftBottomMove");
                handleLeftBottomMove(x, y);
            } else if (currentTouchPos == RIGHT_BOTTOM) {
                Log.d(this.getClass().getSimpleName(), "ACTION_MOVE handleRightBottomMove");
                handleRightBottomMove(x, y);
            } else if (currentTouchPos == CENTER) {
                Log.d(this.getClass().getSimpleName(), "ACTION_MOVE handleCenterMove");
                handleCenterMove(x, y);
            }
            resetAreaPath();
            return true;
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            currentTouchPos = -1;
            return true;
        }
        return super.onTouchEvent(event);
    }

    private void handleLeftTopMove(int x, int y) {
        float areaLeft;
        float areaTop;
        float areaRight = areaPoint[2];
        float areaBottom = areaPoint[3];
        float radius = this.areaPointRadius;

        float minLeft = (getPaddingLeft() + radius);
        float minTop = (getPaddingTop() + radius);
        float maxLeft = areaRight - minLineLength;
        float maxTop = areaBottom - minLineLength;

        areaLeft = Math.min(Math.max(x, minLeft), maxLeft);
        areaTop = Math.min(Math.max(y, minTop), maxTop);

        this.areaPoint[0] = areaLeft;
        this.areaPoint[1] = areaTop;
        invalidate();
    }

    private void handleRightTopMove(int x, int y) {
        float areaLeft = areaPoint[0];
        float areaTop;
        float areaRight;
        float areaBottom = areaPoint[3];
        float radius = this.areaPointRadius;

        float minRight = areaLeft + minLineLength;
        float minTop = (getPaddingTop() + radius);
        float maxRight = (getWidth() - getPaddingRight() - radius);
        float maxTop = areaBottom - minLineLength;

        areaRight = Math.min(Math.max(x, minRight), maxRight);
        areaTop = Math.min(Math.max(y, minTop), maxTop);

        this.areaPoint[2] = areaRight;
        this.areaPoint[1] = areaTop;
        invalidate();
    }

    private void handleLeftBottomMove(int x, int y) {
        float areaLeft;
        float areaTop = areaPoint[1];
        float areaRight = areaPoint[2];
        float areaBottom;
        float radius = this.areaPointRadius;

        float minLeft = (getPaddingLeft() + radius);
        float minBottom = areaTop + minLineLength;
        float maxLeft = areaRight - minLineLength;
        float maxBottom = (getHeight() - getPaddingBottom() - radius);

        areaLeft = Math.min(Math.max(x, minLeft), maxLeft);
        areaBottom = Math.min(Math.max(y, minBottom), maxBottom);
        this.areaPoint[0] = areaLeft;
        this.areaPoint[3] = areaBottom;
        invalidate();
    }

    private void handleRightBottomMove(int x, int y) {
        float areaLeft = areaPoint[0];
        float areaTop = areaPoint[1];
        float areaRight;
        float areaBottom;
        float radius = this.areaPointRadius;

        float minRight = areaLeft + minLineLength;
        float minBottom = areaTop + minLineLength;
        float maxRight = (getWidth() - getPaddingRight() - radius);
        float maxBottom = (getHeight() - getPaddingBottom() - radius);

        areaRight = Math.min(Math.max(x, minRight), maxRight);
        areaBottom = Math.min(Math.max(y, minBottom), maxBottom);
        this.areaPoint[2] = areaRight;
        this.areaPoint[3] = areaBottom;
        invalidate();
    }

    private void handleCenterMove(int x, int y) {
        float areaLeft = areaPoint[0];
        float areaTop = areaPoint[1];
        float areaRight = areaPoint[2];
        float areaBottom = areaPoint[3];

        float frameWidth = areaRight - areaLeft;
        float frameHeight = areaBottom - areaTop;
        float radius = this.areaPointRadius;

        float minLeft = (getPaddingLeft() + radius);
        float minTop = (getPaddingTop() + radius);
        float centerMinX = frameWidth / 2 + minLeft;
        float centerMinY = frameHeight / 2 + minTop;

        float maxRight = (getWidth() - getPaddingRight() - radius);
        float maxBottom = (getHeight() - getPaddingBottom() - radius);
        float centerMaxX = maxRight - frameWidth / 2;
        float centerMaxY = maxBottom - frameHeight / 2;

        float centerCurrentX = Math.min(Math.max(x, centerMinX), centerMaxX);
        float centerCurrentY = Math.min(Math.max(y, centerMinY), centerMaxY);

        float newLeft = centerCurrentX - frameWidth / 2;
        float newRight = centerCurrentX + frameWidth / 2;
        float newTop = centerCurrentY - frameHeight / 2;
        float newBottom = centerCurrentY + frameHeight / 2;

        this.areaPoint[0] = newLeft;
        this.areaPoint[1] = newTop;
        this.areaPoint[2] = newRight;
        this.areaPoint[3] = newBottom;
        invalidate();
    }

    private void resetAreaPath() {
        int viewWidth = getWidth();
        int viewHeight = getHeight();
        float areaLeft = areaPoint[0];
        float areaTop = areaPoint[1];
        float areaRight = areaPoint[2];
        float areaBottom = areaPoint[3];

        Path path = new Path();
        path.moveTo(0 ,0);
        path.lineTo(viewWidth, 0);
        path.lineTo(viewWidth, viewHeight);
        path.lineTo(0, getHeight());
        Path path1 = new Path();
        path1.moveTo(areaLeft, areaTop);
        path1.lineTo(areaRight, areaTop);
        path1.lineTo(areaRight, areaBottom);
        path1.lineTo(areaLeft, areaBottom);
        path.op(path1, Path.Op.DIFFERENCE);
        this.areaPath = path;
    }

    private boolean inRect(int centerX, int centerY, int x, int y) {
        int padding = 10;
        int radius = (int) this.areaPointRadius;
        int left = centerX - radius - (int) dp2px(padding);
        int top = centerY - radius - (int) dp2px(padding);
        int right = centerX + radius + (int) dp2px(padding);
        int bottom = centerY + radius + (int) dp2px(padding);
        return (left <= x && right >= x && top <= y && bottom >= y);
    }

    private float dp2px(int dp) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return dp * density;
    }
}
