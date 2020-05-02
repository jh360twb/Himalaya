package com.example.himalaya;

import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.himalaya.adapters.IndicatorAdapter;
import com.example.himalaya.adapters.MainContentAdapter;
import com.example.himalaya.base.BaseActivity;
import com.example.himalaya.interfaces.IPlayerCallback;
import com.example.himalaya.presenters.PlayerPresenter;
import com.example.himalaya.presenters.RecommendPresenter;
import com.example.himalaya.utils.LogUtil;
import com.example.himalaya.views.RoundRectImageView;
import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import java.util.List;

public class MainActivity extends BaseActivity implements IPlayerCallback {
    private static final String TAG = "MainActivity";
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initPresenter();
        initEvent();
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
    }

    private void getFirstTrackOfFirstAlbum() {
        List<Album> currentRecommand = RecommendPresenter.getInstance().getCurrentRecommand();
        if (currentRecommand!=null && currentRecommand.size()>0){
            Album album = currentRecommand.get(0);
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
        MainContentAdapter mainContentAdapter = new MainContentAdapter(supportFragmentManager, 1);
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
        //搜索
        mSearchBtn = this.findViewById(R.id.search_btn);
    }

    @Override
    public void onPlayStart() {
        mPlayControl.setImageResource(R.drawable.selector_player_pause);
    }

    @Override
    public void onPlayPause() {
        mPlayControl.setImageResource(R.drawable.selector_player_play);
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
    }

    @Override
    public void onPlayModeChange(XmPlayListControl.PlayMode mode) {

    }

    @Override
    public void onProgressChange(long currentProgress, long total) {

    }

    @Override
    public void onAdLoading() {

    }

    @Override
    public void onAdFinished() {

    }

    @Override
    public void onTrackUpdate(Track track, int index) {
        if (track != null) {
            Picasso.with(this).load(track.getCoverUrlMiddle()).into(mRoundRectImageView);
            mHeaderTitle.setText(track.getTrackTitle());
            mSubTitle.setText(track.getAnnouncer().getNickname());
        }
    }

    @Override
    public void updateListOrder(boolean isReverse) {

    }
}
