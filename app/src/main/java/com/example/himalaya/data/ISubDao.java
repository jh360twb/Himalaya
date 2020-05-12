package com.example.himalaya.data;

import com.ximalaya.ting.android.opensdk.model.album.Album;

/**
 * @author Tian
 * @description dao层的接口
 * @date :2020/5/7 22:03
 */
public interface ISubDao {

    void setCallback(ISubDaoCallback callback);
    /**
     *添加专辑订阅
    */
    void addAlbum(Album album);

    /**
     *删除专辑订阅
     */
    void delAlbum(Album album);

    /**
     *获取订阅内容
     */
    void listAlbum();
}
