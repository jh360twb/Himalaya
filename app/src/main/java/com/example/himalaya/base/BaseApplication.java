package com.example.himalaya.base;

import android.app.Application;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.example.himalaya.MainActivity;
import com.example.himalaya.receiver.MyPlayerReceiver;
import com.example.himalaya.utils.LogUtil;
import com.squareup.haha.perflib.Main;
import com.squareup.leakcanary.LeakCanary;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.appnotification.NotificationColorUtils;
import com.ximalaya.ting.android.opensdk.player.appnotification.XmNotificationCreater;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerConfig;
import com.ximalaya.ting.android.opensdk.util.BaseUtil;

public class BaseApplication extends Application {
    private static Handler sHandler = null;
    private static Context sContext = null;
    private XmPlayerManager mXmPlayerManager;
    private static final String TAG = "BaseApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化用户key
        initAccount();
        //初始化通知栏
        initNotification();
        //初始化播放器
        initPlayer();
        //设置是否显示log
        LogUtil.init(this.getPackageName(), false);
        sHandler = new Handler();
        sContext = getBaseContext();
    }


    private void initLeakcanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);
    }

    private void initAccount() {
        if (BaseUtil.isMainProcess(this)) {
            CommonRequest mXimalaya = CommonRequest.getInstanse();
            if (DTransferConstants.isRelease) {
                String mAppSecret = "8646d66d6abe2efd14f2891f9fd1c8af";
                mXimalaya.setAppkey("9f9ef8f10bebeaa83e71e62f935bede8");
                mXimalaya.setPackid("com.app.test.android");
                mXimalaya.init(this, mAppSecret);
            } else {
                String mAppSecret = "0a09d7093bff3d4947a5c4da0125972e";
                mXimalaya.setAppkey("f4d8f65918d9878e1702d49a8cdf0183");
                mXimalaya.setPackid("com.ximalaya.qunfeng");
                mXimalaya.init(this, mAppSecret);
            }
        }
    }

    private void initPlayer() {
        if (BaseUtil.isMainProcess(this)) {
            mXmPlayerManager = XmPlayerManager.getInstance(this);
            mXmPlayerManager.init();
            mXmPlayerManager.setBreakpointResume(true);
            XmPlayerConfig.getInstance(sContext).setUseTrackHighBitrate(true);
        }
    }

    private void initNotification() {
        if (BaseUtil.isPlayerProcess(this)) {
            XmNotificationCreater instance = XmNotificationCreater.getInstanse(this);
            instance.setNextPendingIntent((PendingIntent) null);
            instance.setPrePendingIntent((PendingIntent) null);
            instance.setStartOrPausePendingIntent((PendingIntent)null);

            String actionName = "com.app.test.android.Action_Close";
            Intent intent = new Intent(actionName);
            intent.setClass(this, MyPlayerReceiver.class);
            PendingIntent broadcast = PendingIntent.getBroadcast(this, 0, intent, 0);
            instance.setClosePendingIntent(broadcast);

            String pauseActionName = "com.app.test.android.Action_PAUSE_START";
            Intent intent1 = new Intent(pauseActionName);
            intent1.setClass(this, MyPlayerReceiver.class);
            PendingIntent broadcast1 = PendingIntent.getBroadcast(this, 0, intent1, 0);
            instance.setStartOrPausePendingIntent(broadcast1);
        }
    }

    public static Handler getHandler() {
        return sHandler;
    }

    public static Context getAppContext() {
        return sContext;
    }
}
