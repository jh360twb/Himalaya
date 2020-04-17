package com.example.himalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.List;

/**
 * @author Tian
 * @description
 * @date :2020/4/17 11:48
 */
public interface IRecommendViewCallback {

    /**
     * 获取推荐内容的结果
     * @param result
     */
    void onRecommendListLoaded(List<Album> result);

    //加载更多
    void onLoaderMore(List<Album> result);

    //下拉刷新
    void OnRefreshMore(List<Album> result);


}
