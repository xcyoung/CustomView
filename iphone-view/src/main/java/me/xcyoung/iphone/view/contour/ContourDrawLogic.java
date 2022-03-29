package me.xcyoung.iphone.view.contour;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;

public abstract class ContourDrawLogic {
    protected Context context;
    protected Path contourPath;
    protected Path insidePath;
    protected Paint contourPaint;
    protected Paint bgPaint;
    protected Bitmap backgroundBitmap;
    protected RectF insideRectF;

    ContourDrawLogic(Context context) {
        contourPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        contourPaint.setColor(Color.BLACK);
        this.bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.context = context;
    }

    public RectF sizeChanged(int w, int h, int oldw, int oldh) {
        RectF rectF = onSizeChanged(w, h, oldw, oldh);
        this.insideRectF = rectF;
        return rectF;
    }

    abstract public RectF onSizeChanged(int w, int h, int oldw, int oldh);

    public void onDraw(Canvas canvas) {
        canvas.drawPath(this.insidePath, bgPaint);
    }

    public void setBackground(int resId) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap backgroundBitmap = BitmapFactory.decodeResource(context.getResources(), resId, options);
        float targetWidth = insideRectF.width();
        float targetHeight = insideRectF.height();
        Bitmap backgroundBitmap2 = Bitmap.createScaledBitmap(backgroundBitmap, (int) targetWidth,
                (int) targetHeight, false);

        BitmapShader shader = new BitmapShader(backgroundBitmap2, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        bgPaint.setShader(shader);
        this.backgroundBitmap = backgroundBitmap2;
        bgPaint.setShader(shader);
    }
}
