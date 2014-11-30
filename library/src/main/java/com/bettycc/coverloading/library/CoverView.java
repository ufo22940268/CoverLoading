package com.bettycc.coverloading.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ValueAnimator;

/**
 * Created by ccheng on 11/27/14.
 */
public class CoverView extends ImageView {

    public static final int DEFAULT_SHADOW_COLOR = 0xaa000000;
    public int SHADOW_COLOR = DEFAULT_SHADOW_COLOR;
    public static final int ROTATE_DURATION = 300;
    private static final int MAX_PROGRESS = 100;
    private int mHeight;
    private int mWidth;
    private Bitmap bitmap;
    private Canvas tempCanvas;
    private Paint transparentPaint;
    private float mOuterCircleRadius = -1;
    private float mInnerCircleRadius = -1;
    private float mPauseCircleRadius = -1;
    private float mPauseMaxCircleRadius = -1;
    private float mCornerRadius = -1;
    private int mArcStart;
    private ValueAnimator mRotateAnimator;
    private float mPauseIconHeight;
    private float mPauseIconWidth;
    private float mPauseIconGap;
    private boolean mPausing;
    private ValueAnimator mPauseAnimator;
    private ValueAnimator mResumeAnimator;
    private boolean mStart = false;
    private float mInitOuterCircleRadius;
    private int mProgress;
    private int mPendingProgress;
    private OnPauseResumeListener mOnPauseResumeListener;

    public CoverView(Context context) {
        super(context);
        init(context, null);
    }

