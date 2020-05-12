package com.example.himalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

/**
 * @author Tian
 * @description
 * @date :2020/5/10 15:39
 */
public interface IHistoryCallback {
    /**
     * 加载列表
     * @param tracks
     */
    void onHistoriesLoaded(List<Track> tracks);

}
