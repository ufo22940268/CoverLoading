package com.bettycc.coverloading;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

/**
 * Created by ccheng on 11/27/14.
 */
public class CoverView extends ImageView {

    public static final int SHADOW_COLOR = 0x77000000;
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
    private ValueAnimator mPauseStartAnimator;
    private float mPauseMaxCircleRadius;

    public CoverView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private ValueAnimator.AnimatorUpdateListener mRotateListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            mArcStart = ((Integer) animation.getAnimatedValue()).intValue();
            invalidate();
        }
    };

    private ValueAnimator.AnimatorUpdateListener mPauseStartUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            mPauseCircleRadius = mPauseMaxCircleRadius * ((Float) animation.getAnimatedValue()).floatValue();
            invalidate();
        }
    };

    public void startLoading() {
        mRotateAnimator.start();
    }

    private void init(Context context, AttributeSet attrs) {
        mOuterCircleRadius = getResources().getDimension(R.dimen.outer_circle_radius);
        mInnerCircleRadius = getResources().getDimension(R.dimen.inner_circle_radius);
        mPauseMaxCircleRadius = mInnerCircleRadius * 0.7f;
        mPauseCircleRadius = mPauseMaxCircleRadius;
        mPauseIconHeight = getResources().getDimension(R.dimen.pause_icon_height);
        mPauseIconWidth = getResources().getDimension(R.dimen.pause_icon_width);
        mPauseIconGap = getResources().getDimension(R.dimen.pause_icon_gap);

        mRotateAnimator = ValueAnimator.ofInt(-90, 270);
        mRotateAnimator.setDuration(3000);
        mRotateAnimator.setInterpolator(new DecelerateInterpolator());
        mRotateAnimator.addUpdateListener(mRotateListener);

        mRotateAnimator = ValueAnimator.ofInt(-90, 270);
        mRotateAnimator.setDuration(3000);
        mRotateAnimator.setInterpolator(new DecelerateInterpolator());
        mRotateAnimator.addUpdateListener(mRotateListener);

        mPauseStartAnimator = ValueAnimator.ofFloat(1, 0);
        mPauseStartAnimator.setDuration(1000);
        mPauseStartAnimator.addUpdateListener(mPauseStartUpdateListener);
        mPauseStartAnimator.addListener(new Animator.AnimatorListener() {
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
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

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
        if (mPausing && mPauseCircleRadius > 0) {
            tempCanvas.drawCircle(cx, cy, mPauseCircleRadius, transparentPaint);

            Bitmap pauseBitmap = Bitmap.createBitmap((int) mPauseCircleRadius * 2, (int) mPauseCircleRadius * 2, Bitmap.Config.ARGB_8888);
            Canvas pauseCanvas = new Canvas(pauseBitmap);

            pauseCanvas.drawCircle(mPauseCircleRadius, mPauseCircleRadius, mPauseCircleRadius, shadowPaint);

            Paint gp1 = new Paint(greenPaint);
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

    public void pauseLoading() {
        mPausing = true;
        mPauseStartAnimator.start();
    }
}
