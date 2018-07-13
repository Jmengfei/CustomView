package com.example.jmf.timetest;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.Calendar;
import java.util.TimeZone;


/**
 * Created by 贾梦飞 on 2018/7/10 9:54.
 * QQ:821176301
 * 微信：j821176301
 * desc：
 */
public class AnalogClock extends View {
    // 记录当前时间
    private Calendar mCalendar;

    // 用来存放三张图片资源
    private Drawable mDial;
    private Drawable mMinuteHand;
    private Drawable mHourHand;

    // 得到表盘的大小，dp为单位
    private int mDialWidth;
    private int mDialHeight;

    //用来跟踪我们的View 的尺寸的变化，
    //当发生尺寸变化时，我们在绘制自己
    //时要进行适当的缩放。
    private boolean mChange;

    // 分钟和小时
    private int mHour;
    private int mMinute;

    // 记录是否被加载或者剥离
    private boolean mAttached;

    private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            // 当时区发生改变时
            if (intent.getAction().equals(Intent.ACTION_TIMEZONE_CHANGED)) {
                String tz = intent.getStringExtra("time-zone");
                mCalendar = Calendar.getInstance(TimeZone.getTimeZone(tz));
            }

            Log.e("TAG", "AnalogClock onReceive() 时间");
            // 进行时间更新
            onTimeChanged();

            // 进行页面的重绘
            invalidate();
        }
    };


    public AnalogClock(Context context) {
        this(context, null);
    }

    public AnalogClock(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnalogClock(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AnalogClock(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        Resources resources = context.getResources();
        if (mDial == null) {
            mDial = context.getDrawable(R.drawable.clock_dial);
        }

        if (mMinuteHand == null) {
            mMinuteHand = context.getDrawable(R.drawable.clock_hand_minute);
        }

        if (mHourHand == null) {
            mHourHand = context.getDrawable(R.drawable.clock_hand_hour);
        }

        mCalendar = Calendar.getInstance();

        mDialWidth = mDial.getIntrinsicWidth();
        mDialHeight = mDial.getIntrinsicHeight();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mChange = true;
    }

    private void onTimeChanged() {
        long time = System.currentTimeMillis();
        mCalendar.setTimeInMillis(time);
        mHour = mCalendar.get(Calendar.HOUR);
        mMinute = mCalendar.get(Calendar.MINUTE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        float hScale = 1.0f;
        float vScale = 1.0f;

        if (widthMode != MeasureSpec.UNSPECIFIED && widthSize < mDialWidth) {
            hScale = (float) widthSize / (float) mDialWidth;
        }
        if (heightMode != MeasureSpec.UNSPECIFIED && heightSize < mDialHeight) {
            vScale = (float) heightSize / (float) mDialHeight;
        }

        float scale = Math.min(hScale, vScale);
        setMeasuredDimension(
                resolveSizeAndState((int) (mDialWidth * scale), widthMeasureSpec, 0),
                resolveSizeAndState((int) (mDialHeight * scale), heightMeasureSpec, 0)
        );
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        boolean change = mChange;
        if (change) {
            mChange = false;
        }

        // 找到底部图片中心位置
        int availableWidth = super.getRight() - super.getLeft();
        int availableHeight = super.getBottom() - super.getTop();
        int x = availableWidth / 2;
        int y = availableHeight / 2;

        Drawable dial = mDial;
        int w = dial.getIntrinsicWidth();
        int h = dial.getIntrinsicHeight();
        boolean scaled = false;

        if (availableHeight < h || availableWidth < w) {
            scaled = true;
            float scale = Math.min((float) availableWidth / (float) w, (float) availableHeight / (float) h);
            canvas.save();
            canvas.scale(scale, scale, x, y);
        }

        if (change) {
            dial.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y + (h / 2));
        }
        dial.draw(canvas);
        canvas.save();
        canvas.rotate(mHour / 12.0f * 360.0f, x, y);

        Drawable hourHand =  mHourHand;
        if (change) {
            w = hourHand.getIntrinsicWidth();
            h = hourHand.getIntrinsicHeight();

            hourHand.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y + (h / 2));
        }

        hourHand.draw(canvas);
        canvas.restore();
        canvas.save();

        // 根据分时针的坐标系
        canvas.rotate(mMinute / 60.0f * 360.0f, x, y);
        Drawable minuteHand = mMinuteHand;
        if (change) {
            w = minuteHand.getIntrinsicWidth();
            h = minuteHand.getIntrinsicHeight();
            minuteHand.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y + (h / 2));
        }

        minuteHand.draw(canvas);
        canvas.restore();

        if(scaled) {
            canvas.restore();
        }

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!mAttached) {
            mAttached = true;
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_TIME_TICK);
            filter.addAction(Intent.ACTION_TIME_CHANGED);
            filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
            getContext().registerReceiver(mIntentReceiver, filter);
        }

        mCalendar = Calendar.getInstance();
        onTimeChanged();

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mAttached) {
            getContext().unregisterReceiver(mIntentReceiver);
            mAttached = false;
        }
    }
}
