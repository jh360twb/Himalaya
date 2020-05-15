package com.example.himalaya;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.himalaya.adapters.PlayerTrackPagerAdapter;
import com.example.himalaya.base.BaseActivity;
import com.example.himalaya.interfaces.IPlayerCallback;
import com.example.himalaya.presenters.PlayerPresenter;
import com.example.himalaya.views.PlayPopWindow;
import com.example.himalaya.views.PopWindowBgChange;
import com.example.himalaya.views.TimePopWindow;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import java.text.SimpleDateFormat;
import java.util.List;

import androidx.viewpager.widget.ViewPager;

import static com.example.himalaya.utils.PlayModeUtil.sIntegerPlayModeMap;
import static com.example.himalaya.utils.PlayModeUtil.upDatePlayModeBtnImg;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_LIST;

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

    private ImageView mPlayListBtn;
    private PlayPopWindow mSobPopWindow;
    private TimePopWindow mTimePopWindow;
    private ImageView mBackFifIv;
    private ImageView mFrontFifIv;
    private long mCurrentProgress = 0;
    private ProgressBar mProgressBar;
    private ImageView mTimeCloseIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        mInstance = PlayerPresenter.getInstance();
        initView();
        mInstance.registerViewCallback(this);
        mInstance.getPlayList();
        initEvent();
        //列表弹出时背景变化
        PopWindowBgChange.initBgAnimation(getWindow());
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

        //展示播放列表
        mPlayListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //从下到上出现
                mSobPopWindow.showAtLocation(v, Gravity.BOTTOM, 0, 0);
                //背景透明度
                PopWindowBgChange.mEnterBgAnimator.start();
            }
        });
        mSobPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                //窗体消失时恢复
                //mOutBgAnimator.start();
                PopWindowBgChange.mOutBgAnimator.start();
            }
        });

        //列表点击切换歌曲
        mSobPopWindow.setOnItemClickListener(new PlayPopWindow.onItemClickListener() {
            @Override
            public void onClick(int pos) {
                if (mInstance != null) {
                    mInstance.playByIndex(pos);
                }
            }
        });

        //列表切换播放模式和顺序
        mSobPopWindow.setPlayListActionListener(new PlayPopWindow.playListActionListener() {
            @Override
            public void onPlayModeClick() {
                //切换播放模式
                switchPlayMode();
            }

            @Override
            public void onPlayOrderClick() {
                if (mInstance != null) {
                    mInstance.reversePlayList();
                }
            }
        });

        //后退15秒
        mBackFifIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mInstance != null) {
                    long pos;
                    if (mCurrentProgress > 15000) {
                        pos = mCurrentProgress - 15000;
                    } else {
                        pos = 0;
                    }
                    mInstance.seekTo((int) pos);
                }
            }
        });

        //前进15秒
        mFrontFifIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mInstance != null) {
                    long pos;
                    if (mCurrentProgress + 15000 < mTotalSeek) {
                        pos = mCurrentProgress + 15000;
                    } else {
                        pos = mTotalSeek;
                    }
                    mInstance.seekTo((int) pos);
                }
            }
        });

        mTimeCloseIv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mTimePopWindow.showAtLocation(v,Gravity.BOTTOM,0,0);
                PopWindowBgChange.mEnterBgAnimator.start();
            }
        });
        mTimePopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                PopWindowBgChange.mOutBgAnimator.start();
            }
        });
    }

    private void switchPlayMode() {
        XmPlayListControl.PlayMode playMode = sIntegerPlayModeMap.get(mCurrentPlayMode);
        if (mInstance != null) {
            mInstance.switchPlayMode(playMode);
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
        mPlayListBtn = findViewById(R.id.player_list);
        mBackFifIv = findViewById(R.id.backFifIv);
        mFrontFifIv = findViewById(R.id.frontFifIv);
        mProgressBar = findViewById(R.id.progressPlayer);
        mTimeCloseIv = findViewById(R.id.timeCloseIv);

        //创建适配器
        mPlayerTrackPagerAdapter = new PlayerTrackPagerAdapter();
        mTrackViewPager.setAdapter(mPlayerTrackPagerAdapter);

        if (mInstance!=null) {
            //初始化图标
            if (mInstance.isPlaying()) {
                if (mControlBtn != null) {
                    mControlBtn.setImageResource(R.mipmap.stop_normal);
                }
            } else {
                if (mControlBtn != null) {
                    mControlBtn.setImageResource(R.mipmap.play_press);
                }
            }
        }

        //播放列表弹窗
        mSobPopWindow = new PlayPopWindow();
        mTimePopWindow = new TimePopWindow();

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
    public void onListLoaded(List<Track> list) {
        //Viewpager的适配器
        if (mPlayerTrackPagerAdapter != null) {
            mPlayerTrackPagerAdapter.setData(list);
        }
        //给列表适配器
        if (mSobPopWindow != null) {
            mSobPopWindow.setListData(list);
        }


    }

    @Override
    public void onPlayModeChange(XmPlayListControl.PlayMode mode) {
        mCurrentPlayMode = mode;
        mPlayModeSwitchBtn.setImageResource(upDatePlayModeBtnImg(mCurrentPlayMode));
        mSobPopWindow.setView(upDatePlayModeBtnImg(mCurrentPlayMode));
    }

    @Override
    public void onProgressChange(long currentProgress, long total) {
        mTotalSeek = total;
        mCurrentProgress = currentProgress;
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

        if (mSobPopWindow != null) {
            mSobPopWindow.setCurrentIndex(index);
        }

    }

    @Override
    public void updateListOrder(boolean isReverse) {
        mSobPopWindow.upDateOrderIcon(isReverse);
    }

    @Override
    public void onBufferStart() {
        //缓冲
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBufferStop() {
        mProgressBar.setVisibility(View.GONE);
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
