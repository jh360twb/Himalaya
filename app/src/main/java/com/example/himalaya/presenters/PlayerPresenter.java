package com.example.himalaya.presenters;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.example.himalaya.base.BaseApplication;
import com.example.himalaya.interfaces.IPlayerCallback;
import com.example.himalaya.interfaces.IPlayerPresenter;
import com.example.himalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.advertis.Advertis;
import com.ximalaya.ting.android.opensdk.model.advertis.AdvertisList;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.advertis.IXmAdsStatusListener;
import com.ximalaya.ting.android.opensdk.player.constants.PlayerConstants;
import com.ximalaya.ting.android.opensdk.player.service.IXmPlayerStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException;

import java.util.ArrayList;
import java.util.List;

import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_LIST;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_LIST_LOOP;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_RANDOM;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_SINGLE_LOOP;

/**
 * @author Tian
 * @description 播放器
 * @date :2020/4/21 17:15
 */
public class PlayerPresenter implements IPlayerPresenter, IXmAdsStatusListener, IXmPlayerStatusListener {

    private XmPlayerManager mPlayerManager;
    private static final String TAG = "PlayerPresenter";
    private List<IPlayerCallback> mCallbacks = new ArrayList<>();
    private Track mTrack;
    private int mTrackIndex;
    private int mCurrentIndex;
    private final SharedPreferences mPlayModeSp;
    public static final String PLAY_MODE_SP_NAME = "PlayModeSp";
    public static final String PLAY_MODE_SP_KEY = "CurrentPlayMode";
    private XmPlayListControl.PlayMode mCurrentPlayMode = PLAY_MODEL_LIST;

    private static final int PLAY_MODEL_SINGLE_LOOP_INT = 0;
    private static final int PLAY_MODEL_LIST_INT = 1;
    private static final int PLAY_MODEL_LIST_LOOP_INT = 2;
    private static final int PLAY_MODEL_RANDOM_INT = 3;



    private PlayerPresenter() {
        mPlayerManager = XmPlayerManager.getInstance(BaseApplication.getAppContext());
        //广告物料
        mPlayerManager.addAdsStatusListener(this);
        //播放器的状态变化
        mPlayerManager.addPlayerStatusListener(this);
        //记录当前的播放模式
        mPlayModeSp = BaseApplication.getAppContext().getSharedPreferences(PLAY_MODE_SP_NAME, Context.MODE_PRIVATE);

    }

    private static PlayerPresenter sInstance = null;

    public static PlayerPresenter getInstance() {
        if (sInstance == null) {
            synchronized (PlayerPresenter.class) {
                if (sInstance == null) {
                    sInstance = new PlayerPresenter();
                }
            }
        }
        return sInstance;
    }

    private boolean isPlayListSet = false;

    public void setPlayList(List<Track> list, int playIndex) {
        if (mPlayerManager != null) {
            mPlayerManager.setPlayList(list, playIndex);
            isPlayListSet = true;
            List<Track> playList = mPlayerManager.getPlayList();
            mTrack = playList.get(playIndex);
            mTrackIndex = playIndex;
        } else {
            LogUtil.e(TAG, "mPlayerManager is null");
        }
    }

    @Override
    public void play() {
        //LogUtil.e(TAG,"isPlayListSet -> "+isPlayListSet+"");
        if (isPlayListSet) {
            mPlayerManager.play();
        }

    }

    @Override
    public void pause() {
        if (mPlayerManager != null) {
            mPlayerManager.pause();
        }

    }

    @Override
    public void stop() {

    }

