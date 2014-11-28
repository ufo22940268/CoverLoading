package com.bettycc.coverloading;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Shader;
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

    public void startLoading() {
        mRotateAnimator.start();
    }

    private void init(Context context, AttributeSet attrs) {
        mOuterCircleRadius = getResources().getDimension(R.dimen.outer_circle_radius);
        mInnerCircleRadius = getResources().getDimension(R.dimen.inner_circle_radius);
        mPauseCircleRadius = mInnerCircleRadius * 0.2f;
        mPauseIconHeight = getResources().getDimension(R.dimen.pause_icon_height);
        mPauseIconWidth = getResources().getDimension(R.dimen.pause_icon_width);

        mRotateAnimator = ValueAnimator.ofInt(-90, 270);
        mRotateAnimator.setDuration(3000);
        mRotateAnimator.setInterpolator(new DecelerateInterpolator());
        mRotateAnimator.addUpdateListener(mRotateListener);
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

        //Draw pause icon.
        tempCanvas.drawCircle(cx, cy, mPauseCircleRadius, transparentPaint);

        Bitmap pauseBitmap = Bitmap.createBitmap((int) mPauseCircleRadius * 2, (int) mPauseCircleRadius * 2, Bitmap.Config.ARGB_8888);
        Canvas pauseCanvas = new Canvas(pauseBitmap);

        int pcx = (int) (mPauseCircleRadius);
        int pcy = (int) (mPauseCircleRadius);

        pauseCanvas.drawCircle(pcx, pcy, mPauseCircleRadius, shadowPaint);

        //Draw pause1.
        RectF pause1 = new RectF();
        pause1.left = pcx - mPauseIconWidth / 2;
        pause1.right = pcx + mPauseIconWidth / 2;
        pause1.top = pcx - mPauseIconHeight / 2;
        pause1.bottom = pcx + mPauseIconHeight / 2;
        Paint gp1 = new Paint(greenPaint);
        gp1.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        pauseCanvas.drawRect(pause1, gp1);

        canvas.drawBitmap(bitmap, 0, 0, null);
        canvas.drawBitmap(pauseBitmap, cx - mPauseCircleRadius, cx - mPauseCircleRadius, null);
    }
}
