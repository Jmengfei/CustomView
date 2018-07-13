package com.example.jmf.timetest;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.jmf.timetest.utils.SizeUtils;

import java.util.Calendar;

/**
 * Created by 贾梦飞 on 2018/7/11 10:05.
 * QQ:821176301
 * 微信：j821176301
 * desc：表盘
 */
public class WatchBoard extends View {

    private float mRadius; // 圆形半径
    private float mPadding; // 边距
    private float mTextSize; // 文字大小
    private float mHourPointWidth; // 时针宽度
    private float mMinutePointWidth; // 分针宽度
    private float mSecondPointWidth; // 秒针宽度
    private float mPointRadius;   // 指针圆角
    private float mPointEndLength; // 指针末尾长度

    private int mHourPointColor;  // 时针的颜色
    private int mMinutePointColor;  // 分针的颜色
    private int mSecondPointColor;  // 秒针的颜色
    private int mColorLong;    // 长线的颜色
    private int mColorShort;   // 短线的颜色

    private Paint mPaint; // 画笔
    private PaintFlagsDrawFilter mDrawFilter; // 为画布设置抗锯齿

    private int width; // 钟表的边长

    public WatchBoard(Context context) {
        this(context, null);
    }

    public WatchBoard(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WatchBoard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // 获取属性
        obtainStyledAttrs(attrs);
        //初始化画笔
        initPaint();
        // 为画布实现抗锯齿
        mDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

        //测量手机的宽度
        int widthPixels = context.getResources().getDisplayMetrics().widthPixels;
        int heightPixels = context.getResources().getDisplayMetrics().heightPixels;

        // 默认和屏幕的宽高最小值相等
        width = Math.min(widthPixels, heightPixels);
    }


