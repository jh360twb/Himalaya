package com.example.himalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.List;

/**
 * @author Tian
 * @description
 * @date :2020/4/17 11:48
 */
public interface IRecommendViewCallback {

    //获取推荐内容的结果
    void onRecommendListLoaded(List<Album> result);

    //网络错误
    void onNetworkError();

    //数据为空
    void onEmpty();

    //正在加载
    void onLoading();

}
