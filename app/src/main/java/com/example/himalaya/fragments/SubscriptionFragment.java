package com.example.himalaya.fragments;

import android.content.Intent;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.himalaya.DetailActivity;
import com.example.himalaya.R;
import com.example.himalaya.adapters.AlbumListAdapter;
import com.example.himalaya.base.BaseFragment;
import com.example.himalaya.interfaces.ISubscriptionCallback;
import com.example.himalaya.presenters.AlbumDetailPresenter;
import com.example.himalaya.presenters.SubscriptionPresenter;
import com.example.himalaya.views.ConfirmDialog;
import com.example.himalaya.views.UILoader;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SubscriptionFragment extends BaseFragment implements ISubscriptionCallback, AlbumListAdapter.onAlbumItemClickListener, AlbumListAdapter.onAlbumItemLongClickListener, ConfirmDialog.ItemLongClickListener {

    private SubscriptionPresenter mSubscriptionPresenter;
    private RecyclerView mSubListView;
    private AlbumListAdapter mAlbumListAdapter;
    private Album mAlbum = null;
    private UILoader mUILoader;

    @Override
    protected View onSubViewLoaded(final LayoutInflater layoutInflater, ViewGroup container) {
        FrameLayout view = (FrameLayout) layoutInflater.inflate(R.layout.fragment_subscription, container, false);
        mUILoader = new UILoader(container.getContext()) {
            @Override
            protected View getSuccessView(ViewGroup container) {
                return createSuccessView(layoutInflater, container);
            }

            @Override
            protected View getEmptyView() {
                View emptyView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_empty_view, this, false);
                TextView emptyTv = emptyView.findViewById(R.id.emptyTv);
                emptyTv.setText("没有订阅哦,赶快去订阅吧");
                return emptyView;
            }
        };

        initPresenter();

        if (mUILoader.getParent() instanceof ViewGroup) {
            ((ViewGroup) mUILoader.getParent()).removeView(mUILoader);
        }

        view.addView(mUILoader);
        return view;
    }

    private void initPresenter() {
        mSubscriptionPresenter = SubscriptionPresenter.getInstance();
        mSubscriptionPresenter.registerViewCallback(this);
        mSubscriptionPresenter.getSubscriptionList();
    }

    private View createSuccessView(LayoutInflater layoutInflater, ViewGroup container) {
        View itemView = layoutInflater.inflate(R.layout.succ_subscription, container, false);
        TwinklingRefreshLayout twinklingRefreshLayout = itemView.findViewById(R.id.sub_scroll_view);
        twinklingRefreshLayout.setEnableRefresh(false);
        twinklingRefreshLayout.setEnableLoadmore(false);
        mSubListView = itemView.findViewById(R.id.subscription_list);
        mSubListView.setLayoutManager(new LinearLayoutManager(container.getContext()));
        mAlbumListAdapter = new AlbumListAdapter();
        mAlbumListAdapter.setonAlbumItemClickListener(this);
        mAlbumListAdapter.setOnAlbumItemLongClickListener(this);
        mSubListView.setAdapter(mAlbumListAdapter);
        mSubListView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.bottom = UIUtil.dip2px(view.getContext(), 5);
                outRect.top = UIUtil.dip2px(view.getContext(), 5);
                outRect.left = UIUtil.dip2px(view.getContext(), 5);
                outRect.right = UIUtil.dip2px(view.getContext(), 5);
            }
        });
        if (mUILoader != null) {
            mUILoader.updateStatus(UILoader.UIStatus.LOADING);
        }
        return itemView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //取消接口的注册
        if (mSubscriptionPresenter != null) {
            mSubscriptionPresenter.unRegisterViewCallback(this);
        }
        //置空
        mAlbumListAdapter.setonAlbumItemClickListener(null);
    }

    @Override
    public void onAddResult(boolean isSuccess) {

    }

    @Override
    public void onDeleteResult(boolean isSuccess) {

    }

    @Override
    public void onSubscriptionsLoaded(List<Album> albums) {
        if (albums.size()==0) {
            mUILoader.updateStatus(UILoader.UIStatus.EMPTY);
        }else {
            mUILoader.updateStatus(UILoader.UIStatus.SUCCESS);
        }
        if (mAlbumListAdapter != null) {
            mAlbumListAdapter.setData(albums);
        }
    }

    @Override
    public void onSubFull() {
        //Toast.makeText(getActivity(), "订阅数量不得超过" + Constants.MAX_SUB_COUNT+"个", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(int position, Album album) {
        AlbumDetailPresenter presenter = AlbumDetailPresenter.getInstance();
        presenter.setTargetAlbum(album);
        Intent intent = new Intent(getContext(), DetailActivity.class);
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(int pos, Album album) {
        mAlbum = album;
        ConfirmDialog confirmDialog = new ConfirmDialog(getActivity());
        confirmDialog.setItemLongClickListener(this);
        confirmDialog.show();
    }

    @Override
    public void onItemCancel() {
        if (mAlbum != null && mSubscriptionPresenter != null) {
            mSubscriptionPresenter.deleteSubscription(mAlbum);
        }
    }

    @Override
    public void onItemContinue() {
        //继续订阅
    }
}
