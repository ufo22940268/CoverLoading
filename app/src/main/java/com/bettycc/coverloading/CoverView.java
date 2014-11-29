package com.bettycc.coverloading;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;

/**
 * Created by ccheng on 11/27/14.
 */
public class CoverView extends ImageView {

    public static final int SHADOW_COLOR = 0xaa000000;
    private int mHeight;
    private int mWidth;
    private Bitmap bitmap;
    private Canvas tempCanvas;
    private Paint transparentPaint;
    private float mOuterCircleRadius;
    private float mInnerCircleRadius;
    private float mPauseCircleRadius;
    private int mArcStart;
    private ValueAnimator mRotateAnimator;
    private float mPauseIconHeight;
    private float mPauseIconWidth;
    private float mPauseIconGap;
    private boolean mPausing;
    private ValueAnimator mPauseAnimator;
    private float mPauseMaxCircleRadius;
    private ValueAnimator mResumeAnimator;
    private boolean mStart;
    private float mInitOuterCircleRadius;
    private ValueAnimator mFinishAnimator;

    public CoverView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }


    public void startLoading() {
        if (!mRotateAnimator.isRunning() && !mFinishAnimator.isRunning()) {
            resetValues();
            mRotateAnimator.start();
            mPausing = false;
        }
    }

    private void init(Context context, AttributeSet attrs) {
        resetValues();

        mRotateAnimator = ValueAnimator.ofInt(-90, 270);
        mRotateAnimator.setDuration(10000);
        mRotateAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mRotateAnimator.addUpdateListener(mRotateListener);
        mRotateAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                mStart = true;
            }

            @Override
            public void onAnimationEnd(Animator animator) {
//                mStart = false;
                mFinishAnimator.start();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        mPauseAnimator = ValueAnimator.ofFloat(0.001f, 1);
        mPauseAnimator.setDuration(1000);
        mPauseAnimator.addUpdateListener(mPauseUpdateListener);
        mPauseAnimator.addListener(mPauseListener);

        mResumeAnimator = ValueAnimator.ofFloat(1, 0.001f);
        mResumeAnimator.setDuration(1000);
        mResumeAnimator.addUpdateListener(mResumeUpdateListener);
        mResumeAnimator.addListener(mResumeListener);

        mFinishAnimator = getFinishAnimator();
    }

    private ValueAnimator getFinishAnimator() {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(mInitOuterCircleRadius, mInitOuterCircleRadius * 2);
        valueAnimator.setDuration(1000);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mOuterCircleRadius = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                invalidate();
            }
        });

        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                mPausing = false;
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                mStart = false;
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        return valueAnimator;
    }

    private void resetValues() {
        mInitOuterCircleRadius = getResources().getDimension(R.dimen.outer_circle_radius);
        mOuterCircleRadius = mInitOuterCircleRadius;
        mInnerCircleRadius = getResources().getDimension(R.dimen.inner_circle_radius);
        mPauseMaxCircleRadius = mInnerCircleRadius * 0.7f;
        mPauseCircleRadius = mPauseMaxCircleRadius;
        mPauseIconHeight = getResources().getDimension(R.dimen.pause_icon_height);
        mPauseIconWidth = getResources().getDimension(R.dimen.pause_icon_width);
        mPauseIconGap = getResources().getDimension(R.dimen.pause_icon_gap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (!mStart) {
            return;
        }

        Paint redPaint = new Paint();
        redPaint.setColor(Color.RED);

        Paint greenPaint = new Paint();
        greenPaint.setColor(Color.GREEN);

        bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        bitmap.eraseColor(Color.TRANSPARENT);
        tempCanvas = new Canvas(bitmap);

        transparentPaint = new Paint();
        transparentPaint.setColor(getResources().getColor(android.R.color.transparent));
        transparentPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        transparentPaint.setAntiAlias(true);

        mHeight = getHeight();
        mWidth = getWidth();

        tempCanvas.drawColor(SHADOW_COLOR);
        int cx = mWidth / 2;
        int cy = mHeight / 2;
        tempCanvas.drawCircle(cx, cy, mOuterCircleRadius, transparentPaint);

        Paint shadowPaint = new Paint();
        shadowPaint.setAntiAlias(true);
        shadowPaint.setColor(SHADOW_COLOR);
        RectF rectF = new RectF(cx - mInnerCircleRadius,
                cy - mInnerCircleRadius,
                cx + mInnerCircleRadius,
                cy + mInnerCircleRadius);
        tempCanvas.drawArc(rectF,
                mArcStart,
                270 - mArcStart,
                true,
                shadowPaint);
        canvas.drawBitmap(bitmap, 0, 0, null);

        /**
         * Draw pause icon.
         */
        if (mPausing && mPauseCircleRadius*2 > 1) {
            tempCanvas.drawCircle(cx, cy, mPauseCircleRadius, transparentPaint);

            System.out.println("mPauseCircleRadius = " + mPauseCircleRadius);
            Bitmap pauseBitmap = Bitmap.createBitmap((int) (mPauseCircleRadius * 2), (int) (mPauseCircleRadius * 2), Bitmap.Config.ARGB_8888);
            Canvas pauseCanvas = new Canvas(pauseBitmap);

            pauseCanvas.drawCircle(mPauseCircleRadius, mPauseCircleRadius, mPauseCircleRadius, shadowPaint);

            Paint gp1 = new Paint(transparentPaint);
            gp1.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

            //Draw pause1.
            int pcx = (int) (mPauseCircleRadius - mPauseIconGap / 2 - mPauseIconWidth / 2);
            int pcy = (int) (mPauseCircleRadius);

            RectF pause1 = new RectF();
            pause1.left = pcx - mPauseIconWidth / 2;
            pause1.right = pcx + mPauseIconWidth / 2;
            pause1.top = pcy - mPauseIconHeight / 2;
            pause1.bottom = pcy + mPauseIconHeight / 2;
            pauseCanvas.drawRect(pause1, gp1);

            //Draw pause2.
            int pcx2 = (int) (mPauseCircleRadius + mPauseIconGap / 2 + mPauseIconWidth / 2);
            int pcy2 = (int) (mPauseCircleRadius);

            RectF pause2 = new RectF();
            pause2.left = pcx2 - mPauseIconWidth / 2;
            pause2.right = pcx2 + mPauseIconWidth / 2;
            pause2.top = pcy2 - mPauseIconHeight / 2;
            pause2.bottom = pcy2 + mPauseIconHeight / 2;
            pauseCanvas.drawRect(pause2, gp1);

            canvas.drawBitmap(pauseBitmap, cx - mPauseCircleRadius, cx - mPauseCircleRadius, null);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (mStart) {
                if (mPausing) {
                    resumeLoading();
                } else {
                    pauseLoading();
                }
            }
            return true;
        }

        return true;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void pauseLoading() {
        if (!mResumeAnimator.isRunning() && !mPauseAnimator.isRunning()) {
            mPausing = true;
            mPauseAnimator.start();
            mRotateAnimator.pause();
        }
    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void resumeLoading() {
        if (!mPauseAnimator.isRunning() && !mResumeAnimator.isRunning()) {
            mPausing = true;
            mPauseAnimator.cancel();
            mResumeAnimator.start();
            mRotateAnimator.resume();
        }
    }

    private ValueAnimator.AnimatorUpdateListener mResumeUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            mPauseCircleRadius = mPauseMaxCircleRadius * ((Float) valueAnimator.getAnimatedValue()).floatValue();
            invalidate();
        }
    };

    private Animator.AnimatorListener mPauseListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {
            mPausing = true;
        }

        @Override
        public void onAnimationEnd(Animator animation) {

        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    };

    private ValueAnimator.AnimatorUpdateListener mRotateListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            mArcStart = ((Integer) animation.getAnimatedValue()).intValue();
            invalidate();
        }
    };

    private ValueAnimator.AnimatorUpdateListener mPauseUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            mPauseCircleRadius = mPauseMaxCircleRadius * ((Float) animation.getAnimatedValue()).floatValue();
            invalidate();
        }
    };

    private Animator.AnimatorListener mResumeListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animator) {

        }

        @Override
        public void onAnimationEnd(Animator animator) {
            mPausing = false;
        }

        @Override
        public void onAnimationCancel(Animator animator) {

        }

        @Override
        public void onAnimationRepeat(Animator animator) {

        }
    };
}
