package me.xcyoung.iphone.view.contour;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

import me.xcyoung.iphone.view.Utils;

public class SanDuanContourLogic extends ContourDrawLogic {
    private Path speakerPath;
    private final Paint speakerPaint;

    public SanDuanContourLogic(Context context) {
        super(context);
        speakerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        speakerPaint.setColor(0xaaf9f9f9);
        speakerPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    public RectF onSizeChanged(int w, int h, int oldw, int oldh) {
        float radius = 90;
        float border = Utils.dp2px(context, 8);
        Path outsidePath = new Path();
        outsidePath.addRoundRect(0, 0, w, h, radius, radius, Path.Direction.CW);


        Path insidePath = new Path();
        RectF insideRectF = new RectF(border, h * 0.1f, w - border,
                h - h * 0.1f);
        insidePath.addRect(insideRectF, Path.Direction.CW);

        Path speakerPath = new Path();
        float speakerWidth = 0.35f * insideRectF.width();
        float speakerHeight = Utils.dp2px(context, 4);
        float speakerLeft = insideRectF.centerX() - speakerWidth / 2;
        float speakerTop = insideRectF.top / 2 - speakerHeight / 2;
        float speakerRight = insideRectF.centerX() + speakerWidth / 2;
        float speakerBottom = speakerTop + speakerHeight;

        speakerPath.addCircle(speakerLeft + speakerHeight / 2, speakerTop + speakerHeight / 2,
                speakerHeight / 2, Path.Direction.CW);
        speakerPath.addRoundRect(speakerLeft + speakerHeight + Utils.dp2px(context, 8),
                speakerTop, speakerRight - Utils.dp2px(context, 8), speakerBottom,
                new float[]{50, 50, 50, 50, 50, 50, 50, 50}, Path.Direction.CW);
        speakerPath.addCircle(1.0f * w / 2, insideRectF.bottom + (h - insideRectF.bottom) / 2,
                Utils.dp2px(context, 20), Path.Direction.CW);
        this.speakerPath = speakerPath;

        outsidePath.op(speakerPath, Path.Op.DIFFERENCE);
        outsidePath.op(insidePath, Path.Op.DIFFERENCE);

        this.insidePath = insidePath;
        this.contourPath = outsidePath;

        return insideRectF;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(this.contourPath, contourPaint);
        canvas.drawPath(this.speakerPath, speakerPaint);
    }
}
