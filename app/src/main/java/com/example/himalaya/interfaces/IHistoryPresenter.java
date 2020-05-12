package com.example.himalaya.interfaces;

import com.example.himalaya.base.IBasePresenter;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;

/**
 * @author Tian
 * @description
 * @date :2020/5/10 15:39
 */
public interface IHistoryPresenter extends IBasePresenter<IHistoryCallback> {
    /**
     * 添加历史
     *
     * @param track
     */
    void addHistory(Track track);

    /**
     * 删除历史
     *
     * @param track
     */
    void delHistory(Track track);

    /**
     * 获取历史列表
     */
    void getHistoryList();

    /**
     * 清除历史
     */
    void cleanHistories();
}


