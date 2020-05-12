package com.example.himalaya.data;

import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

/**
 * @author Tian
 * @description
 * @date :2020/5/9 23:43
 */
public interface IHistoryDaoCallback {
    /**
     * 添加的结果回调方法
     * @param isSuccess
     */
    void onAddResult(boolean isSuccess);

    /**
     * 删除的结果回调
     * @param isSuccess
     */
    void onDelResult(boolean isSuccess);

    /**
     * 加载的结果
     * @param tracks
     */
    void onHisListLoaded(List<Track> tracks);

    /**
     * 清除历史
     * @param isSuccess
     */
    void onHistoriesClean(boolean isSuccess);
}
