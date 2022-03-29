package me.xcyoung.iphone.view;

import android.content.Context;

public class Utils {
    public static float dp2px(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return dp * density;
    }
}