    public CoverView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }


    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CoverView);
            int color = typedArray.getColor(R.styleable.CoverView_background, DEFAULT_SHADOW_COLOR);
            mCornerRadius = typedArray.getDimension(R.styleable.CoverView_corner_radius, -1);
            typedArray.recycle();
        }

        resetValues();

        mPauseAnimator = ValueAnimator.ofFloat(0.001f, 1);
        int duration = getResources().getInteger(android.R.integer.config_mediumAnimTime);
        mPauseAnimator.setDuration(duration);
        mPauseAnimator.addUpdateListener(mPauseUpdateListener);
        mPauseAnimator.addListener(mPauseListener);
        mPauseAnimator.setInterpolator(new DecelerateInterpolator());

        mResumeAnimator = ValueAnimator.ofFloat(1, 0.001f);
        mResumeAnimator.setDuration(duration);
        mResumeAnimator.addUpdateListener(mResumeUpdateListener);
        mResumeAnimator.addListener(mResumeListener);
        mResumeAnimator.setInterpolator(new AccelerateInterpolator());

    }

    private ValueAnimator getRotateAnimator(int prevProgress, int progress) {
        ValueAnimator rotateAnimator = ValueAnimator.ofInt(progressToDegress(prevProgress),
                progressToDegress(progress));
        rotateAnimator.setDuration(ROTATE_DURATION);
        rotateAnimator.addUpdateListener(mRotateListener);
        rotateAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                mStart = true;
                mPausing = false;
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                handlePendingProgress();

                if (isFinished()) {
                    getFinishAnimator().start();
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        return rotateAnimator;
    }

    private void handlePendingProgress() {
        if (mPendingProgress != mProgress && mPendingProgress > mProgress) {
            setProgress(mPendingProgress);
        }
    }

    private int progressToDegress(float progress) {
        return (int) (360 * (progress / MAX_PROGRESS) - 90);
    }

    private ValueAnimator getFinishAnimator() {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(mInitOuterCircleRadius, mInitOuterCircleRadius * 2);
        valueAnimator.setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime));
        valueAnimator.setInterpolator(new AccelerateInterpolator());
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

    public void resetValues() {
        mProgress = 0;
        mPendingProgress = 0;

        mOuterCircleRadius = -1;
        mInnerCircleRadius = -1;
        mPauseCircleRadius = -1;
        mPauseMaxCircleRadius = -1;
    }

    private void initSizes(int width, int height) {
        mPauseIconHeight = 45f / 200 * width;
        mPauseIconWidth = 10f / 200 * width;
        mPauseIconGap = 10f / 200 * width;

        mInitOuterCircleRadius = 90f / 200 * width;

        if (mInnerCircleRadius == -1) {
            mInnerCircleRadius = 80f / 200 * width;
        }

        if (mCornerRadius == -1) {
            mCornerRadius = 20f / 200 * width;
        }

        if (mPauseMaxCircleRadius == -1) {
            mPauseMaxCircleRadius = mInnerCircleRadius * 0.7f;
        }

        if (mOuterCircleRadius == -1) {
            mOuterCircleRadius = mInitOuterCircleRadius;
        }

        if (mPauseCircleRadius == -1) {
            mPauseCircleRadius = mPauseMaxCircleRadius;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        initSizes(getWidth(), getHeight());

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
        if (mCornerRadius != 0) {
            Path path = new Path();
            path.addRoundRect(new RectF(0, 0, getWidth(), getHeight()), mCornerRadius, mCornerRadius, Path.Direction.CCW);
            canvas.clipPath(path);
        }
        canvas.drawBitmap(bitmap, 0, 0, null);

        /**
         * Draw pause icon.
         */
        if (mPausing && mPauseCircleRadius * 2 > 1) {
            tempCanvas.drawCircle(cx, cy, mPauseCircleRadius, transparentPaint);

            Bitmap pauseBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            Canvas pauseCanvas = new Canvas(pauseBitmap);

            pauseCanvas.drawCircle(cx, cy, mPauseCircleRadius, shadowPaint);

            Paint gp1 = new Paint(transparentPaint);
            gp1.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

            //Draw pause1.
            int pcx = (int) (cx - mPauseIconGap / 2 - mPauseIconWidth / 2);
            int pcy = (int) (cy);

            RectF pause1 = new RectF();
            pause1.left = pcx - mPauseIconWidth / 2;
            pause1.right = pcx + mPauseIconWidth / 2;
            pause1.top = pcy - mPauseIconHeight / 2;
            pause1.bottom = pcy + mPauseIconHeight / 2;
            pauseCanvas.drawRect(pause1, gp1);

            //Draw pause2.
            int pcx2 = (int) (cx + mPauseIconGap / 2 + mPauseIconWidth / 2);
            int pcy2 = (int) (cy);

            RectF pause2 = new RectF();
            pause2.left = pcx2 - mPauseIconWidth / 2;
            pause2.right = pcx2 + mPauseIconWidth / 2;
            pause2.top = pcy2 - mPauseIconHeight / 2;
            pause2.bottom = pcy2 + mPauseIconHeight / 2;
            pauseCanvas.drawRect(pause2, gp1);

            canvas.drawBitmap(pauseBitmap, 0, 0, null);
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

    public void pauseLoading() {
        if (!mResumeAnimator.isRunning() && !mPauseAnimator.isRunning()) {
            mPausing = true;
            mPauseAnimator.start();
        }
    }


    public void resumeLoading() {
        if (!mPauseAnimator.isRunning() && !mResumeAnimator.isRunning()) {
            mPausing = true;
            mPauseAnimator.cancel();
            mResumeAnimator.start();
            handlePendingProgress();
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
            float v = ((Float) animation.getAnimatedValue()).floatValue();
            System.out.println("v = " + v);
            mPauseCircleRadius = mPauseMaxCircleRadius * v;
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


    public void setProgress(int p) {
        if (p > MAX_PROGRESS) {
            p = MAX_PROGRESS;
        }

        if (p < mProgress) {
            throw new IllegalArgumentException();
        }

        if ((mRotateAnimator != null && mRotateAnimator.isRunning()) || mPausing) {
            mPendingProgress = p;
            return;
        }

        int prevProgress = mProgress;
        mProgress = p;

        if (mRotateAnimator != null) {
            mRotateAnimator.cancel();
        }

        mRotateAnimator = getRotateAnimator(prevProgress, p);
        mRotateAnimator.start();
    }

    public int getProgress() {
        return mProgress;
    }

    public boolean isFinished() {
        return getProgress() == MAX_PROGRESS;
    }

    public OnPauseResumeListener getOnPauseResumeListener() {
        return mOnPauseResumeListener;
    }

    public void setOnPauseResumeListener(OnPauseResumeListener onPauseResumeListener) {
        mOnPauseResumeListener = onPauseResumeListener;
    }

    public interface OnPauseResumeListener {
        public void onPause();

        public void onResume();
    }
}
