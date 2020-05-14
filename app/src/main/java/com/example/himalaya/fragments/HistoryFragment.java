package com.example.himalaya.fragments;

import android.content.Intent;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.example.himalaya.DetailActivity;
import com.example.himalaya.PlayerActivity;
import com.example.himalaya.R;
import com.example.himalaya.adapters.AlbumListAdapter;
import com.example.himalaya.adapters.TracksListAdapter;
import com.example.himalaya.base.BaseFragment;
import com.example.himalaya.interfaces.IHistoryCallback;
import com.example.himalaya.presenters.HistoryPresenter;
import com.example.himalaya.presenters.PlayerPresenter;
import com.example.himalaya.utils.LogUtil;
import com.example.himalaya.views.ConfirmCheckBoxDialog;
import com.example.himalaya.views.ConfirmDialog;
import com.example.himalaya.views.UILoader;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class HistoryFragment extends BaseFragment implements IHistoryCallback, TracksListAdapter.onTrackClickListener, TracksListAdapter.onTrackLongClickListener{
    private RecyclerView mHisListRv;
    private UILoader mUiLoader;
    private TracksListAdapter mTracksListAdapter;
    private HistoryPresenter mHistoryPresenter;
    private static final String TAG = "HistoryFragment";
    private ConfirmCheckBoxDialog mConfirmCheckBoxDialog;

    @Override
    protected View onSubViewLoaded(final LayoutInflater layoutInflater, ViewGroup container) {
        FrameLayout view = (FrameLayout) layoutInflater.inflate(R.layout.fragment_history,container,false);
        mUiLoader = new UILoader(container.getContext()) {
            @Override
            protected View getSuccessView(ViewGroup container) {
                return createSuccessView(layoutInflater,container);
            }
        };
        initPresenter();

        if (mUiLoader.getParent() instanceof ViewGroup) {
            ((ViewGroup) mUiLoader.getParent()).removeView(mUiLoader);
        }

        view.addView(mUiLoader);
        return view;
    }

    private void initPresenter() {
        mHistoryPresenter = HistoryPresenter.getInstance();
        mHistoryPresenter.registerViewCallback(this);
        mHistoryPresenter.getHistoryList();
    }

    private View createSuccessView(LayoutInflater layoutInflater, ViewGroup container) {
        View itemView = layoutInflater.inflate(R.layout.succ_history,container,false);
        TwinklingRefreshLayout twinklingRefreshLayout = itemView.findViewById(R.id.sub_scroll_view);
        twinklingRefreshLayout.setEnableRefresh(false);
        twinklingRefreshLayout.setEnableLoadmore(false);
        mHisListRv = itemView.findViewById(R.id.history_list);
        mHisListRv.setLayoutManager(new LinearLayoutManager(container.getContext()));
        mTracksListAdapter = new TracksListAdapter();
        mTracksListAdapter.setonTrackClickListener(this);
        mTracksListAdapter.setOnTrackLongClickListener(this);
        mHisListRv.setAdapter(mTracksListAdapter);
        mHisListRv.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.bottom = UIUtil.dip2px(view.getContext(), 5);
                outRect.top = UIUtil.dip2px(view.getContext(), 5);
                outRect.left = UIUtil.dip2px(view.getContext(), 5);
                outRect.right = UIUtil.dip2px(view.getContext(), 5);
            }
        });
        if (mUiLoader != null) {
            mUiLoader.updateStatus(UILoader.UIStatus.LOADING);
        }
        return itemView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //防止内存泄漏
        if (mHistoryPresenter != null) {
            mHistoryPresenter.unRegisterViewCallback(this);
        }
        mTracksListAdapter.setonTrackClickListener(null);
        mTracksListAdapter.setOnTrackLongClickListener(null);
    }

    @Override
    public void onHistoriesLoaded(List<Track> tracks) {
        if (tracks.size() == 0){
            mUiLoader.updateStatus(UILoader.UIStatus.EMPTY);
        }else {
            mUiLoader.updateStatus(UILoader.UIStatus.SUCCESS);
        }
        if (mTracksListAdapter != null) {
            mTracksListAdapter.setData(tracks);
        }
    }

    @Override
    public void onItemClick(List<Track> list, int position) {
        //设置播放器数据
        PlayerPresenter instance = PlayerPresenter.getInstance();
        instance.setPlayList(list, position);
        // LogUtil.e(TAG,position+"");
        startActivity(new Intent(getActivity(), PlayerActivity.class));
    }

    @Override
    public void onItemLongClick(final List<Track> list, final int position) {
        mConfirmCheckBoxDialog = new ConfirmCheckBoxDialog(getContext());
        mConfirmCheckBoxDialog.setOnDialogActionClickListener(new ConfirmCheckBoxDialog.OnDialogActionClickListener() {
            @Override
            public void onCancelClick() {
                mConfirmCheckBoxDialog.dismiss();
            }

            @Override
            public void onConfirmClick(boolean isCheck) {
                if (!isCheck) {
                   mHistoryPresenter.delHistory(list.get(position));
                }else {
                    mHistoryPresenter.cleanHistories();
                }
            }
        });
        mConfirmCheckBoxDialog.show();
    }

}
