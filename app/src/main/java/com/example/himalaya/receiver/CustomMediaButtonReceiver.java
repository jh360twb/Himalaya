package com.example.himalaya.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * @author Tian
 * @description
 * @date :2020/5/13 14:53
 */
public class CustomMediaButtonReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("CustomMediaButtonReceiver.onReceive  " + intent);
    }
}
