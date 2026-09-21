package com.example.sheya5;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

public class PersonView extends View {

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private String pose = "";

    private float breath = 0f;
    private ValueAnimator breathingAnimator;

    public PersonView(Context context) {
        super(context);
        init();
    }

    public PersonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PersonView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);

        startBreathing();
    }

    private void startBreathing() {
        breathingAnimator = ValueAnimator.ofFloat(0f, 1f);
        breathingAnimator.setDuration(3000);
        breathingAnimator.setRepeatCount(ValueAnimator.INFINITE);
        breathingAnimator.setRepeatMode(ValueAnimator.REVERSE);
        breathingAnimator.setInterpolator(new LinearInterpolator());

        breathingAnimator.addUpdateListener(animation -> {
            breath = (float) animation.getAnimatedValue();
            invalidate();
        });

        breathingAnimator.start();
    }

    public void setPose(String newPose) {
        pose = newPose == null ? "" : newPose;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float w = getWidth();
        float h = getHeight();

        float cx = w / 2f;
        float headY = h * 0.25f;

        // Небольшое движение при дыхании
        float breathingOffset = breath * 4f;

        // Настройки персонажа
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(10f);

        // Голова
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(cx, headY + breathingOffset, 42f, paint);

        // Шея
        paint.setStrokeWidth(18f);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawLine(
                cx,
                headY + 35f,
                cx,
                headY + 75f + breathingOffset,
                paint
        );

        // Тело
        paint.setStrokeWidth(24f);
        canvas.drawLine(
                cx,
                headY + 75f + breathingOffset,
                cx,
                h * 0.65f,
                paint
        );

        // Руки
        paint.setStrokeWidth(18f);

        float shoulderY = headY + 100f + breathingOffset;

        if ("shoulders".equals(pose)) {
            shoulderY -= 20f;
        }

        canvas.drawLine(
                cx,
                shoulderY,
                cx - 90f,
                shoulderY + 70f,
                paint
        );

        canvas.drawLine(
                cx,
                shoulderY,
                cx + 90f,
                shoulderY + 70f,
                paint
        );

        // Ноги
        paint.setStrokeWidth(20f);

        canvas.drawLine(
                cx,
                h * 0.65f,
                cx - 60f,
                h * 0.88f,
                paint
        );

        canvas.drawLine(
                cx,
                h * 0.65f,
                cx + 60f,
                h * 0.88f,
                paint
        );

        // Направление движения головы
        drawHeadDirection(canvas, cx, headY + breathingOffset);
    }

    private void drawHeadDirection(Canvas canvas, float cx, float cy) {

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(8f);

        if ("right".equals(pose)) {
            drawArrow(canvas, cx + 45f, cy - 5f, cx + 95f, cy - 5f);
        }

        if ("left".equals(pose)) {
            drawArrow(canvas, cx - 45f, cy - 5f, cx - 95f, cy - 5f);
        }

        if ("tiltRight".equals(pose)) {
            drawArrow(canvas, cx + 45f, cy + 5f, cx + 80f, cy + 40f);
        }

        if ("tiltLeft".equals(pose)) {
            drawArrow(canvas, cx - 45f, cy + 5f, cx - 80f, cy + 40f);
        }

        if ("down".equals(pose)) {
            drawArrow(canvas, cx, cy + 45f, cx, cy + 90f);
        }

        if ("up".equals(pose)) {
            drawArrow(canvas, cx, cy - 45f, cx, cy - 90f);
        }
    }

    private void drawArrow(
            Canvas canvas,
            float x1,
            float y1,
            float x2,
            float y2
    ) {
        canvas.drawLine(x1, y1, x2, y2, paint);

        float angle = (float) Math.atan2(y2 - y1, x2 - x1);

        float size = 18f;

        Path path = new Path();

        path.moveTo(x2, y2);

        path.lineTo(
                x2 - size * (float) Math.cos(angle - Math.PI / 6),
                y2 - size * (float) Math.sin(angle - Math.PI / 6)
        );

        path.moveTo(x2, y2);

        path.lineTo(
                x2 - size * (float) Math.cos(angle + Math.PI / 6),
                y2 - size * (float) Math.sin(angle + Math.PI / 6)
        );

        canvas.drawPath(path, paint);
    }

    @Override
    protected void onDetachedFromWindow() {
        if (breathingAnimator != null) {
            breathingAnimator.cancel();
        }

        super.onDetachedFromWindow();
    }
}
