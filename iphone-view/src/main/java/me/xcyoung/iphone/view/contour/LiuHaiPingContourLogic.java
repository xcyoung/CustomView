package me.xcyoung.iphone.view.contour;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

import androidx.annotation.Nullable;

import me.xcyoung.iphone.view.Utils;

public class LiuHaiPingContourLogic extends ContourDrawLogic {
    private Path speakerPath;
    private final Paint speakerPaint;

    public LiuHaiPingContourLogic(Context context) {
        super(context);
        speakerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        speakerPaint.setColor(0xaaf9f9f9);
        speakerPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    public RectF onSizeChanged(int w, int h, int oldw, int oldh) {
        float radius = 90;
        Path outsidePath = new Path();
        outsidePath.addRoundRect(0, 0, w, h, radius, radius, Path.Direction.CW);

        Path insidePath = new Path();
        float insideWidth = Utils.dp2px(context, 5);
        RectF insideRectF = new RectF(insideWidth, insideWidth, w - insideWidth,
                h - insideWidth);
        insidePath.addRoundRect(insideRectF, radius, radius, Path.Direction.CW);

        Path bangsPath = new Path();
        float bangsWidth = 0.4f * insideRectF.width();
        float bangsLeft = insideRectF.centerX() - bangsWidth / 2;
        float bangsTop = insideRectF.top;
        float bangsRight = insideRectF.centerX() + bangsWidth / 2;
        float bangsBottom = bangsTop + Utils.dp2px(context, 25);
        bangsPath.addRoundRect(bangsLeft, bangsTop, bangsRight, bangsBottom,
                new float[]{0, 0, 0, 0, 45, 50, 45, 50}, Path.Direction.CW);
        insidePath.op(bangsPath, Path.Op.DIFFERENCE);
        outsidePath.op(insidePath, Path.Op.DIFFERENCE);
        this.insidePath = insidePath;
        this.contourPath = outsidePath;

        Path speakerPath = new Path();
        float speakerWidth = 0.5f * bangsWidth;
        float speakerLeft = insideRectF.centerX() - speakerWidth / 2;
        float speakerTop = 0f;
        float speakerRight = insideRectF.centerX() + speakerWidth / 2;
        float speakerBottom = speakerTop + Utils.dp2px(context, 5);
        speakerPath.addRoundRect(speakerLeft, speakerTop, speakerRight, speakerBottom,
                new float[]{0, 0, 0, 0, 20, 20, 20, 20}, Path.Direction.CW);
        this.speakerPath = speakerPath;
        return insideRectF;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(this.contourPath, contourPaint);
        canvas.drawPath(this.speakerPath, speakerPaint);
    }
}
