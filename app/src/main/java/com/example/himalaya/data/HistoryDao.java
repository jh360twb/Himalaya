package com.example.himalaya.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.himalaya.base.BaseApplication;
import com.example.himalaya.utils.Constants;
import com.example.himalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.model.album.Announcer;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tian
 * @description
 * @date :2020/5/10 12:44
 */
public class HistoryDao implements IHistoryDao {
    private IHistoryDaoCallback mCallback = null;
    private final XimalayaDBHelper mXimalayaDBHelper;
    private Object mLock = new Object();
    private static final String TAG = "HistoryDao";

    private HistoryDao() {
        mXimalayaDBHelper = new XimalayaDBHelper(BaseApplication.getAppContext());
    }

    private static HistoryDao sHistoryDao = null;


    public static HistoryDao getInstance() {
        if (sHistoryDao == null) {
            synchronized (HistoryDao.class) {
                if (sHistoryDao == null) {
                    sHistoryDao = new HistoryDao();
                }
            }
        }
        return sHistoryDao;
    }

    @Override
    public void setCallback(IHistoryDaoCallback callback) {
        mCallback = callback;
    }

    @Override
    public void addHistory(Track track) {
        synchronized (mLock) {
            SQLiteDatabase db = null;
            boolean isAddSuccess = false;
            try {
                db = mXimalayaDBHelper.getWritableDatabase();
                db.delete(Constants.HIS_TB_NAME, Constants.HIS_TRACK_ID + "=?", new String[]{track.getDataId() + ""});
                db.beginTransaction();
                ContentValues values = new ContentValues();
                values.put(Constants.HIS_TRACK_ID, track.getDataId());
                values.put(Constants.HIS_TITLE, track.getTrackTitle());
                values.put(Constants.HIS_DURATION, track.getDuration());
                values.put(Constants.HIS_PLAY_COUNT, track.getPlayCount());
                values.put(Constants.HIS_UPDATE_TIME, track.getUpdatedAt());
                values.put(Constants.HIS_COVER, track.getCoverUrlLarge());
                values.put(Constants.HIS_AUTHOR_NAME, track.getAnnouncer().getNickname());
                db.insert(Constants.HIS_TB_NAME, null, values);
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
    }

    @Override
    public void delHistory(Track track) {
        synchronized (mLock) {
            SQLiteDatabase db = null;
            boolean isDelSuccess = false;
            try {
                db = mXimalayaDBHelper.getWritableDatabase();
                db.beginTransaction();
                db.delete(Constants.HIS_TB_NAME, Constants.HIS_TRACK_ID + "=?", new String[]{track.getDataId() + ""});
                db.setTransactionSuccessful();
                isDelSuccess = true;
            } catch (Exception e) {
                e.printStackTrace();
                isDelSuccess = false;
            } finally {
                if (db != null) {
                    db.endTransaction();
                    db.close();
                }
                if (mCallback != null) {
                    mCallback.onDelResult(isDelSuccess);
                }
            }
        }
    }

    @Override
    public void getHistories() {
        synchronized (mLock) {
            SQLiteDatabase db = null;
            List<Track> result = new ArrayList<>();
            try {
                db = mXimalayaDBHelper.getReadableDatabase();
                db.beginTransaction();
                Cursor query = db.query(Constants.HIS_TB_NAME, null, null, null, null, null,
                        "_id desc");
                while (query.moveToNext()) {
                    Track track = new Track();
                    long trackId = query.getLong(query.getColumnIndex(Constants.HIS_TRACK_ID));
                    track.setDataId(trackId);
                    String title = query.getString(query.getColumnIndex(Constants.HIS_TITLE));
                    track.setTrackTitle(title);
                    int duration = query.getInt(query.getColumnIndex(Constants.HIS_DURATION));
                    track.setDuration(duration);
                    int playCount = query.getInt(query.getColumnIndex(Constants.HIS_PLAY_COUNT));
                    track.setPlayCount(playCount);
                    long updateTime = query.getLong(query.getColumnIndex(Constants.HIS_UPDATE_TIME));
                    track.setUpdatedAt(updateTime);
                    String coverUrl = query.getString(query.getColumnIndex(Constants.HIS_COVER));
                    track.setCoverUrlLarge(coverUrl);
                    String authorName = query.getString(query.getColumnIndex(Constants.HIS_AUTHOR_NAME));
                    Announcer announcer = new Announcer();
                    announcer.setNickname(authorName);
                    track.setAnnouncer(announcer);
                    result.add(track);
                }
                //query.close();
                db.setTransactionSuccessful();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (db != null) {
                    db.endTransaction();
                    db.close();
                }
                if (mCallback != null) {
                    mCallback.onHisListLoaded(result);
                }
            }
        }
    }

    @Override
    public void cleanHistories() {
        synchronized (mLock) {
            SQLiteDatabase db = null;
            boolean isDelSuccess = false;
            try {
                db = mXimalayaDBHelper.getWritableDatabase();
                db.beginTransaction();
                db.delete(Constants.HIS_TB_NAME, null, null);
                db.setTransactionSuccessful();
                isDelSuccess = true;
            } catch (Exception e) {
                e.printStackTrace();
                isDelSuccess = false;
            } finally {
                if (db != null) {
                    db.endTransaction();
                    db.close();
                }
                if (mCallback != null) {
                    mCallback.onHistoriesClean(isDelSuccess);
                }
            }
        }
    }
}
