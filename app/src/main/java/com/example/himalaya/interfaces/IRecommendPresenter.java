package com.example.himalaya.interfaces;

/**
 * @author Tian
 * @description 推荐页的逻辑层
 * @date :2020/4/17 11:44
 */
public interface IRecommendPresenter {

    //获取推荐内容
    void getRecommendList();

    //下拉刷新
    void pullRefreshMore();

    //上拉加载更多
    void loadMore();

    //用于注册UI的回调实现类
    void registerViewCallback(IRecommendViewCallback callback);

    //取消UI的回调注册
    void unRegisterViewCallback(IRecommendViewCallback callback);

}

