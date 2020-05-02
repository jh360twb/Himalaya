package com.example.himalaya.api;

import com.example.himalaya.utils.Constants;
import com.example.himalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Tian
 * @description
 * @date :2020/5/2 17:05
 */
public class XimalayaApi {

    private XimalayaApi(){}

    private static XimalayaApi instance;

    public static XimalayaApi getInstance(){
        if (instance == null){
            synchronized (XimalayaApi.class){
                if (instance == null){
                    instance = new XimalayaApi();
                }
            }
        }
        return instance;
    }

    /**
     * 获取推荐内容
     * @param callBack
     */
    public void getRecommendList(IDataCallBack<GussLikeAlbumList> callBack) {
        //封装参数
        Map<String, String> map = new HashMap<>();
        //表示一页数据返回多少条
        map.put(DTransferConstants.LIKE_COUNT, Constants.COUNT_RECOMMEND + "");
        CommonRequest.getGuessLikeAlbum(map, callBack);
    }

    /**
     * 根据专辑id获取专辑内容
     * @param callBack
     * @param albumId
     * @param pageIndex
     */
    public void getAlbumDetail(IDataCallBack<TrackList> callBack,long albumId,int pageIndex){
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.ALBUM_ID, albumId + "");
        map.put(DTransferConstants.SORT, "asc");
        map.put(DTransferConstants.PAGE, pageIndex + "");
        map.put(DTransferConstants.PAGE_SIZE, Constants.COUNT_TRACKS + "");
        CommonRequest.getTracks(map,callBack);
    }

}
