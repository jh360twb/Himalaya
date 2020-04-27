package com.example.himalaya;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.himalaya.adapters.TracksListAdapter;
import com.example.himalaya.base.BaseActivity;
import com.example.himalaya.interfaces.IAlbumDetailViewCallback;
import com.example.himalaya.presenters.AlbumDetailPresenter;
import com.example.himalaya.presenters.PlayerPresenter;
import com.example.himalaya.utils.ImageBlur;
import com.example.himalaya.utils.LogUtil;
import com.example.himalaya.views.UILoader;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.List;

public class DetailActivity extends BaseActivity implements IAlbumDetailViewCallback, UILoader.onRetryClickListener,TracksListAdapter.onTrackClickListener{

    private ImageView largeCover;
    private TextView albumTitle;
    private TextView albumAuthor;
    private ImageView smallCover;
    private AlbumDetailPresenter albumDetailPresenter;
    private static final String TAG = "DetailActivity";
    private TracksListAdapter tracksListAdapter;
    private RecyclerView tracks_list;
    private UILoader uiLoader;
    private FrameLayout detail_list;
    private Album currentAlbum;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        initView();
        albumDetailPresenter = AlbumDetailPresenter.getInstance();
        albumDetailPresenter.registerViewCallback(this);

    }

    private void initView() {
        detail_list = this.findViewById(R.id.detail_list);
        largeCover = this.findViewById(R.id.iv_large_cover);
        smallCover = this.findViewById(R.id.viv_small_cover);
        albumTitle = this.findViewById(R.id.tv_album_title);
        albumAuthor = this.findViewById(R.id.tv_album_author);
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

    private View createSuccessView(ViewGroup container) {
        View view = LayoutInflater.from(this).inflate(R.layout.item_succ_tracks_list,container,false);
        tracks_list = view.findViewById(R.id.tracks_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        tracks_list.setLayoutManager(linearLayoutManager);
        tracksListAdapter = new TracksListAdapter();
        tracksListAdapter.setonTrackClickListener(this);
        tracks_list.setAdapter(tracksListAdapter);
        tracks_list.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.bottom = UIUtil.dip2px(view.getContext(), 2);
                outRect.top = UIUtil.dip2px(view.getContext(), 2);
                outRect.left = UIUtil.dip2px(view.getContext(), 5);
                outRect.right = UIUtil.dip2px(view.getContext(), 5);
            }
        });
        return view;
    }

    @Override
    public void onDetailListLoaded(List<Track> tracks) {
        if (tracks == null || tracks.size() == 0) {
            if (uiLoader != null) {
                uiLoader.updateStatus(UILoader.UIStatus.EMPTY);
            }
        }else {
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
        albumDetailPresenter.getAlbumDetail(album.getId(), 1);

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
    public void onRetryClick() {
        if (albumDetailPresenter != null) {
            albumDetailPresenter.getAlbumDetail(currentAlbum.getId(),1);
        }
    }


    @Override
    public void onClick(List<Track> list, int position) {
        //设置播放器数据
        PlayerPresenter instance = PlayerPresenter.getInstance();
        instance.setPlayList(list,position);
        // LogUtil.e(TAG,position+"");
        startActivity(new Intent(DetailActivity.this,PlayerActivity.class));
    }
}
