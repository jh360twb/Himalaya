package com.example.himalaya.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import com.example.himalaya.MainActivity;
import com.example.himalaya.R;

import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

public class IndicatorAdapter extends CommonNavigatorAdapter {

    private final String[] titles;
    private OnIndicatorTabClickListener mOnIndicatorTabClickListener;

    public IndicatorAdapter(Context context) {
        titles = context.getResources().getStringArray(R.array.indicator_title);
    }

    @Override
    public int getCount() {
        if (titles != null) {
            return titles.length;
        }
        return 0;
    }

    @Override
    public IPagerTitleView getTitleView(Context context, final int index) {
        //创建View
        ColorTransitionPagerTitleView colorTransitionPagerTitleView = new
                ColorTransitionPagerTitleView(context);
        //设置一般情况为灰色
        colorTransitionPagerTitleView.setNormalColor(Color.parseColor("#aaffffff"));
        //设置选中颜色为黑色
        colorTransitionPagerTitleView.setSelectedColor(Color.parseColor("#ffffff"));
        colorTransitionPagerTitleView.setTextSize(18);
        colorTransitionPagerTitleView.setText(titles[index]);
        colorTransitionPagerTitleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击事件
                if (mOnIndicatorTabClickListener != null) {
                    mOnIndicatorTabClickListener.onTabClick(index);
                }
            }
        });
        return colorTransitionPagerTitleView;
    }

    @Override
    public IPagerIndicator getIndicator(Context context) {
        LinePagerIndicator indicator = new LinePagerIndicator(context);
        indicator.setMode(LinePagerIndicator.MODE_WRAP_CONTENT);
        indicator.setColors(Color.parseColor("#ffffff"));
        return indicator;
    }

    public void setOnIndicatorTabClickListener(OnIndicatorTabClickListener listener){
        this.mOnIndicatorTabClickListener = listener;
    }

    public interface OnIndicatorTabClickListener{
        void onTabClick(int index);
    }

}
