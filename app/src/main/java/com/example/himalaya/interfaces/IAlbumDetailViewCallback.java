package com.example.himalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

/**
 * @author Tian
 * @description
 * @date :2020/4/19 16:37
 */
public interface IAlbumDetailViewCallback {
    /**
     * 加载列表
     * @param tracks
     */
    void onDetailListLoaded(List<Track> tracks);


    /**
     * 把album传给UI使用
     */
    void onAlbumLoaded(Album album);

    //网络错误
    void onNetworkError(int i, String s);

    void onLoading();

}