    //上一首
    @Override
    public void playPre() {
        if (mPlayerManager != null) {
            if (mPlayerManager.hasPreSound()) {
                mPlayerManager.playPre();
            } else {
                BaseApplication.getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(BaseApplication.getAppContext(), "这是专辑的第一首啦", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    //下一首
    @Override
    public void playNext() {
        if (mPlayerManager != null) {
            if (mPlayerManager.hasNextSound()) {
                mPlayerManager.playNext();
            } else {
                BaseApplication.getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(BaseApplication.getAppContext(), "这是专辑的最后一首啦", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    //播放模式
    @Override
    public void switchPlayMode(XmPlayListControl.PlayMode mode) {
        if (mPlayerManager != null) {
            mCurrentPlayMode = mode;
            mPlayerManager.setPlayMode(mode);
            for (IPlayerCallback callback : mCallbacks) {
                callback.onPlayModeChange(mode);
            }
            SharedPreferences.Editor edit = mPlayModeSp.edit();
            edit.putInt(PLAY_MODE_SP_KEY,getIntByMode(mode));
            edit.commit();
        }
    }

    private int getIntByMode(XmPlayListControl.PlayMode mode) {
        switch (mode){
            case PLAY_MODEL_LIST:
                return PLAY_MODEL_LIST_INT;
            case PLAY_MODEL_LIST_LOOP:
                return PLAY_MODEL_LIST_LOOP_INT;
            case PLAY_MODEL_SINGLE_LOOP:
                return PLAY_MODEL_SINGLE_LOOP_INT;
            case PLAY_MODEL_RANDOM:
                return PLAY_MODEL_RANDOM_INT;
        }
        return PLAY_MODEL_LIST_INT;
    }

    private XmPlayListControl.PlayMode getModeByInt(int index) {
        switch(index) {
            case PLAY_MODEL_SINGLE_LOOP_INT:
                return PLAY_MODEL_SINGLE_LOOP;
            case PLAY_MODEL_LIST_LOOP_INT:
                return PLAY_MODEL_LIST_LOOP;
            case PLAY_MODEL_RANDOM_INT:
                return PLAY_MODEL_RANDOM;
            case PLAY_MODEL_LIST_INT:
                return PLAY_MODEL_LIST;
        }
        return PLAY_MODEL_LIST;
    }

    @Override
    public void getPlayList() {
        if (mPlayerManager != null) {
            List<Track> playList = mPlayerManager.getPlayList();
            for (IPlayerCallback callback : mCallbacks) {
                callback.onListLoaded(playList);
            }
        }
    }

    //序号播放歌曲
    @Override
    public void playByIndex(int index) {
        if (mPlayerManager != null) {
            //会调用onSoundSwitch方法
            mPlayerManager.play(index);
        }
    }

    //进度条
    @Override
    public void seekTo(int progress) {
        if (mPlayerManager != null) {
            mPlayerManager.seekTo(progress);
        }
    }

    @Override
    public boolean isPlaying() {
        return mPlayerManager.isPlaying();
    }


    @Override
    public void registerViewCallback(IPlayerCallback iPlayerCallback) {
        //LogUtil.e(TAG,"registerViewCallback ->");

        if (mCallbacks != null && !mCallbacks.contains(iPlayerCallback)) {
            mCallbacks.add(iPlayerCallback);
        }
        //更新界面
        for (IPlayerCallback callback : mCallbacks) {
            callback.onTrackUpdate(mTrack,mTrackIndex);
        }
        //更新PlayMode
        int modeIndex = mPlayModeSp.getInt(PLAY_MODE_SP_KEY, PLAY_MODEL_LIST_INT);
        mCurrentPlayMode = getModeByInt(modeIndex);
        for (IPlayerCallback callback : mCallbacks) {
            callback.onPlayModeChange(mCurrentPlayMode);
        }
    }

    @Override
    public void unRegisterViewCallback(IPlayerCallback iPlayerCallback) {
        if (mCallbacks != null) {
            mCallbacks.remove(iPlayerCallback);
        }
    }

    //======================广告相关的回调start========================

    @Override
    public void onStartGetAdsInfo() {
       // LogUtil.e(TAG, "onStartGetAdsInfo");
    }

    @Override
    public void onGetAdsInfo(AdvertisList advertisList) {
        //LogUtil.e(TAG, "onGetAdsInfo");

    }

    @Override
    public void onAdsStartBuffering() {
        //LogUtil.e(TAG, "onAdsStartBuffering");

    }

    @Override
    public void onAdsStopBuffering() {
        //LogUtil.e(TAG, "onAdsStopBuffering");

    }

    @Override
    public void onStartPlayAds(Advertis advertis, int i) {
        //LogUtil.e(TAG, "onStartPlayAds");

    }

    @Override
    public void onCompletePlayAds() {
        //LogUtil.e(TAG, "onCompletePlayAds");

    }

    @Override
    public void onError(int what, int extra) {
        //LogUtil.e(TAG, "onError" + what + " " + extra);

    }
    //======================广告相关的回调end========================
    //
    //======================播放器相关的回调start========================

    @Override
    public void onPlayStart() {
        //LogUtil.e(TAG, "onPlayStart");
        for (IPlayerCallback callback : mCallbacks) {
            callback.onPlayStart();
        }
    }

    @Override
    public void onPlayPause() {
        //LogUtil.e(TAG, "onPlayPause");
        for (IPlayerCallback callback : mCallbacks) {
            callback.onPlayPause();
        }
    }

    @Override
    public void onPlayStop() {

    }

    @Override
    public void onSoundPlayComplete() {

    }

    //准备好了才开始播放
    @Override
    public void onSoundPrepared() {
        //设置播放模式
        mPlayerManager.setPlayMode(mCurrentPlayMode);
        if (mPlayerManager.getPlayerStatus() == PlayerConstants.STATE_PREPARED) {
            mPlayerManager.play();
        }
    }

    /**
     * 切换歌曲才会调用
     *
     * @param lastModel 上一首模式
     * @param curModel 当前模式
     */
    @Override
    public void onSoundSwitch(PlayableModel lastModel, PlayableModel curModel) {
        LogUtil.e(TAG,"onSoundSwitch->");
        //属于Track
        mCurrentIndex = mPlayerManager.getCurrentIndex();
        if (curModel instanceof Track) {
            Track track = (Track) curModel;
            for (IPlayerCallback callback : mCallbacks) {
                callback.onTrackUpdate(track, mCurrentIndex);
            }
        }
    }

    @Override
    public void onBufferingStart() {

    }

    @Override
    public void onBufferingStop() {

    }

    @Override
    public void onBufferProgress(int progress) {
        // LogUtil.e(TAG, "progress ->" + progress);
    }

    @Override
    public void onPlayProgress(int currPos, int duration) {
        for (IPlayerCallback callback : mCallbacks) {
            callback.onProgressChange(currPos, duration);
        }
        //LogUtil.e(TAG, currPos + " " + duration);
    }

    @Override
    public boolean onError(XmPlayerException e) {
        return false;
    }

    //======================播放器相关的回调end========================

}