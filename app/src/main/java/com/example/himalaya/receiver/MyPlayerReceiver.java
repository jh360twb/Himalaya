package com.example.himalaya.receiver;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;

public class MyPlayerReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("MyPlayerReceiver.onReceive " + intent);
        if(intent.getAction().equals("com.app.test.android.Action_Close")) {
            Toast.makeText(context, "通知栏点击了关闭", Toast.LENGTH_LONG).show();
            XmPlayerManager.release();
            //android.os.Process.killProcess(android.os.Process.myPid());
        } else if(intent.getAction().equals("com.app.test.android.Action_PAUSE_START")) {
            if(XmPlayerManager.getInstance(context).isPlaying()) {
                XmPlayerManager.getInstance(context).pause();
            } else {
                XmPlayerManager.getInstance(context).play();
            }
        }
    }
}
