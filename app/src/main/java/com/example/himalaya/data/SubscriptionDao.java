package com.example.himalaya.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.himalaya.base.BaseApplication;
import com.example.himalaya.utils.Constants;
import com.example.himalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.Announcer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tian
 * @description dao层,用于数据库的增删查
 * @date :2020/5/7 22:06
 */
public class SubscriptionDao implements ISubDao {
    private static final SubscriptionDao ourInstance = new SubscriptionDao();
    private final XimalayaDBHelper mXimalayaDBHelper;
    private static final String TAG = "SubscriptionDao";
    private ISubDaoCallback mCallback = null;

    public static SubscriptionDao getInstance() {
        return ourInstance;
    }

    private SubscriptionDao() {
        mXimalayaDBHelper = new XimalayaDBHelper(BaseApplication.getAppContext());
    }

    @Override
    public void setCallback(ISubDaoCallback callback) {
        mCallback = callback;
    }

    @Override
    public void addAlbum(Album album) {
        SQLiteDatabase db = null;
        boolean isAddSuccess = false;
        try {
            db = mXimalayaDBHelper.getWritableDatabase();
            db.beginTransaction();
            ContentValues values = new ContentValues();
            //封装数据
            values.put(Constants.SUB_COVER_URL, album.getCoverUrlLarge());
            values.put(Constants.SUB_TITLE, album.getAlbumTitle());
            values.put(Constants.SUB_DESCRIPTION, album.getAlbumIntro());
            values.put(Constants.SUB_TRACKS_COUNT, album.getIncludeTrackCount());
            values.put(Constants.SUB_PLAY_COUNT, album.getPlayCount());
            values.put(Constants.SUB_AUTHOR_NAME, album.getAnnouncer().getNickname());
            values.put(Constants.SUB_ALBUM_ID, album.getId());
            db.insert(Constants.SUB_TB_NAME, null, values);
            db.setTransactionSuccessful();
            isAddSuccess = true;

        } catch (Exception e) {
            e.printStackTrace();
            isAddSuccess = false;
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
            if (mCallback != null) {
                mCallback.onAddResult(isAddSuccess);
            }
        }
    }

    @Override
    public void delAlbum(Album album) {
        SQLiteDatabase db = null;
        boolean isDeleteSuccess = false;
        try {
            db = mXimalayaDBHelper.getWritableDatabase();
            db.beginTransaction();
            int delete = db.delete(Constants.SUB_TB_NAME, Constants.SUB_ALBUM_ID + "=?", new String[]{album.getId() + ""});
            LogUtil.e(TAG, "delete -> " + delete);
            db.setTransactionSuccessful();
            isDeleteSuccess = true;
        } catch (Exception e) {
            e.printStackTrace();
            isDeleteSuccess = false;
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
            if (mCallback != null) {
                mCallback.onDelResult(isDeleteSuccess);
            }
        }
    }

    @Override
    public void listAlbum() {
        SQLiteDatabase db = null;
        List<Album> result = new ArrayList<>();
        try {
            db = mXimalayaDBHelper.getReadableDatabase();
            db.beginTransaction();
            //逆序添加
            Cursor query = db.query(Constants.SUB_TB_NAME, null, null, null, null
                    , null, "_id desc");
            while (query.moveToNext()) {
                Album album = new Album();
                String coverUrl = query.getString(query.getColumnIndex(Constants.SUB_COVER_URL));
                album.setCoverUrlLarge(coverUrl);
                String title = query.getString(query.getColumnIndex(Constants.SUB_TITLE));
                album.setAlbumTitle(title);
                //
                String description = query.getString(query.getColumnIndex(Constants.SUB_DESCRIPTION));
                album.setAlbumIntro(description);
                //
                int tracksCount = query.getInt(query.getColumnIndex(Constants.SUB_TRACKS_COUNT));
                album.setIncludeTrackCount(tracksCount);
                //
                int playCount = query.getInt(query.getColumnIndex(Constants.SUB_PLAY_COUNT));
                album.setPlayCount(playCount);
                //
                int albumId = query.getInt(query.getColumnIndex(Constants.SUB_ALBUM_ID));
                album.setId(albumId);
                String authorName = query.getString(query.getColumnIndex(Constants.SUB_AUTHOR_NAME));
                Announcer announcer = new Announcer();
                announcer.setNickname(authorName);
                album.setAnnouncer(announcer);
                result.add(album);
            }
            query.close();
            db.setTransactionSuccessful();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
            //数据通知出去
            if (mCallback != null) {
                mCallback.onSubListLoaded(result);
            }
        }
    }
}
