package me.xcyoung.iphone.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import me.xcyoung.iphone.view.contour.ContourDrawLogic;
import me.xcyoung.iphone.view.contour.LiuHaiPingContourLogic;
import me.xcyoung.iphone.view.contour.SanDuanContourLogic;

public class iPhoneView extends View {
    private ContourDrawLogic contourDrawLogic;
    private ScreenDrawLogic screenDrawLogic;

    public iPhoneView(Context context) {
        super(context);
        init(context, null);
    }

    public iPhoneView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public iPhoneView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        this.contourDrawLogic = new LiuHaiPingContourLogic(context);
        this.screenDrawLogic = new ScreenDrawLogic(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);

        setMeasuredDimension(width, (int) ((16f / 9f) * width));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        RectF insideRectF = this.contourDrawLogic.sizeChanged(w, h, oldw, oldh);
        this.contourDrawLogic.setBackground(R.drawable.test_background);
        screenDrawLogic.setInsideRectF(insideRectF);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        this.contourDrawLogic.onDraw(canvas);
        this.screenDrawLogic.onDraw(canvas);
    }

    public void setBackground(int resId) {
        this.contourDrawLogic.setBackground(resId);
        invalidate();
    }
}
