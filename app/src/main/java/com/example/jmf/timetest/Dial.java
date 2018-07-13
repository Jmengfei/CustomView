package com.example.jmf.timetest;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by 贾梦飞 on 2018/7/10 16:48.
 * QQ:821176301
 * 微信：j821176301
 * desc：表盘
 */

public class Dial extends View {
    private static final int HOUR_LINE_HEIGHT = 35;
    private static final int MINUTE_LINE_HEIGHT = 25;
    // 圆环的宽度
    private int mCircleLineWidth;
    private int mHourLineWidth;
    private int mMinuteLineWidth;

    // 时针长度
    private int mHourLineHeight;
    // 分针长度
    private int mMinuteLineHeight;

    // 用于控制刻度线位置
    private int mFixLineHeight;

    private PaintFlagsDrawFilter mDrawFilter;

    // 圆形画笔
    private Paint mCirclePaint;
    // 线的画笔
    private Paint mLinePaint;

    //圆心（表盘中心）
    private int mCenterX, mCenterY, mCenterRadius;

    // 刻度线的左、上位置
    private int mLineLeft, mLineTop;
    private int mLineBottom;

    public Dial(Context context) {
        this(context, null);
    }

    public Dial(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Dial(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // 为canvas画布设置抗锯齿
        mDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        // 转化为标准的单位，dip --> px
        mCircleLineWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
        mHourLineWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
        mMinuteLineWidth = mHourLineWidth / 2;

        mFixLineHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4,
                getResources().getDisplayMetrics());

        mHourLineHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                HOUR_LINE_HEIGHT,
                getResources().getDisplayMetrics());
        mMinuteLineHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                MINUTE_LINE_HEIGHT,
                getResources().getDisplayMetrics());

        initPaint();
    }

    private void initPaint() {
        // 定义抗锯齿画笔
        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setColor(Color.RED);
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mCirclePaint.setStrokeWidth(mCircleLineWidth);

        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setColor(Color.RED);
        mLinePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mLinePaint.setStrokeWidth(mHourLineWidth);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 为画布设置抗锯齿
        canvas.setDrawFilter(mDrawFilter);
        super.onDraw(canvas);

        // 绘制表盘
        drawCircle(canvas);

        // 绘制刻度
        drawLines(canvas);

    }

    private void drawLines(Canvas canvas) {
        canvas.save();
        for (int i = 0; i < 60;i++){
            if(i % 5 == 0) { // 整点
                mLinePaint.setStrokeWidth(mHourLineWidth);
                mLineBottom = mLineTop + mHourLineHeight;
            }else{
                mLineBottom = mLineTop + mMinuteLineHeight;
                mLinePaint.setStrokeWidth(mMinuteLineWidth);
            }
            canvas.drawLine(mLineLeft,mLineTop,mLineLeft,mLineBottom,mLinePaint);
            canvas.rotate(6,mCenterX,mCenterY);
        }
        canvas.restore();
    }

    private void drawCircle(Canvas canvas) {
        canvas.drawCircle(mCenterX, mCenterY, mCenterRadius, mCirclePaint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mCenterX = w / 2;
        mCenterY = h / 2;

        mCenterRadius = Math.min(mCenterX, mCenterY) - mCircleLineWidth / 2;

        mLineLeft = mCenterX ;
        mLineTop = mCenterY - mCenterRadius;

    }
}
