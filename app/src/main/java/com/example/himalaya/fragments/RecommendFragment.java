package com.example.himalaya.fragments;

import android.content.Intent;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.himalaya.DetailActivity;
import com.example.himalaya.R;
import com.example.himalaya.adapters.AlbumListAdapter;
import com.example.himalaya.base.BaseFragment;
import com.example.himalaya.interfaces.IRecommendViewCallback;
import com.example.himalaya.presenters.AlbumDetailPresenter;
import com.example.himalaya.presenters.RecommendPresenter;
import com.example.himalaya.utils.LogUtil;
import com.example.himalaya.views.UILoader;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class RecommendFragment extends BaseFragment implements IRecommendViewCallback, UILoader.onRetryClickListener, AlbumListAdapter.onAlbumItemClickListener {
    private static final String TAG = "RecommendFragment";
    private View view;
    private AlbumListAdapter recommendListAdapter;
    private RecommendPresenter mRecommendPresenter;
    private UILoader uiLoader;

    @Override
    protected View onSubViewLoaded(final LayoutInflater layoutInflater, ViewGroup container) {
        uiLoader = new UILoader(getContext()) {
            @Override
            protected View getSuccessView(ViewGroup container) {
                return createSuccessView(layoutInflater, container);
            }
        };

        //获取到逻辑层的对象
        mRecommendPresenter = RecommendPresenter.getInstance();
        //先要注册通知接口的设置
        mRecommendPresenter.registerViewCallback(this);
        //获取推荐列表
        mRecommendPresenter.getRecommendList();

        //解绑
        if (uiLoader.getParent() instanceof ViewGroup) {
            ((ViewGroup) uiLoader.getParent()).removeView(uiLoader);
        }

        uiLoader.setonRetryClickListener(this);

        return uiLoader;
    }

    //成功显示的界面
    private View createSuccessView(LayoutInflater layoutInflater, ViewGroup container) {
        view = layoutInflater.inflate(R.layout.fragment_recommend, container, false);
        //View加载完成
        //开源的框架
        TwinklingRefreshLayout scrollView = view.findViewById(R.id.scroll_view);
        scrollView.setPureScrollModeOn();
        RecyclerView recommend_list = view.findViewById(R.id.recommend_list);
        //布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recommend_list.setLayoutManager(linearLayoutManager);
        //设置间距
        recommend_list.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.bottom = UIUtil.dip2px(view.getContext(), 5);
                outRect.top = UIUtil.dip2px(view.getContext(), 5);
                outRect.left = UIUtil.dip2px(view.getContext(), 5);
                outRect.right = UIUtil.dip2px(view.getContext(), 5);
            }
        });
        //设置适配器
        recommendListAdapter = new AlbumListAdapter();
        recommendListAdapter.setonAlbumItemClickListener(this);
        recommend_list.setAdapter(recommendListAdapter);
        return view;
    }


    @Override
    public void onRecommendListLoaded(List<Album> result) {
        LogUtil.e(TAG, "onRecommendListLoaded");
        //当我们获取到推荐内容时,方法就会被调用
        //数据回来之后更新UI
        recommendListAdapter.setData(result);
        uiLoader.updateStatus(UILoader.UIStatus.SUCCESS);
    }

    @Override
    public void onNetworkError() {
        LogUtil.e(TAG, "onNetworkError");

        uiLoader.updateStatus(UILoader.UIStatus.NETWORK_ERROR);

    }

    @Override
    public void onEmpty() {
        LogUtil.e(TAG, "onEmpty");

        uiLoader.updateStatus(UILoader.UIStatus.EMPTY);

    }

    @Override
    public void onLoading() {
        LogUtil.e(TAG, "onLoading");

        uiLoader.updateStatus(UILoader.UIStatus.LOADING);

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //取消接口的注册
        if (mRecommendPresenter != null) {
            mRecommendPresenter.unRegisterViewCallback(this);
        }
    }

    @Override
    public void onRetryClick() {
        //todo:表示网络不佳用户点击重试
        if (mRecommendPresenter != null) {
            mRecommendPresenter.getRecommendList();
        }
    }

    @Override
    public void onItemClick(int position, Album album) {
        AlbumDetailPresenter presenter = AlbumDetailPresenter.getInstance();
        presenter.setTargetAlbum(album);
            Intent intent = new Intent(getContext(), DetailActivity.class);
            startActivity(intent);
    }
}
