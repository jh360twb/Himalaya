package com.example.himalaya.data;

import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.List;

/**
 * @author Tian
 * @description 将数据库修改后的消息通知到presenter层的接口
 * @date :2020/5/7 23:40
 */
public interface ISubDaoCallback {
    /**
     * 添加的结果回调方法
     * @param isSuccess
     */
    void onAddResult(boolean isSuccess);

    /**
     * 删除的结果回调
     * @param isSuccess
     */
    void onDelResult(boolean isSuccess);

    /**
     * 加载的结果
     * @param result
     */
    void onSubListLoaded(List<Album> result);
}
