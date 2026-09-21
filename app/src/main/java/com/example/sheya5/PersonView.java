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

    private float animationValue = 0f;
    private ValueAnimator animator;

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

        paint.setAntiAlias(true);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);

        startAnimation();
    }

    private void startAnimation() {

        animator = ValueAnimator.ofFloat(0f, 1f);

        animator.setDuration(1800);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.setInterpolator(new LinearInterpolator());

        animator.addUpdateListener(animation -> {

            animationValue = (float) animation.getAnimatedValue();

            invalidate();
        });

        animator.start();
    }

    public void setPose(String newPose) {

        if (newPose == null) {
            pose = "";
        } else {
            pose = newPose;
        }

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        float width = getWidth();
        float height = getHeight();

        float centerX = width / 2f;

        /*
         * Основное положение тела
         */

        float headX = centerX;
        float headY = height * 0.22f;

        float shoulderY = height * 0.40f;

        float bodyBottom = height * 0.68f;

        /*
         * Амплитуда движения.
         */

        float movement = (float) Math.sin(animationValue * Math.PI);

        /*
         * Положение головы
         */

        float dx = 0f;
        float dy = 0f;

        if ("right".equals(pose)) {

            dx = 35f * movement;

        } else if ("left".equals(pose)) {

            dx = -35f * movement;

        } else if ("tiltRight".equals(pose)) {

            dx = 28f * movement;
            dy = 15f * movement;

        } else if ("tiltLeft".equals(pose)) {

            dx = -28f * movement;
            dy = 15f * movement;

        } else if ("down".equals(pose)) {

            dy = 32f * movement;

        } else if ("up".equals(pose)) {

            dy = -25f * movement;
        }

        headX += dx;
        headY += dy;

        /*
         * Движение плеч
         */

        float shoulderMovement = 0f;

        if ("shoulders".equals(pose)) {

            shoulderMovement = -25f * movement;

        } else if ("back".equals(pose)) {

            shoulderMovement = 18f * movement;
        }

        /*
         * Рисуем тело
         */

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(24f);

        canvas.drawLine(
                centerX,
                shoulderY,
                centerX,
                bodyBottom,
                paint
        );

        /*
         * Голова
         */

        paint.setStyle(Paint.Style.FILL);

        canvas.drawCircle(
                headX,
                headY,
                42f,
                paint
        );

        /*
         * Шея
         */

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(18f);

        canvas.drawLine(
                centerX,
                shoulderY - 5f,
                headX,
                headY + 32f,
                paint
        );

        /*
         * Плечи
         */

        float leftShoulderX = centerX - 85f;
        float rightShoulderX = centerX + 85f;

        float leftShoulderY = shoulderY + shoulderMovement;
        float rightShoulderY = shoulderY + shoulderMovement;

        paint.setStrokeWidth(20f);

        canvas.drawLine(
                centerX,
                shoulderY,
                leftShoulderX,
                leftShoulderY + 65f,
                paint
        );

        canvas.drawLine(
                centerX,
                shoulderY,
                rightShoulderX,
                rightShoulderY + 65f,
                paint
        );

        /*
         * Упражнение "лопатки"
         */

        if ("scapula".equals(pose)) {

            float backMovement = 25f * movement;

            canvas.drawLine(
                    centerX - 20f,
                    shoulderY,
                    centerX - 45f - backMovement,
                    shoulderY + 45f,
                    paint
            );

            canvas.drawLine(
                    centerX + 20f,
                    shoulderY,
                    centerX + 45f + backMovement,
                    shoulderY + 45f,
                    paint
            );
        }

        /*
         * Ноги
         */

        paint.setStrokeWidth(22f);

        canvas.drawLine(
                centerX,
                bodyBottom,
                centerX - 60f,
                height * 0.90f,
                paint
        );

        canvas.drawLine(
                centerX,
                bodyBottom,
                centerX + 60f,
                height * 0.90f,
                paint
        );

        /*
         * Стрелки направления движения.
         */

        drawDirection(
                canvas,
                headX,
                headY
        );
    }

    private void drawDirection(
            Canvas canvas,
            float x,
            float y
    ) {

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(7f);

        float arrowMove =
                8f + animationValue * 12f;

        if ("right".equals(pose)) {

            drawArrow(
                    canvas,
                    x + 50f,
                    y,
                    x + 90f + arrowMove,
                    y
            );

        } else if ("left".equals(pose)) {

            drawArrow(
                    canvas,
                    x - 50f,
                    y,
                    x - 90f - arrowMove,
                    y
            );

        } else if ("tiltRight".equals(pose)) {

            drawArrow(
                    canvas,
                    x + 40f,
                    y + 5f,
                    x + 75f,
                    y + 40f + arrowMove
            );

        } else if ("tiltLeft".equals(pose)) {

            drawArrow(
                    canvas,
                    x - 40f,
                    y + 5f,
                    x - 75f,
                    y + 40f + arrowMove
            );

        } else if ("down".equals(pose)) {

            drawArrow(
                    canvas,
                    x,
                    y + 50f,
                    x,
                    y + 90f + arrowMove
            );

        } else if ("up".equals(pose)) {

            drawArrow(
                    canvas,
                    x,
                    y - 50f,
                    x,
                    y - 90f - arrowMove
            );
        }
    }

    private void drawArrow(
            Canvas canvas,
            float x1,
            float y1,
            float x2,
            float y2
    ) {

        canvas.drawLine(
                x1,
                y1,
                x2,
                y2,
                paint
        );

        float angle =
                (float) Math.atan2(
                        y2 - y1,
                        x2 - x1
                );

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

        if (animator != null) {
            animator.cancel();
        }

        super.onDetachedFromWindow();
    }
            }
