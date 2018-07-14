package com.huawei.opensdk.ec_sdk_demo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.ColorRes;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.huawei.opensdk.ec_sdk_demo.R;


/**
 * This class is about relativelayout with Bottom Line
 * 带底线的RelativeLayout
 */
public class BottomLineLayout extends RelativeLayout
{
    static final int SHOW_LINE;

    static final int LINE_SIZE;
    static final int LINE_COLOR;
    static final int LINE_DEF_COLOR;

    static final int LINE_PADDING_LEFT;
    static final int LINE_PADDING_RIGHT;

    static
    {
        LINE_SIZE = R.styleable.BottomLineLayout_lineSize;
        LINE_COLOR = R.styleable.BottomLineLayout_lineColor;
        LINE_DEF_COLOR = R.color.textSecondary;

        SHOW_LINE = R.styleable.BottomLineLayout_showLine;

        LINE_PADDING_LEFT = R.styleable.BottomLineLayout_linePaddingLeft;
        LINE_PADDING_RIGHT = R.styleable.BottomLineLayout_linePaddingRight;
    }

    /** 是否显示底线 */
    boolean showLine = true;

    Paint paint;
    float lineSize;
    int lineColor;

    float linePaddingLeft = 0;
    float linePaddingRight = 0;

    public BottomLineLayout(Context context)
    {
        this(context, null);
    }

    public BottomLineLayout(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public BottomLineLayout(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);

        float defSize = context.getResources().getDimension(R.dimen.dp0_5);

        final int[] style = R.styleable.BottomLineLayout;
        final TypedArray a = context.obtainStyledAttributes(attrs, style, 0, 0);

        // 默认显示底线
        showLine = a.getBoolean(SHOW_LINE, true);
        lineSize = a.getDimension(LINE_SIZE, defSize);
        lineColor = a.getColor(LINE_COLOR, getColor(LINE_DEF_COLOR));

        linePaddingLeft = a.getDimension(LINE_PADDING_LEFT, 0);
        linePaddingRight = a.getDimension(LINE_PADDING_RIGHT, 0);

        a.recycle();

        init();
    }

    int getColor(@ColorRes int id)
    {
        return getResources().getColor(id);
    }


    private void init()
    {
        paint = new Paint();
        paint.setAntiAlias(false);
        paint.setColor(lineColor);
        paint.setStrokeWidth(lineSize);
    }

    public void setShowLine(boolean showLine)
    {
        this.showLine = showLine;
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        if (showLine)
        {
            drawLine(canvas);
        }
    }

    void drawLine(Canvas canvas)
    {
        final int width = getWidth();
        final int height = getHeight();

        final float startX = linePaddingLeft;
        final float endX = width - linePaddingRight;

        canvas.drawLine(startX, height - lineSize, endX, height - lineSize, paint);
    }
}
