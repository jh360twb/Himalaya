package com.example.himalaya.interfaces;

import com.example.himalaya.base.IBasePresenter;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

/**
 * @author Tian
 * @description
 * @date :2020/4/21 15:52
 */
public interface IPlayerPresenter extends IBasePresenter<IPlayerCallback> {
    /**
     * 播放
     */
    void play();

    /**
     * 暂停
     */
    void pause();

    /**
     * 停止播放
     */
    void stop();

    /**
     * 上一首
     */
    void playPre();

    /**
     * 播放下一首
     */
    void playNext();

    /**
     * 切换播放模式
     *
     * @param mode
     */
    void switchPlayMode(XmPlayListControl.PlayMode mode);


    /**
     * 获取播放列表
     */
    void getPlayList();


    /**
     * 根据节目的位置进行播放
     *
     * @param index 节目在列表中的位置
     */
    void playByIndex(int index);

    /**
     * 切换播放进度
     *
     * @param progress
     */
    void seekTo(int progress);

    /**
     * 是否正在播放
     */
    boolean isPlaying();

    /**
     * 把播放器列表内容翻转
     */
    void reversePlayList();

    /**
     * 播放专辑第一首
     * @param id
     */
    void playByAlbumId(long id);


}
