package com.example.himalaya.data;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.himalaya.utils.Constants;
import com.example.himalaya.utils.LogUtil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @author Tian
 * @description 新建数据库和更新数据库
 * @date :2020/5/6 21:30
 */
public class XimalayaDBHelper extends SQLiteOpenHelper {
    private static final String TAG = "XimalayaDBHelper";

    public XimalayaDBHelper(@Nullable Context context) {
        super(context, Constants.DB_NAME,null,Constants.DB_VERSION_CODE);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建订阅表
        String subTbSql = "create table " + Constants.SUB_TB_NAME + "(" +
                Constants.SUB_ID + " integer primary key autoincrement, " +
                Constants.SUB_COVER_URL + " varchar, " +
                Constants.SUB_TITLE + " varchar," +
                Constants.SUB_DESCRIPTION + " varchar," +
                Constants.SUB_PLAY_COUNT + " integer," +
                Constants.SUB_TRACKS_COUNT + " integer," +
                Constants.SUB_AUTHOR_NAME + " varchar," +
                Constants.SUB_ALBUM_ID + " integer" +
                ")";
        db.execSQL(subTbSql);
        //创建历史表
        String hisTbSql = "create table "+Constants.HIS_TB_NAME+"("+
                Constants.HIS_ID+" integer primary key autoincrement,"+
                Constants.HIS_TRACK_ID+" integer,"+
                Constants.HIS_TITLE+" varchar,"+
                Constants.HIS_PLAY_COUNT+" integer,"+
                Constants.HIS_DURATION+" integer,"+
                Constants.HIS_UPDATE_TIME+" integer,"+
                Constants.HIS_AUTHOR_NAME+" varchar,"+
                Constants.HIS_COVER+" varchar"+
                ")";
        db.execSQL(hisTbSql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
