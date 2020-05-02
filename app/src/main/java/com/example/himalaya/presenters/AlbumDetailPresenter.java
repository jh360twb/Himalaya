package com.example.himalaya.presenters;

import android.widget.Toast;

import com.example.himalaya.api.XimalayaApi;
import com.example.himalaya.base.BaseApplication;
import com.example.himalaya.interfaces.IAlbumDetailPresenter;
import com.example.himalaya.interfaces.IAlbumDetailViewCallback;
import com.example.himalaya.utils.Constants;
import com.example.himalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Tian
 * @description
 * @date :2020/4/19 16:23
 */
public class AlbumDetailPresenter implements IAlbumDetailPresenter {
    private static AlbumDetailPresenter sInstance = null;
    private Album mTargetAlbum = null;
    List<IAlbumDetailViewCallback> mAlbumCallbacks = new ArrayList<>();
    //track集合
    List<Track> mTracks = new ArrayList<>();
    private static final String TAG = "AlbumDetailPresenter";
    //当前专辑id
    private long mCurrentAlbumId = -1;
    //当前页码
    private int mCurrentPage = 0;
    private boolean isRefresh = false;

    private void AlbumDetailPresenter() {
    }


    public static AlbumDetailPresenter getInstance() {
        if (sInstance == null) {
            synchronized (AlbumDetailPresenter.class) {
                if (sInstance == null) {
                    sInstance = new AlbumDetailPresenter();
                }
            }
        }
        return sInstance;
    }

    @Override
    public void refresh() {
        mCurrentPage = 1;
        mTracks.clear();
        isRefresh = true;
        doLoaded(false, true);
    }

    @Override
    public void loadMore() {
        mCurrentPage++;
        doLoaded(true, false);
    }

    private void doLoaded(final boolean isLoadMore, final boolean isRefresh) {
        XimalayaApi ximalayaApi = XimalayaApi.getInstance();
        ximalayaApi.getAlbumDetail(new IDataCallBack<TrackList>() {
            @Override
            public void onSuccess(TrackList trackList) {
                List<Track> tracks = trackList.getTracks();
                //没有更多数据
                if (tracks.size() == 0) {
                    for (IAlbumDetailViewCallback albumCallback : mAlbumCallbacks) {
                        albumCallback.onLoadMoreFinished(tracks.size());
                    }
                }
                //刷新
                if (!isLoadMore && tracks.size() > 0 && isRefresh) {
                    for (IAlbumDetailViewCallback albumCallback : mAlbumCallbacks) {
                        albumCallback.onRefreshFinished();
                    }
                }
                mTracks.addAll(tracks);
                //加载成功
                if (tracks.size() > 0 && isLoadMore) {
                    for (IAlbumDetailViewCallback albumCallback : mAlbumCallbacks) {
                        albumCallback.onLoadMoreFinished(tracks.size());
                    }
                }
                for (IAlbumDetailViewCallback mAlbumCallback : mAlbumCallbacks) {
                    mAlbumCallback.onDetailListLoaded(mTracks);
                }
                //LogUtil.e(TAG,trackList.getAlbumIntro());
            }

            @Override
            public void onError(int i, String s) {
                //回退
                mCurrentPage--;
                //LogUtil.e(TAG, s);
                for (IAlbumDetailViewCallback mAlbumCallback : mAlbumCallbacks) {
                    mAlbumCallback.onNetworkError(i, s);
                }
            }
        },mCurrentAlbumId,mCurrentPage);
    }


    //获取专辑的所有歌曲,默认为50条
    @Override
    public void getAlbumDetail(long albumId, int page) {
        mTracks.clear();
        mCurrentAlbumId = albumId;
        mCurrentPage = page;
        updateLoading();
        doLoaded(false, false);
    }

    private void updateLoading() {
        for (IAlbumDetailViewCallback mAlbumCallback : mAlbumCallbacks) {
            mAlbumCallback.onLoading();
        }
    }


    public void setTargetAlbum(Album targetAlbum) {
        this.mTargetAlbum = targetAlbum;
    }


    @Override
    public void registerViewCallback(IAlbumDetailViewCallback callback) {
        if (mAlbumCallbacks != null) {
            mAlbumCallbacks.add(callback);
            if (mTargetAlbum != null) {
                callback.onAlbumLoaded(mTargetAlbum);
            }
        }
    }

    @Override
    public void unRegisterViewCallback(IAlbumDetailViewCallback callback) {
        mAlbumCallbacks.remove(callback);
    }
}
