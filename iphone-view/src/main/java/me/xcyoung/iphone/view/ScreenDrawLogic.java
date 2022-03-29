package me.xcyoung.iphone.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;

public class ScreenDrawLogic {
    private final Context context;
    private RectF dockRectF;
    private RectF widgetRectF;
    private final Paint clockBgpaint;
    private final Paint dockPaint;

    private final Paint iconPaint;
    private final Paint iconLinePaint;
    private final Path iconPath;

    private final Paint weatherBgPaint;

    ScreenDrawLogic(Context context) {
        this.context = context;

        this.clockBgpaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.clockBgpaint.setColor(Color.BLACK);
        this.dockPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.dockPaint.setColor(0xaaf9f9f9);

        this.iconPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.iconPaint.setColor(Color.WHITE);

        this.iconLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.iconLinePaint.setColor(Color.BLACK);
        this.iconLinePaint.setStrokeWidth(1);
        this.iconLinePaint.setStyle(Paint.Style.STROKE);
        this.iconLinePaint.setPathEffect(new DashPathEffect(new float[]{10, 10}, 0));
        this.iconPath = new Path();

        this.weatherBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Shader shader = new LinearGradient(50, 50, 500, 500, Color.parseColor("#2196F3"),
                Color.WHITE, Shader.TileMode.CLAMP);
        weatherBgPaint.setShader(shader);
    }

    public void setInsideRectF(RectF insideRectF) {
        float dockWidth = insideRectF.width() * 0.95f;
        float dockHeight = insideRectF.height() * 0.12f;
        float dockBottom = insideRectF.bottom - Utils.dp2px(context, 8);
        float dockTop = dockBottom - dockHeight;
        this.dockRectF = new RectF(
                insideRectF.centerX() - dockWidth / 2,
                dockTop,
                insideRectF.centerX() + dockWidth / 2,
                dockBottom);

        float widgetWidth = insideRectF.width() * 0.85f;
        float widgetStart = insideRectF.centerX() - widgetWidth / 2;
        float widgetEnd = insideRectF.centerX() + widgetWidth / 2;
        float widgetTop = insideRectF.top + Utils.dp2px(context, 50);
//        float widgetTop = insideRectF.top + Utils.dp2px(context, 15);
        float widgetBottom = dockRectF.top - Utils.dp2px(context, 30);
        this.widgetRectF = new RectF(
                widgetStart, widgetTop, widgetEnd, widgetBottom);
    }

    public void onDraw(Canvas canvas) {
        float widgetHeight = this.widgetRectF.height() / 3;
        float widgetSpace = Utils.dp2px(context, 25);
        drawFirstPart(canvas, widgetHeight, widgetRectF.top, widgetSpace);
        drawMiddlePart(canvas, widgetHeight, widgetRectF.top + widgetHeight, widgetSpace);
        drawLastPart(canvas, widgetHeight, widgetRectF.top + 2 * widgetHeight, widgetSpace);
        drawDockPart(canvas);
    }

    private void drawFirstPart(Canvas canvas,
                               float widgetHeight,
                               float top,
                               float space) {
        float radius = 45;
        canvas.drawRoundRect(widgetRectF.left, widgetRectF.top, widgetRectF.right,
                widgetRectF.top + widgetHeight - space, radius, radius, weatherBgPaint);
    }

    private void drawMiddlePart(Canvas canvas,
                                float widgetHeight,
                                float top,
                                float space) {
        float halfWidgetWidth = widgetRectF.width() / 2;
        drawClock(canvas, widgetHeight, top, space);

        float rightPartStart = widgetRectF.left + halfWidgetWidth + space / 2;
        float rightPartWidth = widgetRectF.right - rightPartStart;
        float rightPartHeight = widgetHeight - space;
        float iconSpace = Utils.dp2px(context, 12);
        drawAppIcon(canvas, rightPartHeight / 2 - iconSpace / 2, rightPartWidth / 2 - iconSpace / 2,
                rightPartStart, top);
        drawAppIcon(canvas, rightPartHeight / 2 - iconSpace / 2, rightPartWidth / 2 - iconSpace / 2,
                rightPartStart + rightPartWidth / 2 + iconSpace / 2, top);
        drawAppIcon(canvas, rightPartHeight / 2 - iconSpace / 2, rightPartWidth / 2 - iconSpace / 2,
                rightPartStart, top + rightPartHeight / 2 + iconSpace / 2);
        drawAppIcon(canvas, rightPartHeight / 2 - iconSpace / 2, rightPartWidth / 2 - iconSpace / 2,
                rightPartStart + rightPartWidth / 2 + iconSpace / 2, top + rightPartHeight / 2 + iconSpace / 2);
    }

