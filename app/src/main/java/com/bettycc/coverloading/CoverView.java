package com.bettycc.coverloading;

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
import android.view.animation.AccelerateInterpolator;
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
    private int mSweep;
    private ValueAnimator mRotateAnimator;

    public CoverView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }
    private ValueAnimator.AnimatorUpdateListener mRotateListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            mSweep = ((Integer) animation.getAnimatedValue()).intValue();
            invalidate();
        }
    };

    public void startLoading() {
        mRotateAnimator.start();
    }

    private void init(Context context, AttributeSet attrs) {
        mOuterCircleRadius = getResources().getDimension(R.dimen.outer_circle_radius);
        mInnerCircleRadius = getResources().getDimension(R.dimen.inner_circle_radius);

        mRotateAnimator = ValueAnimator.ofInt(0, 360);
        mRotateAnimator.setDuration(3000);
        mRotateAnimator.setInterpolator(new DecelerateInterpolator());
        mRotateAnimator.addUpdateListener(mRotateListener);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

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
        shadowPaint.setColor(SHADOW_COLOR);
        RectF rectF = new RectF(cx - mInnerCircleRadius,
                cy - mInnerCircleRadius,
                cx + mInnerCircleRadius,
                cy + mInnerCircleRadius);
        tempCanvas.drawArc(rectF,
                -90,
                mSweep,
                true,
                shadowPaint);

        canvas.drawBitmap(bitmap, 0, 0, null);
    }
}
