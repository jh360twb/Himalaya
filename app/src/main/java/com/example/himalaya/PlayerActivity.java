package com.example.himalaya;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.himalaya.adapters.PlayerTrackPagerAdapter;
import com.example.himalaya.base.BaseActivity;
import com.example.himalaya.interfaces.IPlayerCallback;
import com.example.himalaya.presenters.PlayerPresenter;
import com.example.himalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.viewpager.widget.ViewPager;

import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_LIST;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_LIST_LOOP;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_RANDOM;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_SINGLE;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_SINGLE_LOOP;

public class PlayerActivity extends BaseActivity implements IPlayerCallback, ViewPager.OnPageChangeListener {

    private ImageView mControlBtn;
    private PlayerPresenter mInstance;
    private TextView mCurrentPos;
    private TextView mTrackDura;
    private static final String TAG = "PlayerActivity";
    //long转换为分钟:秒
    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat minFormat = new SimpleDateFormat("mm:ss");
    private SeekBar mTrackSeekBar;
    //拖动进度条
    private int mSeekCurPos;
    //手触摸进度条
    private boolean mIsUserTouchSeekBar = false;
    private ImageView mPlayPre;
    private ImageView mPlayNext;
    private TextView mTrackTitle;
    private ViewPager mTrackViewPager;
    //ViewPager的适配器
    private PlayerTrackPagerAdapter mPlayerTrackPagerAdapter;
    //总时长
    private long mTotalSeek;
    //触摸了pager
    private boolean isUserTouchPager = false;
    //改变播放模式
    private ImageView mPlayModeSwitchBtn;
    //当前播放模式
    private XmPlayListControl.PlayMode mCurrentPlayMode = PLAY_MODEL_LIST;
    //播放模式的map
    private static Map<XmPlayListControl.PlayMode, XmPlayListControl.PlayMode> sIntegerPlayModeMap = new HashMap<>();

