package com.example.himalaya.presenters;

import com.example.himalaya.data.XimalayaApi;
import com.example.himalaya.interfaces.IRecommendPresenter;
import com.example.himalaya.interfaces.IRecommendViewCallback;
import com.example.himalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tian
 * @description 对接口IRecommendPresenter重写
 * @date :2020/4/17 11:52
 */
public class RecommendPresenter implements IRecommendPresenter {

    private static final String TAG = "RecommendPresenter";

    private List<IRecommendViewCallback> mCallbacks = new ArrayList<>();
    private List<Album> mCurrentAlbums =null;

    private RecommendPresenter() {
    }

    private static RecommendPresenter sInstance = null;

    //获取单例对象
    public static RecommendPresenter getInstance() {
        if (sInstance == null) {
            synchronized (RecommendPresenter.class) {
                if (sInstance == null) {
                    sInstance = new RecommendPresenter();
                }
            }
        }
        return sInstance;
    }

    @Override
    public void getRecommendList() {
        getRecommendData();
    }

    public List<Album> getCurrentRecommand() {
        return mCurrentAlbums;
    }

    //获取推荐内容 接口3.10.6
    private void getRecommendData() {
        updateLoading();
        XimalayaApi ximalayaApi = XimalayaApi.getInstance();
        ximalayaApi.getRecommendList(new IDataCallBack<GussLikeAlbumList>() {

            @Override
            public void onSuccess(GussLikeAlbumList gussLikeAlbumList) {
                //已经到达主线程了,所以不需要切换线程
                if (gussLikeAlbumList != null) {
                    List<Album> albumList = gussLikeAlbumList.getAlbumList();
                    if (albumList != null) {
                        //数据回来,更新UI
                        //Log.e(TAG, "albumList.size -> "+ albumList.size() );
                        handleRecommendResult(albumList);
                    }
                }
            }
            @Override
            public void onError(int i, String s) {
                LogUtil.e(TAG, i + s);
                handleError();
            }
        });
    }

    private void handleError() {

        if (mCallbacks != null) {
            for (IRecommendViewCallback mCallback : mCallbacks) {
                mCallback.onNetworkError();
            }
        }
    }

    private void handleRecommendResult(List<Album> albumList) {
        //通知UI
        if (albumList != null) {
            if (albumList.size() == 0) {
                for (IRecommendViewCallback mCallback : mCallbacks) {
                    mCallback.onEmpty();
                }
            }else {
                for (IRecommendViewCallback mCallback : mCallbacks) {
                    mCallback.onRecommendListLoaded(albumList);
                }
            }
            mCurrentAlbums = albumList;
        }
    }

    private void updateLoading(){
        for (IRecommendViewCallback mCallback : mCallbacks) {
            mCallback.onLoading();
        }
    }

    @Override
    public void pullRefreshMore() {

    }

    @Override
    public void loadMore() {

    }

    @Override
    public void registerViewCallback(IRecommendViewCallback callback) {
        if (mCallbacks != null && !mCallbacks.contains(callback)) {
            mCallbacks.add(callback);
        }
    }

    @Override
    public void unRegisterViewCallback(IRecommendViewCallback callback) {
        if (mCallbacks != null) {
            mCallbacks.remove(callback);
        }
    }



}
