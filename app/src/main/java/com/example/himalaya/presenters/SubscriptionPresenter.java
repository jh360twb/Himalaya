package com.example.himalaya.presenters;

import com.example.himalaya.base.BaseApplication;
import com.example.himalaya.data.ISubDaoCallback;
import com.example.himalaya.data.SubscriptionDao;
import com.example.himalaya.interfaces.ISubscriptionCallback;
import com.example.himalaya.interfaces.ISubscriptionPresenter;
import com.example.himalaya.utils.Constants;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;

/**
 * @author Tian
 * @description
 * @date :2020/5/8 10:42
 */
public class SubscriptionPresenter implements ISubscriptionPresenter, ISubDaoCallback {

    private final SubscriptionDao mScriptionDao;
    private Map<Long, Album> mData = new HashMap<>();
    private List<ISubscriptionCallback> mCallbacks = new ArrayList<>();

    private SubscriptionPresenter() {
        mScriptionDao = SubscriptionDao.getInstance();
        mScriptionDao.setCallback(this);
    }

    //子线程查询数据
    private void listSubscriptions() {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> emitter) {
                //只调用,不处理结果
                if (mScriptionDao != null) {
                    //数据库耗时操作放在子线程
                    mScriptionDao.listAlbum();
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }


    private static SubscriptionPresenter sPresenter = null;

    public static SubscriptionPresenter getInstance() {
        if (sPresenter == null) {
            synchronized (SubscriptionPresenter.class) {
                if (sPresenter == null) {
                    sPresenter = new SubscriptionPresenter();
                }
            }
        }
        return sPresenter;
    }

    @Override
    public void addSubscription(final Album album) {

        //判断当前的订阅数量，不能超过100
        if (mData.size() >= Constants.MAX_SUB_COUNT) {
            //给出提示
            for (ISubscriptionCallback callback : mCallbacks) {
                callback.onSubFull();
            }
            return;
        }

        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> emitter) {
                if (mScriptionDao != null) {
                    mScriptionDao.addAlbum(album);
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void deleteSubscription(final Album album) {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Object> emitter) {
                if (mScriptionDao != null) {
                    mScriptionDao.delAlbum(album);
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void getSubscriptionList() {
        listSubscriptions();
    }

    @Override
    public boolean isSub(Album album) {
        Album result = mData.get(album.getId());
        //不为空,表示已经订阅
        return result!=null;
    }

    @Override
    public void registerViewCallback(ISubscriptionCallback iSubscriptionCallback) {
        if (!mCallbacks.contains(iSubscriptionCallback)) {
            mCallbacks.add(iSubscriptionCallback);
        }
    }

    @Override
    public void unRegisterViewCallback(ISubscriptionCallback iSubscriptionCallback) {
        mCallbacks.remove(iSubscriptionCallback);
    }

    //通过dao层通知来的数据
    @Override
    public void onAddResult(final boolean isSuccess) {
        listSubscriptions();
        //添加结果的回调
        BaseApplication.getHandler().post(new Runnable() {
            @Override
            public void run() {
                for (ISubscriptionCallback callback : mCallbacks) {
                    //通知到UI界面
                    callback.onAddResult(isSuccess);
                }
            }
        });
    }

    @Override
    public void onDelResult(final boolean isSuccess) {
        listSubscriptions();
        //删除结果的回调
        BaseApplication.getHandler().post(new Runnable() {
            @Override
            public void run() {
                for (ISubscriptionCallback callback : mCallbacks) {
                    callback.onDeleteResult(isSuccess);
                }
            }
        });
    }

    @Override
    public void onSubListLoaded(final List<Album> result) {
        mData.clear();
        //把通知来的数据存储到mData中
        for (Album album : result) {
            mData.put(album.getId(), album);
        }
        //界面的更新
        BaseApplication.getHandler().post(new Runnable() {
            @Override
            public void run() {
                for (ISubscriptionCallback callback : mCallbacks) {
                    callback.onSubscriptionsLoaded(result);
                }
            }
        });
    }
}
