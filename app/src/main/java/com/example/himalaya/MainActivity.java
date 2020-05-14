package com.example.himalaya;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.Notification;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.himalaya.adapters.IndicatorAdapter;
import com.example.himalaya.adapters.MainContentAdapter;
import com.example.himalaya.data.XimalayaApi;
import com.example.himalaya.base.BaseActivity;
import com.example.himalaya.data.XimalayaDBHelper;
import com.example.himalaya.interfaces.IPlayerCallback;
import com.example.himalaya.presenters.PlayerPresenter;
import com.example.himalaya.presenters.RecommendPresenter;
import com.example.himalaya.utils.LogUtil;
import com.example.himalaya.views.PopWindowBgChange;
import com.example.himalaya.views.RoundRectImageView;
import com.example.himalaya.views.SobPopWindow;
import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.appnotification.XmNotificationCreater;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import java.util.ArrayList;
import java.util.List;

import static com.example.himalaya.utils.PlayModeUtil.sIntegerPlayModeMap;
import static com.example.himalaya.utils.PlayModeUtil.upDatePlayModeBtnImg;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_LIST;

public class MainActivity extends BaseActivity implements IPlayerCallback {
    private static final String TAG = "MainActivity";
    List<String> notPermission = new ArrayList<>();
    MagicIndicator magicIndicator;
    private IndicatorAdapter indicatorAdapter;
    private ViewPager viewPager;
    private RoundRectImageView mRoundRectImageView;
    private TextView mHeaderTitle;
    private TextView mSubTitle;
    private ImageView mPlayControl;
    private PlayerPresenter mPlayerPresenter;
    private View mPlayControlItem;
    private View mSearchBtn;
    private ImageView mPlayListIv;
    private SobPopWindow mSobPopWindow;
    //当前播放模式
    private XmPlayListControl.PlayMode mCurrentPlayMode = PLAY_MODEL_LIST;
    private XmPlayerManager mXmPlayerManager;
    private ProgressBar mProgressBar;
    private String mCurrentNickname;
    private String mCurrentTrackTitle;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initPresenter();
        initEvent();
        initPermission();
        initReceiver();
    }

    private void initReceiver() {
        mXmPlayerManager = XmPlayerManager.getInstance(MainActivity.this);
        Notification mNotification = XmNotificationCreater.getInstanse(this).initNotification(this.getApplicationContext(), MainActivity.class);
        mXmPlayerManager.init((int) System.currentTimeMillis(), mNotification);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void initPermission() {
        notPermission.clear();
        String[] permissions = new String[]{
                Manifest.permission.INTERNET,
        };
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this,permission)!= PackageManager.PERMISSION_GRANTED){
                notPermission.add(permission);
            }
        }
        if (notPermission.size()>0){
            ActivityCompat.requestPermissions(this,permissions,1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean permissed = false;
        if (requestCode == 1){
            for (int i=0;i<permissions.length;i++){
                if (grantResults[i] == -1){
                    permissed = true;
                }
            }

            if (permissed){
                Toast.makeText(this, "权限未通过", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, "权限通过", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        PopWindowBgChange.initBgAnimation(getWindow());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPlayerPresenter != null) {
            mPlayerPresenter.unRegisterViewCallback(this);
        }
    }

    private void initPresenter() {
        mPlayerPresenter = PlayerPresenter.getInstance();
        mPlayerPresenter.registerViewCallback(this);
        mPlayerPresenter.getPlayList();
    }

    private void initEvent() {
        //导航栏
        indicatorAdapter.setOnIndicatorTabClickListener(new IndicatorAdapter.OnIndicatorTabClickListener() {
            @Override
            public void onTabClick(int index) {
                LogUtil.e(TAG, "index --->" + index);
                if (viewPager != null) {
                    viewPager.setCurrentItem(index);
                }
            }
        });
        //跳转到播放详情页面
        mPlayControlItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayerPresenter != null) {
                    boolean has = mPlayerPresenter.hasPlayList();
                    if (has){
                        Intent intent = new Intent(MainActivity.this,PlayerActivity.class);
                        startActivity(intent);
                    }else {
                        //没有列表,获取第一个专辑的第一首
                        getFirstTrackOfFirstAlbum();
                    }
                }
            }
        });

        //播放控制
        mPlayControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayerPresenter != null) {
                    boolean has = mPlayerPresenter.hasPlayList();
                    if (has){
                        if (mPlayerPresenter.isPlaying()) {
                            mPlayerPresenter.pause();
                        } else {
                            mPlayerPresenter.play();
                        }
                    }else {
                        //没有列表,获取第一个专辑的第一首
                        getFirstTrackOfFirstAlbum();
                    }
                }
            }
        });

        //弹出列表
        mPlayListIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayerPresenter != null) {
                    boolean has = mPlayerPresenter.hasPlayList();
                    if (has){
                        mPlayerPresenter.getPlayList();
                    }else {
                        getTracksOfFirstAlbum();
                    }
                }
                mSobPopWindow.showAtLocation(v, Gravity.BOTTOM,0,0);
                //背景透明度
                PopWindowBgChange.mEnterBgAnimator.start();
               // mAlbumDetailPresenter.setIfCanClick(false);
            }
        });

        mSobPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                PopWindowBgChange.mOutBgAnimator.start();
            }
        });


        //列表点击切换歌曲
        mSobPopWindow.setOnItemClickListener(new SobPopWindow.onItemClickListener() {
            @Override
            public void onClick(int pos) {
                if (mPlayerPresenter != null) {
                    mPlayerPresenter.playByIndex(pos);
                }
            }
        });

        //列表切换播放模式和顺序
        mSobPopWindow.setPlayListActionListener(new SobPopWindow.playListActionListener() {
            @Override
            public void onPlayModeClick() {
                //切换播放模式
                switchPlayMode();
            }

            @Override
            public void onPlayOrderClick() {
                if (mPlayerPresenter != null) {
                    mPlayerPresenter.reversePlayList();
                }
            }
        });

        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,SearchActivity.class);
                startActivity(intent);
            }
        });
    }

    private void switchPlayMode() {
        XmPlayListControl.PlayMode playMode = sIntegerPlayModeMap.get(mCurrentPlayMode);
        if (mPlayerPresenter != null) {
            mPlayerPresenter.switchPlayMode(playMode);
        }
    }

    //获取第一专辑的列表
    private void getTracksOfFirstAlbum() {
        List<Album> currentRecommand = RecommendPresenter.getInstance().getCurrentRecommand();
        if (currentRecommand!=null && currentRecommand.size()>0) {
            Album album = currentRecommand.get(0);
            XimalayaApi instance = XimalayaApi.getInstance();
            instance.getAlbumDetail(new IDataCallBack<TrackList>() {
                @Override
                public void onSuccess(TrackList trackList) {
                    mPlayerPresenter.setPlayList(trackList.getTracks(),0);
                    mPlayerPresenter.getPlayList();
                }

                @Override
                public void onError(int i, String s) {
                    Toast.makeText(MainActivity.this, "请求失败", Toast.LENGTH_SHORT).show();
                }
            },album.getId(),1);
        }
    }

    //获取第一专辑的第一首歌
    private void getFirstTrackOfFirstAlbum() {
        List<Album> currentRecommend = RecommendPresenter.getInstance().getCurrentRecommand();
        if (currentRecommend!=null && currentRecommend.size()>0){
            Album album = currentRecommend.get(0);
            long id = album.getId();
            mPlayerPresenter.playByAlbumId(id);
        }
    }


    private void initView() {
        magicIndicator = this.findViewById(R.id.main_indicator);
        magicIndicator.setBackgroundColor(this.getResources().getColor(R.color.main_color));
        //创建indicator的适配器
        indicatorAdapter = new IndicatorAdapter(this);
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdjustMode(true);
        commonNavigator.setAdapter(indicatorAdapter);

        //ViewPager
        viewPager = this.findViewById(R.id.content_pager);

        //创建内容适配器
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        MainContentAdapter mainContentAdapter = new MainContentAdapter(supportFragmentManager);
        viewPager.setAdapter(mainContentAdapter);
        //把ViewPager和indicator绑定起来
        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator, viewPager);

        //播放控制相关的
        mRoundRectImageView = this.findViewById(R.id.main_track_cover);
        mHeaderTitle = this.findViewById(R.id.main_head_title);
        mHeaderTitle.setSelected(true);
        mSubTitle = this.findViewById(R.id.main_sub_title);
        mPlayControl = this.findViewById(R.id.main_play_control);
        mPlayControlItem = this.findViewById(R.id.main_play_control_item);
        mPlayListIv = this.findViewById(R.id.main_play_list);
        //搜索
        mSearchBtn = this.findViewById(R.id.search_btn);
        //列表弹窗
        mSobPopWindow = new SobPopWindow();
    }

    @Override
    public void onPlayStart() {
        mPlayControl.setImageResource(R.drawable.selector_player_pause);
    }

    @Override
    public void onPlayPause() {
        mPlayControl.setImageResource(R.mipmap.play_press);
    }


    @Override
    public void onListLoaded(List<Track> list) {
        //给列表适配器
        if (mSobPopWindow != null) {
            mSobPopWindow.setListData(list);
        }
    }

    @Override
    public void onPlayModeChange(XmPlayListControl.PlayMode mode) {
        mCurrentPlayMode = mode;
        mSobPopWindow.setView(upDatePlayModeBtnImg(mCurrentPlayMode));
    }

    @Override
    public void onProgressChange(long currentProgress, long total) {

    }


    @Override
    public void onTrackUpdate(Track track, int index) {
        if (track != null) {
            Picasso.with(this).load(track.getCoverUrlLarge()).into(mRoundRectImageView);
            mCurrentTrackTitle = track.getTrackTitle();
            mCurrentNickname = track.getAnnouncer().getNickname();
            mHeaderTitle.setText(mCurrentTrackTitle);
            mSubTitle.setText(mCurrentNickname);
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
        mHeaderTitle.setText("正在缓冲");
        mSubTitle.setText("请等待");
    }

    @Override
    public void onBufferStop() {
        mHeaderTitle.setText(mCurrentTrackTitle);
        mSubTitle.setText(mCurrentNickname);
    }
}
