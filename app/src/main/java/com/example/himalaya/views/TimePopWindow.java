package com.example.himalaya.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.himalaya.R;
import com.example.himalaya.base.BaseApplication;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;

/**
 * @author Tian
 * @description
 * @date :2020/5/14 23:39
 */
public class TimePopWindow extends PopupWindow {

    private final View mTimePop;
    private TextView mTimeListClose;
    private RadioGroup mTimeGroup;
    Context mContext = BaseApplication.getAppContext();
    private static final int ten = 10*1000*60;
    private static final int twe = 20*1000*60;
    private static final int thr = 30*1000*60;
    private static final int fou = 45*1000*60;
    private static final int sixty = 60*1000*60;

    public TimePopWindow() {
        super(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setOutsideTouchable(true);
        setFocusable(true);
        mTimePop = LayoutInflater.from(BaseApplication.getAppContext()).inflate(R.layout.pop_time_list, null);
        setContentView(mTimePop);
        setAnimationStyle(R.style.pop_animation);
        initView();
        initEvent();

    }

    private void initView() {
        mTimeListClose = mTimePop.findViewById(R.id.time_list_close_btn);
        mTimeGroup = mTimePop.findViewById(R.id.time_group);
    }

    private void initEvent() {
        mTimeListClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        mTimeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio_0:
                        Toast.makeText(mContext, "取消定时关闭", Toast.LENGTH_SHORT).show();
                        XmPlayerManager.getInstance(mContext).pausePlayInMillis(0);
                        break;
                    case R.id.radio_10:
                        Toast.makeText(mContext, "10分钟之后关闭", Toast.LENGTH_SHORT).show();
                        XmPlayerManager.getInstance(mContext).pausePlayInMillis(System.currentTimeMillis()+ten);
                        break;
                    case R.id.radio_20:
                        Toast.makeText(mContext, "20分钟之后关闭", Toast.LENGTH_SHORT).show();
                        XmPlayerManager.getInstance(mContext).pausePlayInMillis(System.currentTimeMillis()+twe);

                        break;
                    case R.id.radio_30:
                        Toast.makeText(mContext, "30分钟之后关闭", Toast.LENGTH_SHORT).show();
                        XmPlayerManager.getInstance(mContext).pausePlayInMillis(System.currentTimeMillis()+thr);

                        break;
                    case R.id.radio_45:
                        Toast.makeText(mContext, "45分钟之后关闭", Toast.LENGTH_SHORT).show();
                        XmPlayerManager.getInstance(mContext).pausePlayInMillis(System.currentTimeMillis()+fou);

                        break;
                    case R.id.radio_60:
                        Toast.makeText(mContext, "60分钟之后关闭", Toast.LENGTH_SHORT).show();
                        XmPlayerManager.getInstance(mContext).pausePlayInMillis(System.currentTimeMillis()+sixty);
                        break;
                }
            }
        });
    }
}
