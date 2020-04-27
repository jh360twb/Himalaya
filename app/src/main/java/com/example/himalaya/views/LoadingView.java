package com.example.himalaya.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.example.himalaya.R;

import androidx.annotation.Nullable;

/**
 * @author Tian
 * @description 加载动画
 * @date :2020/4/19 13:20
 */
@SuppressLint("AppCompatCustomView")
public class LoadingView extends ImageView{

    private int rotateDegree = 0;

    private boolean mNeedRotate = false;

    public LoadingView(Context context) {
        this(context,null);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //设置图标
        setImageResource(R.mipmap.loading);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mNeedRotate = true;
        //绑定到window
        post(new Runnable() {
            @Override
            public void run() {
               rotateDegree += 30;
               rotateDegree = rotateDegree > 360 ? 0 : rotateDegree;
               //调用onDraw
               invalidate();
                if (mNeedRotate) {
                    //延时,100毫秒调用一次
                    postDelayed(this,100);
                }
            }
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        //从window中解绑
        mNeedRotate = false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //第一个参数是旋转角度,第二个是旋转中心
        canvas.rotate(rotateDegree,getWidth()/2,getHeight()/2);
        super.onDraw(canvas);
    }
}