    private void drawClock(Canvas canvas,
                           float widgetHeight,
                           float top,
                           float space) {
        float halfWidgetWidth = widgetRectF.width() / 2;
        float width = halfWidgetWidth - space / 2;
        float height = widgetHeight - space;
        float radius = width / 5;
        canvas.drawRoundRect(widgetRectF.left,
                top,
                widgetRectF.left + width,
                top + height,
                radius, radius, clockBgpaint);
        canvas.drawArc(widgetRectF.left + 10,
                top + 10,
                widgetRectF.left + width - 10,
                top + height - 10,
                0,
                360,
                true,
                iconPaint);
    }

    private void drawLastPart(Canvas canvas,
                              float widgetHeight,
                              float top,
                              float space) {
        float halfWidgetWidth = widgetRectF.width() / 2;
        float leftPartStart = widgetRectF.left;
        float leftPartWidth = halfWidgetWidth - space / 2;
        float leftPartHeight = widgetHeight - space;
        float iconSpace = Utils.dp2px(context, 12);
        drawAppIcon(canvas, leftPartHeight / 2 - iconSpace / 2, leftPartWidth / 2 - iconSpace / 2,
                leftPartStart, top);
        drawAppIcon(canvas, leftPartHeight / 2 - iconSpace / 2, leftPartWidth / 2 - iconSpace / 2,
                leftPartStart + leftPartWidth / 2 + iconSpace / 2, top);
        drawAppIcon(canvas, leftPartHeight / 2 - iconSpace / 2, leftPartWidth / 2 - iconSpace / 2,
                leftPartStart, top + leftPartHeight / 2 + iconSpace / 2);
        drawAppIcon(canvas, leftPartHeight / 2 - iconSpace / 2, leftPartWidth / 2 - iconSpace / 2,
                leftPartStart + leftPartWidth / 2 + iconSpace / 2, top + leftPartHeight / 2 + iconSpace / 2);

        drawOnePart(canvas, widgetHeight - space, halfWidgetWidth - space / 2,
                widgetRectF.left + halfWidgetWidth + space / 2, top);
    }

    private void drawDockPart(Canvas canvas) {
        canvas.drawRoundRect(this.dockRectF,
                50, 50,
                dockPaint);

        float dockStart = this.dockRectF.left + Utils.dp2px(context, 8);
        float dockTop = this.dockRectF.top + Utils.dp2px(context, 8);
        float dockIconHeight = this.dockRectF.height() - 2 * Utils.dp2px(context, 8);
        float dockIconWidth = (this.dockRectF.width() - 2 * Utils.dp2px(context, 8)) / 4;
        float dockLeft = dockStart;
        for (int i = 0; i < 4; i++) {
            drawAppIcon(canvas, dockIconHeight, dockIconWidth * 0.8f,
                    dockLeft + dockIconWidth * 0.1f, dockTop);
            dockLeft += dockIconWidth;
        }
    }

    private void drawAppIcon(Canvas canvas, float height, float width,
                             float left, float top) {
        float radius = width / 5;
        canvas.drawRoundRect(left,
                top,
                left + width,
                top + height,
                radius, radius, iconPaint);

        iconPath.moveTo(left + 10, top + 10);
        iconPath.lineTo(left + width - 10, top + height - 10);
        iconPath.moveTo(left + width - 10, top + 10);
        iconPath.lineTo(left + 10, top + height - 10);
        canvas.drawPath(iconPath, iconLinePaint);
    }

    private void drawOnePart(Canvas canvas, float height, float width,
                             float left, float top) {
        float radius = width / 5;
        canvas.drawRoundRect(left,
                top,
                left + width,
                top + height,
                radius, radius, clockBgpaint);
    }
}
