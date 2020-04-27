package com.example.himalaya.base;

import com.example.himalaya.interfaces.IAlbumDetailViewCallback;

/**
 * @author Tian
 * @description
 * @date :2020/4/21 15:53
 */
public interface IBasePresenter<T> {
    //注册
    void registerViewCallback(T t);

    //注销
    void unRegisterViewCallback(T t);
}
