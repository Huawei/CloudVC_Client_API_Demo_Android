package com.huawei.opensdk.ec_sdk_demo.widget;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.huawei.opensdk.ec_sdk_demo.R;

/**
 * This class is about Circle progress bar.
 */
public class CircleProgressBar extends ImageView
{
    private static final int SWEEP_INC = 10;

    private final Paint mFramePaint;

    private int maxProgress = 100;

    private int progress = -1;

    private int mSweep = -90;

    private float stokeWidth;
    private float stokeOutWidth;

    /**
     * The Oval.
     */
//A distance zone in which a circle is drawn
    RectF oval;

    /**
     * The Out oval.
     */
//Frame area
    RectF outOval;

    /**
     * The Paint.
     */
    Paint paint;


    /**
     * Instantiates a new Circle progress bar.
     *
     * @param context the context
     * @param attrs   the attrs
     */
    public CircleProgressBar(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        stokeWidth = getResources().getDimension(R.dimen.circle_stoke_width);
        stokeOutWidth = getResources().getDimension(R.dimen.circle_stoke_out);

        paint = new Paint();
        paint.setAntiAlias(true); // Sets the brush to antialiasing
        paint.setStyle(Style.STROKE);

        mFramePaint = new Paint();
        mFramePaint.setAntiAlias(true);
        mFramePaint.setStyle(Style.STROKE);
        mFramePaint.setStrokeWidth(0);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        canvas.drawColor(Color.TRANSPARENT); // transparent

        prepareRecF();

        //Draw a circular translucent background
        drawCircle(canvas);

        //Draw outer box
        paint.setStyle(Style.STROKE);
        paint.setStrokeWidth(stokeOutWidth); //line width
        paint.setColor(getResources().getColor(R.color.gray));
//        canvas.drawRect(outOval, mFramePaint);
        canvas.drawArc(outOval, -90, 360, false, paint);

        paint.setStrokeWidth(stokeWidth); //line width
        paint.setColor(Color.WHITE); // setpc
//        canvas.drawRect(oval, mFramePaint);
        // Draw a white circle, that is, the progress bar background
        canvas.drawArc(oval, -90, 360, false, paint);

        if (progress <= 0)
        {
            paint.setColor(getResources().getColor(R.color.gray));
            canvas.drawArc(oval, mSweep, 60, false, paint);

            mSweep += SWEEP_INC;
            if (mSweep >= 360)
            {
                mSweep -= 360;
            }
            invalidate();
        }
        else
        {
            mSweep = -90;

            paint.setColor(getResources().getColor(R.color.main_conf_item_red));
            // Draw the progress arc, here is green
            canvas.drawArc(oval, -90, ((float)progress / maxProgress) * 360, false, paint);
        }
    }

    private void drawCircle(Canvas canvas)
    {
        float width = this.getWidth();

        paint.setStyle(Style.FILL);
        paint.setColor(getResources().getColor(R.color.half_transparent));
        canvas.drawCircle(outOval.centerX(), outOval.centerY(), width/2, paint);
    }

    private void prepareRecF()
    {
        int width = this.getWidth();
        int height = this.getHeight();

        if (width != height)
        {
            int min = Math.min(width, height);
            width = min;
            height = min;
        }

        if (outOval == null)
        {
            outOval = new RectF(stokeOutWidth/2, stokeOutWidth/2,
                width - stokeOutWidth/2, height - stokeOutWidth/2);
        }

        float mWidth = (stokeWidth + stokeOutWidth) / 2;
        if (oval == null)
        {
            oval = new RectF(mWidth, mWidth, width - mWidth, height - mWidth);
        }


    }

    /**
     * Gets max progress.
     *
     * @return the max progress
     */
    public int getMaxProgress()
    {
        return maxProgress;
    }

    /**
     * Sets max progress.
     *
     * @param maxProgress the max progress
     */
    public void setMaxProgress(int maxProgress)
    {
        this.maxProgress = maxProgress;
    }

    /**
     * Sets progress.
     *
     * @param progress the progress
     */
    public void setProgress(int progress)
    {
        this.progress = progress;
        this.invalidate();
    }

    /**
     * Non UI thread calls
     *
     * @param progress the progress
     */
    public void setProgressNotInUiThread(int progress)
    {
        this.progress = progress;
        this.postInvalidate();
    }

}
