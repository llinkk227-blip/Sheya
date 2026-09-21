package com.example.sheya5;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

public class PersonView extends View {

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private float headAngle = 0f;
    private float targetAngle = 0f;
    private float shoulderOffset = 0f;
    private float breathing = 0f;

    private ValueAnimator poseAnimator;
    private ValueAnimator breathingAnimator;

    public PersonView(Context context) {
        super(context);

        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);

        startBreathing();
    }

    public void setPose(String pose) {

        float newAngle = 0f;
        float newShoulders = 0f;

        switch (pose) {

            case "right":
                newAngle = 18f;
                break;

            case "left":
                newAngle = -18f;
                break;

            case "tiltRight":
                newAngle = 14f;
                break;

            case "tiltLeft":
                newAngle = -14f;
                break;

            case "down":
                newAngle = 10f;
                break;

            case "up":
                newAngle = -7f;
                break;

            case "shoulders":
                newShoulders = -15f;
                break;

            case "back":
                newShoulders = 10f;
                break;

            case "scapula":
                newShoulders = 6f;
                break;

            case "relax":
                newShoulders = 0f;
                break;
        }

        animatePose(newAngle, newShoulders);
    }

    private void animatePose(
            float newAngle,
            float newShoulders
    ) {

        if (poseAnimator != null) {
            poseAnimator.cancel();
        }

        final float startAngle = headAngle;
        final float startShoulders = shoulderOffset;

        targetAngle = newAngle;

        poseAnimator = ValueAnimator.ofFloat(0f, 1f);

        poseAnimator.setDuration(650);
        poseAnimator.setInterpolator(
                new AccelerateDecelerateInterpolator()
        );

        poseAnimator.addUpdateListener(animation -> {

            float value = (float) animation.getAnimatedValue();

            headAngle =
                    startAngle
                            + (newAngle - startAngle) * value;

            shoulderOffset =
                    startShoulders
                            + (newShoulders - startShoulders) * value;

            invalidate();
        });

        poseAnimator.start();
    }

    private void startBreathing() {

        breathingAnimator =
                ValueAnimator.ofFloat(-2f, 2f);

        breathingAnimator.setDuration(2200);
        breathingAnimator.setRepeatMode(
                ValueAnimator.REVERSE
        );
        breathingAnimator.setRepeatCount(
                ValueAnimator.INFINITE
        );

        breathingAnimator.addUpdateListener(animation -> {

            breathing =
                    (float) animation.getAnimatedValue();

            invalidate();
        });

        breathingAnimator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        float width = getWidth();
        float height = getHeight();

        float cx = width / 2f;

        float bodyTop = height * 0.32f;
        float shoulderY = bodyTop + 80f + breathing;
        float bodyBottom = height * 0.76f;

        // мягкое свечение за человеком
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.rgb(24, 54, 86));

        canvas.drawCircle(
                cx,
                height * 0.43f,
                Math.min(width, height) * 0.31f,
                paint
        );

        // тело
        paint.setColor(Color.rgb(77, 166, 255));
        paint.setStyle(Paint.Style.FILL);

        RectF body = new RectF(
                cx - 68,
                shoulderY,
                cx + 68,
                bodyBottom
        );

        canvas.drawRoundRect(
                body,
                35,
                35,
                paint
        );

        // шея
        paint.setColor(Color.rgb(236, 190, 155));

        canvas.drawRoundRect(
                new RectF(
                        cx - 24,
                        shoulderY - 50,
                        cx + 24,
                        shoulderY + 12
                ),
                18,
                18,
                paint
        );

        // голова
        canvas.save();

        canvas.rotate(
                headAngle,
                cx,
                shoulderY - 78
        );

        paint.setColor(Color.rgb(242, 199, 165));

        canvas.drawCircle(
                cx,
                shoulderY - 92,
                55,
                paint
        );

        // волосы
        paint.setColor(Color.rgb(47, 55, 72));

        canvas.drawOval(
                new RectF(
                        cx - 58,
                        shoulderY - 148,
                        cx + 58,
                        shoulderY - 92
                ),
                paint
        );

        // лицо
        paint.setColor(Color.rgb(40, 50, 65));

        canvas.drawCircle(
                cx - 20,
                shoulderY - 92,
                4,
                paint
        );

        canvas.drawCircle(
                cx + 20,
                shoulderY - 92,
                4,
                paint
        );

        canvas.restore();

        // плечи и руки
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(25);
        paint.setColor(Color.rgb(77, 166, 255));

        canvas.drawLine(
                cx - 48,
                shoulderY + 10,
                cx - 105,
                shoulderY + 75 + shoulderOffset,
                paint
        );

        canvas.drawLine(
                cx + 48,
                shoulderY + 10,
                cx + 105,
                shoulderY + 75 + shoulderOffset,
                paint
        );

        // кисти
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.rgb(236, 190, 155));

        canvas.drawCircle(
                cx - 105,
                shoulderY + 75 + shoulderOffset,
                14,
                paint
        );

        canvas.drawCircle(
                cx + 105,
                shoulderY + 75 + shoulderOffset,
                14,
                paint
        );

        // маленькая подсказка
        paint.setColor(Color.rgb(145, 166, 192));
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(17);

        canvas.drawText(
                "Двигайтесь мягко",
                cx,
                height - 32,
                paint
        );
    }
}
