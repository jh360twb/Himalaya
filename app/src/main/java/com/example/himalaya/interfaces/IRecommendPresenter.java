package com.example.himalaya.interfaces;

import com.example.himalaya.base.IBasePresenter;

/**
 * @author Tian
 * @description 推荐页的逻辑层
 * @date :2020/4/17 11:44
 */
public interface IRecommendPresenter extends IBasePresenter<IRecommendViewCallback> {

    //获取推荐内容
    void getRecommendList();

    //下拉刷新
    void pullRefreshMore();

    //上拉加载更多
    void loadMore();


}

