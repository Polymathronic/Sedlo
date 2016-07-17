package com.aprilbrother.blueduino;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

public class CanvasView extends View {

    public int width;
    public int height;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Path mPath;
    Context context;
    private Paint gPaint;
    private Paint yPaint;
    private Paint rPaint;
    private Paint bPaint;
    private float mX, mY;
    private static final float TOLERANCE = 5;
    private boolean touched = false;

    public CanvasView(Context c, AttributeSet attrs) {
        super(c, attrs);
        context = c;

        // we set a new Path
        mPath = new Path();

        // and we set new Paints with the desired attributes
        gPaint = new Paint();
        yPaint = new Paint();
        rPaint = new Paint();
        // This one is only for the background
        bPaint = new Paint();

        // Green
        gPaint.setAntiAlias(true);
        gPaint.setColor(Color.GREEN);
        gPaint.setStyle(Paint.Style.FILL);
        gPaint.setStrokeJoin(Paint.Join.ROUND);
        gPaint.setStrokeWidth(360);
        gPaint.setMaskFilter(new BlurMaskFilter(60, BlurMaskFilter.Blur.NORMAL));

        // Yellow
        yPaint.setAntiAlias(true);
        yPaint.setColor(Color.YELLOW);
        yPaint.setStyle(Paint.Style.FILL);
        yPaint.setStrokeJoin(Paint.Join.ROUND);
        yPaint.setStrokeWidth(240);
        yPaint.setMaskFilter(new BlurMaskFilter(60, BlurMaskFilter.Blur.NORMAL));
        
        // Red
        rPaint.setAntiAlias(true);
        rPaint.setColor(Color.RED);
        rPaint.setStyle(Paint.Style.FILL);
        rPaint.setStrokeJoin(Paint.Join.ROUND);
        rPaint.setStrokeWidth(120);
        rPaint.setMaskFilter(new BlurMaskFilter(60, BlurMaskFilter.Blur.NORMAL));

        // Background (seat sensors)
        bPaint.setColor(Color.BLUE);
    }

    // override onSizeChanged
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;

        // your Canvas will draw onto the defined Bitmap
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }

    // override onDraw
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawLine (width/2, 0, width/2, height, bPaint);
        canvas.drawLine (0, height/2, width, height/2, bPaint);

        // draw the mPath with the gPaint on the canvas when onDraw
        if(touched) {
            canvas.drawCircle(mX, mY, 200, gPaint);
            canvas.drawCircle(mX, mY, 150, yPaint);
            canvas.drawCircle(mX, mY, 75, rPaint);
        }

        touched = true;
    }

    // when ACTION_DOWN start touch according to the x,y values
    public void startTouch(float x, float y) {
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
        invalidate();
    }

    public void clearCanvas() {
        mPath.reset();
        invalidate();
    }

    // when ACTION_UP stop touch
    private void upTouch() {
        mPath.lineTo(mX, mY);
        invalidate();
    }

    //override the onTouchEvent
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startTouch(x, y);
                break;
            case MotionEvent.ACTION_UP:
                upTouch();
                break;
        }
        return true;
    }
}