    //四种播放模式,默认为列表播放
    static {
        sIntegerPlayModeMap.put(PLAY_MODEL_LIST, PLAY_MODEL_LIST_LOOP);
        sIntegerPlayModeMap.put(PLAY_MODEL_LIST_LOOP, PLAY_MODEL_RANDOM);
        sIntegerPlayModeMap.put(PLAY_MODEL_RANDOM, PLAY_MODEL_SINGLE_LOOP);
        sIntegerPlayModeMap.put(PLAY_MODEL_SINGLE_LOOP, PLAY_MODEL_LIST);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        mInstance = PlayerPresenter.getInstance();
        initView();
        mInstance.registerViewCallback(this);
        mInstance.getPlayList();
        initEvent();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mInstance != null) {
            mInstance.unRegisterViewCallback(this);
            mInstance = null;
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    private void initEvent() {
        //控制播放
        mControlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mInstance.isPlaying()) {
                    if (mInstance != null) {
                        mInstance.pause();
                    }
                } else {
                    if (mInstance != null) {
                        mInstance.play();
                    }
                }
            }
        });

        //进度条的手滑动监听
        mTrackSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mSeekCurPos = progress;
                    //拖动实时更新当前进度
                    mInstance.onPlayProgress(progress, (int) mTotalSeek);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mIsUserTouchSeekBar = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //手离开之后
                mIsUserTouchSeekBar = false;
                mInstance.seekTo(mSeekCurPos);
            }
        });

        mPlayPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInstance.playPre();
            }
        });

        mPlayNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInstance.playNext();
            }
        });

        //viewpager左右滑动
        mTrackViewPager.addOnPageChangeListener(this);

        //ViewPager触摸事件
        mTrackViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        isUserTouchPager = true;
                        break;
                }
                return false;
            }
        });

        //切换播放模式
        mPlayModeSwitchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //默认为列表播放
                XmPlayListControl.PlayMode playMode = sIntegerPlayModeMap.get(mCurrentPlayMode);
                if (mInstance != null) {
                    mInstance.switchPlayMode(playMode);
                    mCurrentPlayMode = playMode;

                }
            }
        });

    }

    //改变
    private void upDatePlayModeBtnImg() {
        switch (mCurrentPlayMode) {
            case PLAY_MODEL_LIST:
                mPlayModeSwitchBtn.setImageResource(R.drawable.selector_play_mode_list_order);
                break;
            case PLAY_MODEL_RANDOM:
                mPlayModeSwitchBtn.setImageResource(R.drawable.selector_paly_mode_random);
                break;
            case PLAY_MODEL_SINGLE_LOOP:
                mPlayModeSwitchBtn.setImageResource(R.drawable.selector_paly_mode_single_loop);
                break;
            case PLAY_MODEL_LIST_LOOP:
                mPlayModeSwitchBtn.setImageResource(R.drawable.selector_paly_mode_list_order_looper);
                break;
        }
    }

    private void initView() {
        mControlBtn = findViewById(R.id.play_or_pause_btn);
        mCurrentPos = findViewById(R.id.current_position);
        mTrackDura = findViewById(R.id.track_duration);
        mTrackSeekBar = findViewById(R.id.track_seek_bar);
        mPlayPre = findViewById(R.id.play_pre);
        mPlayNext = findViewById(R.id.play_next);
        mTrackTitle = findViewById(R.id.track_title);
        mTrackViewPager = findViewById(R.id.track_pager_view);
        mPlayModeSwitchBtn = findViewById(R.id.player_mode_switch_btn);

        //创建适配器
        mPlayerTrackPagerAdapter = new PlayerTrackPagerAdapter();
        mTrackViewPager.setAdapter(mPlayerTrackPagerAdapter);


        //初始化图标
        if (mInstance.isPlaying()) {
            if (mControlBtn != null) {
                mControlBtn.setImageResource(R.mipmap.stop_normal);
            }
        } else {
            if (mControlBtn != null) {
                mControlBtn.setImageResource(R.mipmap.play_normal);
            }
        }
    }

    @Override
    public void onPlayStart() {
        if (mControlBtn != null) {
            mControlBtn.setImageResource(R.mipmap.stop_normal);
        }
    }

    @Override
    public void onPlayPause() {
        if (mControlBtn != null) {
            mControlBtn.setImageResource(R.mipmap.play_normal);
        }
    }

    @Override
    public void onPlayStop() {

    }

    @Override
    public void onPlayError() {

    }

    @Override
    public void onNextPlay(Track track) {

    }

    @Override
    public void onPrePlay(Track track) {

    }

    @Override
    public void onListLoaded(List<Track> list) {
        //LogUtil.e(TAG,"list ->" + list.size());
        mPlayerTrackPagerAdapter.setData(list);
    }

    @Override
    public void onPlayModeChange(XmPlayListControl.PlayMode mode) {
        mCurrentPlayMode = mode;
        upDatePlayModeBtnImg();
    }

    @Override
    public void onProgressChange(long currentProgress, long total) {
        mTotalSeek = total;
        //非用户拖动
        if (!mIsUserTouchSeekBar) {
            mTrackSeekBar.setMax((int) total);
            mTrackSeekBar.setProgress((int) currentProgress);
        }

        String stringCur;
        String stringTotal;
        long anHour = 60 * 1000 * 60;
        //LogUtil.e(TAG, "total -> " + total);
        //总时间
        if (total > anHour) {
            stringTotal = HourFormat(total);
        } else {
            stringTotal = minFormat.format(total);
        }
        //当前时间
        if (currentProgress > anHour) {
            stringCur = HourFormat(currentProgress);
        } else {
            stringCur = minFormat.format(currentProgress);
        }
        if (mCurrentPos != null) {
            mCurrentPos.setText(stringCur);
        }
        if (mTrackDura != null) {
            mTrackDura.setText(stringTotal);
        }
    }

    //数字转换为小时的字符串
    private String HourFormat(long currentProgress) {
        StringBuilder sb;
        currentProgress = currentProgress / 1000;
        int hour = (int) (currentProgress / 3600);
        int minute = (int) ((currentProgress - hour * 3600) / 60);
        int second = (int) (currentProgress - hour * 3600 - minute * 60);
        String hourStr = hour + ":", minuteStr = minute + ":", secondStr = second + "";
        if (hour < 10) hourStr = "0" + hourStr;
        if (minute < 10) minuteStr = "0" + minuteStr;
        if (second < 10) secondStr = "0" + secondStr;
        sb = new StringBuilder(hourStr + minuteStr + secondStr);
        return sb.toString();
    }

    @Override
    public void onAdLoading() {

    }

    @Override
    public void onAdFinished() {

    }

    //更新歌曲标题和图面
    @Override
    public void onTrackUpdate(Track track, int index) {
        //LogUtil.e(TAG, "Title ->" + title);
        if (mTrackTitle != null) {
            mTrackTitle.setText(track.getTrackTitle());
        }

        if (mTrackViewPager != null) {
            mTrackViewPager.setCurrentItem(index, true);
        }

    }

    //==============ViewPager==========
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (mInstance != null && isUserTouchPager) {
            mInstance.playByIndex(position);
        }
        isUserTouchPager = false;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
    //==============ViewPager=============
}
