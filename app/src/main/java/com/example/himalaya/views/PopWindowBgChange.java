package com.example.himalaya.views;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.view.Window;
import android.view.WindowManager;

/**
 * @author Tian
 * @description
 * @date :2020/5/3 10:34
 */
public class PopWindowBgChange extends Activity {
    public static ValueAnimator mEnterBgAnimator;
    public static ValueAnimator mOutBgAnimator;

    //背景的渐变动画
    public static void initBgAnimation(final Window window) {
        //进入
        mEnterBgAnimator = ValueAnimator.ofFloat(1.0f,0.7f);
        mEnterBgAnimator.setDuration(300);
        mEnterBgAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //LogUtil.e(TAG,"value -> " + animation.getAnimatedValue());
                //背景透明度变化
                upDateBgAlpha((Float) animation.getAnimatedValue(),window);
            }
        });
        //退出
        mOutBgAnimator = ValueAnimator.ofFloat(0.7f,1.0f);
        mOutBgAnimator.setDuration(300);
        mOutBgAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                upDateBgAlpha((Float) animation.getAnimatedValue(),window);
            }
        });
    }

    private static void upDateBgAlpha(float alpha,Window window) {
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.alpha = alpha;
        window.setAttributes(attributes);
    }
}
