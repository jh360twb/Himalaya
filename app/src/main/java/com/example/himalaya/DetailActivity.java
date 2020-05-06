package com.example.himalaya;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.himalaya.adapters.TracksListAdapter;
import com.example.himalaya.base.BaseActivity;
import com.example.himalaya.base.BaseApplication;
import com.example.himalaya.interfaces.IAlbumDetailViewCallback;
import com.example.himalaya.interfaces.IPlayerCallback;
import com.example.himalaya.presenters.AlbumDetailPresenter;
import com.example.himalaya.presenters.PlayerPresenter;
import com.example.himalaya.utils.ImageBlur;
import com.example.himalaya.utils.LogUtil;
import com.example.himalaya.views.UILoader;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.List;

public class DetailActivity extends BaseActivity implements IAlbumDetailViewCallback, UILoader.onRetryClickListener, TracksListAdapter.onTrackClickListener, IPlayerCallback {

    private ImageView largeCover;
    private TextView albumTitle;
    private TextView albumAuthor;
    private ImageView smallCover;
    private AlbumDetailPresenter mAlbumDetailPresenter;
    private static final String TAG = "DetailActivity";
    private TracksListAdapter tracksListAdapter;
    private RecyclerView tracks_list;
    private UILoader uiLoader;
    private FrameLayout detail_list;
    private Album currentAlbum;
    private View mDetailPlayController;
    private ImageView mDetailPlayIv;
    private TextView mDetailPlayTv;
    private PlayerPresenter mPlayerPresenter;
    private List<Track> mCurrentTracks;
    public static String mCurrentTitle;
    private TwinklingRefreshLayout mRefreshLayout;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        //屏幕沉浸式
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        initView();
        initPresenter();
        //LogUtil.e(TAG,"mCurrentTitle" + mCurrentTitle);
        //改变播放状态
        updatePlayState(mPlayerPresenter.isPlaying());
        //初始化点击事件
        initEvent();
    }

    private void updatePlayState(boolean playing) {
        if (mDetailPlayTv != null && mCurrentTitle != null) {
            mDetailPlayIv.setImageResource(playing ? R.drawable.selector_play_control_pause :
                    R.drawable.selector_play_control_play);
            // LogUtil.e(TAG,"mCurrentTrack.getTrackTitle() -> "+mCurrentTrack.getTrackTitle());
            if (!playing) {
                mDetailPlayTv.setText("点击播放");
            } else {
                if (!TextUtils.isEmpty(mCurrentTitle)) {
                    mDetailPlayTv.setText(mCurrentTitle);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPlayerPresenter != null) {
            mPlayerPresenter.unRegisterViewCallback(this);
        }
        if (mAlbumDetailPresenter != null) {
            mAlbumDetailPresenter.unRegisterViewCallback(this);
        }

    }

    private void initPresenter() {
        //专辑列表相关
        mAlbumDetailPresenter = AlbumDetailPresenter.getInstance();
        mAlbumDetailPresenter.registerViewCallback(this);
        //播放器
        mPlayerPresenter = PlayerPresenter.getInstance();
        mPlayerPresenter.registerViewCallback(this);
    }

    private void initEvent() {
        mDetailPlayController.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean has = mPlayerPresenter.hasPlayList();
                if (has) {
                    handlePlayControl();
                } else {
                    handleNoPlayListControl();
                }
            }
        });
    }

    private void handleNoPlayListControl() {
        mPlayerPresenter.setPlayList(mCurrentTracks, 0);
    }

    //播放按钮
    private void handlePlayControl() {
        if (mPlayerPresenter.isPlaying()) {
            mPlayerPresenter.pause();
        } else {
            mPlayerPresenter.play();
        }
    }

    private void initView() {
        detail_list = this.findViewById(R.id.detail_list);
        largeCover = this.findViewById(R.id.iv_large_cover);
        smallCover = this.findViewById(R.id.viv_small_cover);
        albumTitle = this.findViewById(R.id.tv_album_title);
        albumAuthor = this.findViewById(R.id.tv_album_author);
        mDetailPlayController = findViewById(R.id.detail_play_controller);
        mDetailPlayIv = findViewById(R.id.detail_play_iv);
        mDetailPlayTv = findViewById(R.id.detail_play_tv);
        if (uiLoader == null) {
            uiLoader = new UILoader(this) {
                @Override
                protected View getSuccessView(ViewGroup container) {
                    return createSuccessView(container);
                }
            };
        }
        detail_list.removeAllViews();
        detail_list.addView(uiLoader);
        uiLoader.setonRetryClickListener(DetailActivity.this);
    }

    @SuppressLint("ResourceAsColor")
    private View createSuccessView(ViewGroup container) {
        final View view = LayoutInflater.from(this).inflate(R.layout.succ_tracks_list, container, false);
        mRefreshLayout = view.findViewById(R.id.refresh_layout);
        tracks_list = view.findViewById(R.id.tracks_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        tracks_list.setLayoutManager(linearLayoutManager);
        tracksListAdapter = new TracksListAdapter();
        tracksListAdapter.setonTrackClickListener(this);
        tracks_list.setAdapter(tracksListAdapter);

        //专辑列表
        tracks_list.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.bottom = UIUtil.dip2px(view.getContext(), 2);
                outRect.top = UIUtil.dip2px(view.getContext(), 2);
                outRect.left = UIUtil.dip2px(view.getContext(), 5);
                outRect.right = UIUtil.dip2px(view.getContext(), 5);
            }
        });

        //刷新下拉框架监听
        RefreshListenerAdapter refreshListenerAdapter = new RefreshListenerAdapter() {
            @Override
            public void onRefresh(TwinklingRefreshLayout refreshLayout) {
                super.onRefresh(refreshLayout);
                if (mAlbumDetailPresenter != null) {
                    mAlbumDetailPresenter.refresh();
                }
                mRefreshLayout.finishRefreshing();
            }

            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                super.onLoadMore(refreshLayout);
                if (mAlbumDetailPresenter != null) {
                    mAlbumDetailPresenter.loadMore();
                }
                mRefreshLayout.finishLoadmore();
            }
        };
        mRefreshLayout.setOnRefreshListener(refreshListenerAdapter);
        return view;
    }

    @Override
    public void onDetailListLoaded(List<Track> tracks) {
        mCurrentTracks = tracks;
        if (tracks == null || tracks.size() == 0) {
            if (uiLoader != null) {
                uiLoader.updateStatus(UILoader.UIStatus.EMPTY);
            }
        } else {
            if (uiLoader != null) {
                uiLoader.updateStatus(UILoader.UIStatus.SUCCESS);
                tracksListAdapter.setData(tracks);
            }
        }
        // Log.e(TAG, "onDetailListLoaded: "+tracks.get(0));

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onAlbumLoaded(Album album) {
        currentAlbum = album;
        mAlbumDetailPresenter.getAlbumDetail(album.getId(), 1);

        if (albumTitle != null) {
            albumTitle.setText(album.getAlbumTitle());
        }

        if (albumAuthor != null) {
            albumAuthor.setText(album.getAnnouncer().getNickname());
        }

        if (largeCover != null) {
            Picasso.with(DetailActivity.this).load(album.getCoverUrlLarge()).into(largeCover, new Callback() {
                @Override
                public void onSuccess() {
                    Drawable drawable = largeCover.getDrawable();
                    if (drawable != null) {
                        ImageBlur.makeBlur(largeCover, DetailActivity.this);
                    }
                }

                @Override
                public void onError() {
                    LogUtil.e(TAG, "error");
                }
            });
        }

        if (smallCover != null) {
            Picasso.with(DetailActivity.this).load(album.getCoverUrlSmall()).into(smallCover);
        }
    }

    @Override
    public void onNetworkError(int i, String s) {
        LogUtil.e(TAG, "onNetworkError" + i + s);
        if (uiLoader != null) {
            uiLoader.updateStatus(UILoader.UIStatus.NETWORK_ERROR);
        }
    }

    @Override
    public void onLoading() {
        if (uiLoader != null) {
            uiLoader.updateStatus(UILoader.UIStatus.LOADING);
        }
    }

    @Override
    public void onLoadMoreFinished(int size) {
        if (size>0){
            Toast.makeText(BaseApplication.getAppContext(), "新加载了"+size+"条节目", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(BaseApplication.getAppContext(), "没有更多节目", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public void onRefreshFinished() {
        Toast.makeText(BaseApplication.getAppContext(), "刷新成功", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRetryClick() {
        if (mAlbumDetailPresenter != null) {
            mAlbumDetailPresenter.getAlbumDetail(currentAlbum.getId(), 1);
        }
    }


    @Override
    public void onItemClick(List<Track> list, int position) {
        //设置播放器数据
        PlayerPresenter instance = PlayerPresenter.getInstance();
        instance.setPlayList(list, position);
        // LogUtil.e(TAG,position+"");
        startActivity(new Intent(DetailActivity.this, PlayerActivity.class));
    }

    @Override
    public void onPlayStart() {
        //修改图标为暂停的，文字修改为正在播放.
        updatePlayState(true);
    }

    @Override
    public void onPlayPause() {
        updatePlayState(false);
    }

    @Override
    public void onPlayStop() {
        updatePlayState(false);
    }

    @Override
    public void onTrackUpdate(Track track, int index) {
        if (track != null) {
            mCurrentTitle = track.getTrackTitle();
            if (!TextUtils.isEmpty(mCurrentTitle) && mDetailPlayTv != null) {
                mDetailPlayTv.setText(mCurrentTitle);
            }
        }
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
    public void updateListOrder(boolean isReverse) {

    }
}
