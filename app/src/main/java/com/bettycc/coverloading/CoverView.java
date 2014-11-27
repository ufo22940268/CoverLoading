package com.bettycc.coverloading;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by ccheng on 11/27/14.
 */
public class CoverView extends ImageView {

    public static final int SHADOW_COLOR = 0x77000000;
    private int mHeight;
    private int mWidth;
    private Bitmap bitmap;
    private Canvas temp;
    private Paint transparentPaint;

    public CoverView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        bitmap.eraseColor(Color.TRANSPARENT);
        temp = new Canvas(bitmap);

        transparentPaint = new Paint();
        transparentPaint.setColor(getResources().getColor(android.R.color.transparent));
        transparentPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        transparentPaint.setAntiAlias(true);

        mHeight = getHeight();
        mWidth = getWidth();

        temp.drawColor(SHADOW_COLOR);
        temp.drawCircle(mWidth/2, mHeight/2, 200, transparentPaint);
        canvas.drawBitmap(bitmap, 0, 0, null);
    }
}
