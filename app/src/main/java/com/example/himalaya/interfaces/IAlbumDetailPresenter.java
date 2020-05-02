package com.example.himalaya.interfaces;

import com.example.himalaya.base.IBasePresenter;

/**
 * @author Tian
 * @description
 * @date :2020/4/19 16:19
 */
public interface IAlbumDetailPresenter extends IBasePresenter<IAlbumDetailViewCallback> {

    //下拉刷新
    void refresh();

    //上拉加载更多
    void loadMore();

    /**
     * 获取专辑详情
     * @param albumId
     * @param page
     */
    void getAlbumDetail(long albumId, int page);

}
