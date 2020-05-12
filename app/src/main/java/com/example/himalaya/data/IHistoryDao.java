package com.example.himalaya.data;

import com.ximalaya.ting.android.opensdk.model.track.Track;

/**
 * @author Tian
 * @description history的Dao去实现,在presenter层调用
 * @date :2020/5/9 23:43
 */
public interface IHistoryDao {
    /**
     * 设置回调接口
     * @param callback
     */
    void setCallback(IHistoryDaoCallback callback);

    /**
     * 添加历史
     * @param track
     */
    void addHistory(Track track);

    /**
     * 删除历史
     * @param track
     */
    void delHistory(Track track);

    /**
     * 获取历史列表
     */
    void getHistories();

    /**
     * 清除历史
     */
    void cleanHistories();
}
