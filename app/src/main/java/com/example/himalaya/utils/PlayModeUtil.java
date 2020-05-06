package com.example.himalaya.utils;

import com.example.himalaya.R;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import java.util.HashMap;
import java.util.Map;

import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_LIST;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_LIST_LOOP;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_RANDOM;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_SINGLE_LOOP;

/**
 * @author Tian
 * @description
 * @date :2020/5/3 11:15
 */
public class PlayModeUtil {
    //播放模式的map
    public static Map<XmPlayListControl.PlayMode, XmPlayListControl.PlayMode> sIntegerPlayModeMap = new HashMap<>();

    //四种播放模式,默认为列表播放
    static {
        sIntegerPlayModeMap.put(PLAY_MODEL_LIST, PLAY_MODEL_LIST_LOOP);
        sIntegerPlayModeMap.put(PLAY_MODEL_LIST_LOOP, PLAY_MODEL_RANDOM);
        sIntegerPlayModeMap.put(PLAY_MODEL_RANDOM, PLAY_MODEL_SINGLE_LOOP);
        sIntegerPlayModeMap.put(PLAY_MODEL_SINGLE_LOOP, PLAY_MODEL_LIST);
    }

    //改变
   public static int upDatePlayModeBtnImg(XmPlayListControl.PlayMode mCurrentPlayMode) {
        int resId = R.drawable.selector_play_mode_list_order;
        switch (mCurrentPlayMode) {
            case PLAY_MODEL_LIST:
                resId = R.drawable.selector_play_mode_list_order;
                break;
            case PLAY_MODEL_RANDOM:
                resId = R.drawable.selector_paly_mode_random;
                break;
            case PLAY_MODEL_SINGLE_LOOP:
                resId = R.drawable.selector_paly_mode_single_loop;
                break;
            case PLAY_MODEL_LIST_LOOP:
                resId = R.drawable.selector_paly_mode_list_order_looper;
                break;
        }

        return resId;
    }




}
