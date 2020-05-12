package com.example.himalaya.utils;

/**
 * @author Tian
 * @description 常量类
 * @date :2020/4/16 12:06
 */

public class Constants {

    //搜索几条的默认值
    public static final int COUNT_SEARCH = 50;
    //获取推荐列表的专辑数量
    public static int COUNT_RECOMMEND = 50;
    //专辑中的声音个数
    public static int COUNT_TRACKS = 50;
    //热词的数量
    public static int COUNT_HOT_WORD = 10;
    //数据库相关的常量
    public static final String DB_NAME = "ximalaya.db";
    //数据库的版本
    public static final int DB_VERSION_CODE = 1;
    //订阅的表名
    public static final String SUB_TB_NAME = "tb_subscription";
    public static final String SUB_ID = "_id";
    public static final String SUB_COVER_URL = "coverUrl";
    public static final String SUB_TITLE = "title";
    public static final String SUB_DESCRIPTION = "description";
    public static final String SUB_TRACKS_COUNT = "tracksCount";
    public static final String SUB_PLAY_COUNT = "playCount";
    public static final String SUB_AUTHOR_NAME = "authorName";
    public static final String SUB_ALBUM_ID = "albumId";
    //订阅的最大数量
    public static final int MAX_SUB_COUNT = 100;
    //历史的表名
    public static final String HIS_TB_NAME = "tb_history";
    public static final String HIS_ID = "_id";
    public static final String HIS_TRACK_ID = "historyTrackId";
    public static final String HIS_TITLE = "title";
    public static final String HIS_DURATION = "duration";
    public static final String HIS_PLAY_COUNT = "playCount";
    public static final String HIS_UPDATE_TIME = "updateTime";
    public static final String HIS_COVER = "coverUrl";
    public static final String HIS_AUTHOR_NAME = "authorName";
    //历史的最大数量
    public static final int MAX_HIS_COUNT = 100;
}
