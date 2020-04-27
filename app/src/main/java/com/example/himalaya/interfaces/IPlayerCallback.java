package com.example.himalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import java.util.List;

/**
 * @author Tian
 * @description
 * @date :2020/4/21 16:06
 */
public interface IPlayerCallback {
    /**
     * 开始播放
     */
    void onPlayStart();

    /**
     * 播放暂停
     */
    void onPlayPause();

    /**
     * 播放停止
     */
    void onPlayStop();

    /**
     *播放错误
     */
    void onPlayError();

    /**
     * 下一首
     */
    void onNextPlay(Track track);

    /**
     * 上一首
     */
    void onPrePlay(Track track);

    /**
     * 播放器列表数据
     * @param list
     */
    void onListLoaded(List<Track> list);

    /**
     * 播放器模式
     * @param mode
     */
    void onPlayModeChange(XmPlayListControl.PlayMode mode);

    /**
     * 进度条改变
     * @param currentProgress
     * @param total
     */
    void onProgressChange(long currentProgress,long total);

    /**
     * 广告加载中
     */
    void onAdLoading();

    /**
     * 广告加载完成
     */
    void onAdFinished();

    /**
     *更新当前Track
     */
    void onTrackUpdate(Track track,int index);


}