    private void initPaint() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
    }

    /**
     * @param attrs
     */
    private void obtainStyledAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.WatchBoard);
        mPadding = typedArray.getDimension(R.styleable.WatchBoard_wb_padding, DptoPx(10));
        mTextSize = typedArray.getDimension(R.styleable.WatchBoard_wb_text_size, SptoPx(16));
        mHourPointWidth = typedArray.getDimension(R.styleable.WatchBoard_wb_hour_pointer_width, DptoPx(5));
        mMinutePointWidth = typedArray.getDimension(R.styleable.WatchBoard_wb_minute_pointer_width, DptoPx(3));
        mSecondPointWidth = typedArray.getDimension(R.styleable.WatchBoard_wb_second_pointer_width, DptoPx(2));
        mPointRadius = typedArray.getDimension(R.styleable.WatchBoard_wb_pointer_corner_radius, DptoPx(10));
        mPointEndLength = typedArray.getDimension(R.styleable.WatchBoard_wb_pointer_end_length, DptoPx(10));

        mHourPointColor = typedArray.getColor(R.styleable.WatchBoard_wb_hour_pointer_color, Color.BLACK);
        mMinutePointColor = typedArray.getColor(R.styleable.WatchBoard_wb_minute_pointer_color, Color.BLACK);
        mSecondPointColor = typedArray.getColor(R.styleable.WatchBoard_wb_second_pointer_color, Color.RED);
        mColorLong = typedArray.getColor(R.styleable.WatchBoard_wb_scale_long_color, Color.argb(225, 0, 0, 0));
        mColorShort = typedArray.getColor(R.styleable.WatchBoard_wb_scale_short_color, Color.argb(125, 0, 0, 0));

        // 一定要回收
        typedArray.recycle();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mRadius = (Math.min(w, h) - mPadding) / 2;
        mPointEndLength = mRadius / 6; // 设置成半径的六分之一
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
//        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
//        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
//        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
//
//        if (widthMode == MeasureSpec.AT_MOST || widthMode == MeasureSpec.UNSPECIFIED ||
//                heightMode == MeasureSpec.AT_MOST || heightMode == MeasureSpec.UNSPECIFIED)
//            try {
//                throw new NoDetermineSizeException("宽度高度至少有一个确定的值,不能同时为wrap_content");
//            } catch (NoDetermineSizeException e) {
//                e.printStackTrace();
//            }
//        else { // 至少有一个是确定值
//            if (widthMode == MeasureSpec.EXACTLY) {
//                width = Math.min(widthSize, width);
//            }
//
//            if (heightMode == MeasureSpec.EXACTLY) {
//                width = Math.min(heightSize, width);
//            }
//        }
        int i = measureSize(widthMeasureSpec);
        int j = measureSize(heightMeasureSpec);
        Log.e("TAG", "WatchBoard onMeasure()i+++j ==" + i + "+++" + j);
        setMeasuredDimension(i, j);
    }

    // 测量宽高和屏幕作对比
    private int measureSize(int measureSpec) {
        int size = MeasureSpec.getSize(measureSpec);
        width = Math.min(width, size);
        Log.e("TAG", "WatchBoard measureSize() width == " + width);
        return width;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.setDrawFilter(mDrawFilter);
        // 绘制半径圆
        drawCircle(canvas);
        // 绘制刻度尺
        printScale(canvas);
        // 绘制指针
        printPointer(canvas);
        // 每一秒刷新一次
        postInvalidateDelayed(1000);
    }

    private void printPointer(Canvas canvas) {
        Calendar calendar = Calendar.getInstance();

        int hour = calendar.get(Calendar.HOUR);// 时
        int minute = calendar.get(Calendar.MINUTE);// 分
        int second = calendar.get(Calendar.SECOND);// 秒
        // 转过的角度
        float angleHour = (hour + (float) minute / 60) * 360 / 12;
        float angleMinute = (minute + (float) second / 60) * 360 / 60;
        int angleSecond = second * 360 / 60;

        // 绘制时针
        canvas.save();
        canvas.rotate(angleHour,width/2,width/2); // 旋转到时针的角度
        RectF rectHour = new RectF(width/2 -mHourPointWidth / 2, width/2 -mRadius * 3 / 5, width/2 +mHourPointWidth / 2, width/2 + mPointEndLength);
        mPaint.setColor(mHourPointColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mHourPointWidth);
        canvas.drawRoundRect(rectHour, mPointRadius, mPointRadius, mPaint);
        canvas.restore();
        // 绘制分针
        canvas.save();
        canvas.rotate(angleMinute,width/2,width/2); // 旋转到分针的角度
        RectF rectMinute = new RectF(width/2-mMinutePointWidth / 2, width/2-mRadius * 3.5f / 5, width/2+mMinutePointWidth / 2, width/2+mPointEndLength);
        mPaint.setColor(mMinutePointColor);
        mPaint.setStrokeWidth(mMinutePointWidth);
        canvas.drawRoundRect(rectMinute, mPointRadius, mPointRadius, mPaint);
        canvas.restore();
        // 绘制分针
        canvas.save();
        canvas.rotate(angleSecond,width/2,width/2); // 旋转到分针的角度
        RectF rectSecond = new RectF(width/2-mSecondPointWidth / 2, width/2-mRadius + DptoPx(10), width/2+mSecondPointWidth / 2, width/2+mPointEndLength);
        mPaint.setStrokeWidth(mSecondPointWidth);
        mPaint.setColor(mSecondPointColor);
        canvas.drawRoundRect(rectSecond, mPointRadius, mPointRadius, mPaint);
        canvas.restore();

        // 绘制原点
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(width/2, width/2, mSecondPointWidth * 4, mPaint);
    }

    private void printScale(Canvas canvas) {
        mPaint.setStrokeWidth(SizeUtils.Dp2Px(getContext(), 1));
        int lineWidth;
        for (int i = 0; i < 60; i++) {
            if (i % 5 == 0) {
                mPaint.setStrokeWidth(SizeUtils.Dp2Px(getContext(), 1.5f));
                mPaint.setColor(mColorLong);
                lineWidth = 40;
                mPaint.setTextSize(mTextSize);
                String text = ((i / 5) == 0 ? 12 : (i / 5)) + "";
                Rect textBound = new Rect();
                mPaint.getTextBounds(text, 0, text.length(), textBound);
                mPaint.setColor(Color.BLACK);
                canvas.drawText(text, width / 2 - textBound.width() / 2, textBound.height() + DptoPx(5) + lineWidth + mPadding, mPaint);

            } else {
                lineWidth = 30;
                mPaint.setColor(mColorShort);
                mPaint.setStrokeWidth(SizeUtils.Dp2Px(getContext(), 1));
            }
            canvas.drawLine(width / 2, mPadding, width / 2, mPadding + lineWidth, mPaint);
            canvas.rotate(6, width / 2, width / 2);
        }
    }

    private void drawCircle(Canvas canvas) {
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(width / 2, width / 2, mRadius, mPaint);
    }

    private float SptoPx(int value) {
        return SizeUtils.Sp2Px(getContext(), value);
    }

    private float DptoPx(int value) {
        return SizeUtils.Dp2Px(getContext(), value);
    }


}
