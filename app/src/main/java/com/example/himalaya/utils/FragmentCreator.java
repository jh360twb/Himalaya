package com.example.himalaya.utils;

import android.annotation.SuppressLint;

import com.example.himalaya.base.BaseFragment;
import com.example.himalaya.fragments.HistoryFragment;
import com.example.himalaya.fragments.RecommendFragment;
import com.example.himalaya.fragments.SubscriptionFragment;

import java.util.HashMap;
import java.util.Map;

public class FragmentCreator {
    //缓存,不会重复加载
    @SuppressLint("UseSparseArrays")
    private static Map<Integer, BaseFragment> sCache = new HashMap<>();
    public static final int INDEX_RECOMMEND = 0;
    public static final int INDEX_SUBSCRIPTION = 1;
    public static final int INDEX_HISTORY = 2;
    //有几个fragment
    public static final int PAGE_COUNT = 3;
    private static final String TAG = "FragmentCreator";

    public static BaseFragment getFragment(int index) {
        LogUtil.e(TAG,"sCache.size -> "+sCache.size());
        BaseFragment baseFragment = sCache.get(index);

        if (baseFragment != null) {
            return baseFragment;
        }

        switch (index) {
            case INDEX_RECOMMEND:
                baseFragment = new RecommendFragment();
                break;
            case INDEX_SUBSCRIPTION:
                baseFragment = new SubscriptionFragment();
                break;
            case INDEX_HISTORY:
                baseFragment = new HistoryFragment();
                break;
        }

        sCache.put(index, baseFragment);

        return baseFragment;
    }

}
