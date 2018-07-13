package com.example.jmf.timetest.utils;

import android.content.Context;

/**
 * Created by 贾梦飞 on 2018/7/11 10:40.
 * QQ:821176301
 * 微信：j821176301
 * desc：
 */

public class SizeUtils {

    public static float Dp2Px(Context context, float value) {
        float scale = context.getResources().getDisplayMetrics().density;
        return value * scale ;
    }

    public static float Sp2Px(Context context , float value){
        float scale = context.getResources().getDisplayMetrics().density;
        return value * scale;
    }
}
