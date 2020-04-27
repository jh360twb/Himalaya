package com.example.himalaya.presenters;

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
    private static AlbumDetailPresenter sInstance= null;
    private Album mTargetAlbum = null;
    List<IAlbumDetailViewCallback> mAlbumCallbacks = new ArrayList<>();
    private static final String TAG = "AlbumDetailPresenter";

    private void AlbumDetailPresenter(){}


    public static AlbumDetailPresenter getInstance(){
        if (sInstance == null) {
            synchronized (AlbumDetailPresenter.class){
                if (sInstance == null) {
                    sInstance = new AlbumDetailPresenter();
                }
            }
        }
        return sInstance;
    }

    @Override
    public void pullRefreshMore() {

    }

    @Override
    public void loadMore() {

    }

    @Override
    public void getAlbumDetail(long albumId, int page) {
        updateLoading();
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.ALBUM_ID, albumId+"");
        map.put(DTransferConstants.SORT, "asc");
        map.put(DTransferConstants.PAGE, page+"");
        map.put(DTransferConstants.PAGE_SIZE, Constants.COUNT_TRACKS+"");
        CommonRequest.getTracks(map, new IDataCallBack<TrackList>() {
            @Override
            public void onSuccess(TrackList trackList) {
                List<Track> tracks = trackList.getTracks();
                //tracks.clear();
                for (IAlbumDetailViewCallback mAlbumCallback : mAlbumCallbacks) {
                    mAlbumCallback.onDetailListLoaded(tracks);
                }
                //LogUtil.e(TAG,trackList.getAlbumIntro());
            }

            @Override
            public void onError(int i, String s) {
                LogUtil.e(TAG,s);
                for (IAlbumDetailViewCallback mAlbumCallback : mAlbumCallbacks) {
                    mAlbumCallback.onNetworkError(i,s);
                }
            }
        });
    }

    private void updateLoading() {
        for (IAlbumDetailViewCallback mAlbumCallback : mAlbumCallbacks) {
            mAlbumCallback.onLoading();
        }
    }


    public void setTargetAlbum(Album targetAlbum){
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
