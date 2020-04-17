package com.example.himalaya.presenters;

import com.example.himalaya.interfaces.IRecommendPresenter;
import com.example.himalaya.interfaces.IRecommendViewCallback;
import com.example.himalaya.utils.Constants;
import com.example.himalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Tian
 * @description
 * @date :2020/4/17 11:52
 */
public class RecommendPresenter implements IRecommendPresenter {

    private static final String TAG = "RecommendPresenter";

    private List<IRecommendViewCallback> mCallbacks = new ArrayList<>();

    private RecommendPresenter(){}

    private static RecommendPresenter sInstance = null;

    //获取单例对象
    public static RecommendPresenter getInstance(){
        if (sInstance == null){
            synchronized (RecommendPresenter.class){
                if (sInstance == null){
                    sInstance = new RecommendPresenter();
                }
            }
        }
        return sInstance;
    }

    @Override
    public void getRecommendList() {
        getRecommandData();
    }

    //获取推荐内容 接口3.10.6
    private void getRecommandData() {
        //封装参数
        Map<String, String> map = new HashMap<>();
        //表示一页数据返回多少条
        map.put(DTransferConstants.LIKE_COUNT, Constants.RECOMMEND_COUNT+"");
        CommonRequest.getGuessLikeAlbum(map, new IDataCallBack<GussLikeAlbumList>() {
            @Override
            public void onSuccess(GussLikeAlbumList gussLikeAlbumList) {
                //已经到达主线程了,所以不需要切换线程
                if (gussLikeAlbumList != null) {
                    List<Album> albumList =gussLikeAlbumList.getAlbumList();
                    if (albumList != null) {
                        //数据回来,更新UI
                        //Log.e(TAG, "albumList.size -> "+ albumList.size() );
                        handleRecommendResult(albumList);
                    }
                }
            }

            @Override
            public void onError(int i, String s) {
                LogUtil.e(TAG,i+s);
            }
        });
    }

    private void handleRecommendResult(List<Album> albumList) {
        //通知UI
        if (mCallbacks != null) {
            for (IRecommendViewCallback mCallback : mCallbacks) {
                mCallback.onRecommendListLoaded(albumList);
            }
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
        if (mCallbacks!=null &&!mCallbacks.contains(callback)) {
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
