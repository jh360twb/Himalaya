package com.example.himalaya.presenters;

import com.example.himalaya.base.BaseApplication;
import com.example.himalaya.data.HistoryDao;
import com.example.himalaya.data.IHistoryDaoCallback;
import com.example.himalaya.interfaces.IHistoryCallback;
import com.example.himalaya.interfaces.IHistoryPresenter;
import com.example.himalaya.utils.Constants;
import com.example.himalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;

/**
 * @author Tian
 * @description
 * @date :2020/5/10 15:38
 */
public class HistoryPresenter implements IHistoryPresenter, IHistoryDaoCallback {
    List<IHistoryCallback> mCallbacks = new ArrayList<>();
    private final HistoryDao mHistoryDao;
    private List<Track> mCurrentTracks;
    private Track mCurrentTrack;
    private static final String TAG = "HistoryPresenter";

    private HistoryPresenter() {
        mHistoryDao = HistoryDao.getInstance();
        mHistoryDao.setCallback(this);
    }

    private static HistoryPresenter sHistoryPresenter = null;

    public static HistoryPresenter getInstance() {
        if (sHistoryPresenter == null) {
            synchronized (HistoryPresenter.class) {
                sHistoryPresenter = new HistoryPresenter();
            }
        }
        return sHistoryPresenter;
    }

    private boolean isOutOfSize = false;

    @Override
    public void addHistory(final Track track) {
        //LogUtil.e(TAG,track.getTrackTitle());
        if (mCurrentTracks != null && mCurrentTracks.size() >= Constants.MAX_HIS_COUNT) {
            isOutOfSize = true;
            mCurrentTrack = track;
            delHistory(mCurrentTracks.get(mCurrentTracks.size() - 1));
        } else {
            doAddHistory(track);
        }
    }

    private void doAddHistory(final Track track) {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> emitter) {
                if (mHistoryDao != null) {
                    mHistoryDao.addHistory(track);
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void delHistory(final Track track) {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> emitter) {
                if (mHistoryDao != null) {
                    mHistoryDao.delHistory(track);
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    //调用数据库去查询数据,要放在子线程中
    @Override
    public void getHistoryList() {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> emitter) {
                if (mHistoryDao != null) {
                    mHistoryDao.getHistories();
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void cleanHistories() {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> emitter) {
                if (mHistoryDao != null) {
                    mHistoryDao.cleanHistories();
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }


    @Override
    public void registerViewCallback(IHistoryCallback iHistoryCallback) {
        if (!mCallbacks.contains(iHistoryCallback)) {
            mCallbacks.add(iHistoryCallback);
        }
    }

    @Override
    public void unRegisterViewCallback(IHistoryCallback iHistoryCallback) {
        mCallbacks.remove(iHistoryCallback);
    }

    //=========dao层
    @Override
    public void onAddResult(boolean isSuccess) {
        getHistoryList();
    }

    @Override
    public void onDelResult(boolean isSuccess) {
        if (isOutOfSize && mCurrentTrack != null) {
            isOutOfSize = false;
            addHistory(mCurrentTrack);
        } else {
            getHistoryList();
        }
    }

    @Override
    public void onHisListLoaded(final List<Track> tracks) {
        mCurrentTracks = tracks;
        //LogUtil.e(TAG,tracks.size()+"");
        BaseApplication.getHandler().post(new Runnable() {
            @Override
            public void run() {
                for (IHistoryCallback callback : mCallbacks) {
                    callback.onHistoriesLoaded(tracks);
                }
            }
        });

    }

    @Override
    public void onHistoriesClean(boolean isSuccess) {
        getHistoryList();
    }
}
