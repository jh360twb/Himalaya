package com.example.himalaya.views;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.himalaya.R;
import com.example.himalaya.adapters.PopListAdapter;
import com.example.himalaya.base.BaseApplication;
import com.example.himalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author Tian
 * @description 播放列表弹窗
 * @date :2020/4/27 16:10
 */
public class SobPopWindow extends PopupWindow{

    private final View mPopView;
    private TextView mClose_btn;
    private PopListAdapter mPopListAdapter;
    private RecyclerView mPlayListRv;
    private static final String TAG = "SobPopWindow";
    private ImageView mPlayListModeIv;
    private TextView mPlayListModeTv;
    private playListActionListener mOnPlayListActionListener = null;
    private View mPlayModeContainer;
    private View mPlayOrderContainer;
    private ImageView mPlayListOrderIv;
    private TextView mPlayListOrderTv;

    public SobPopWindow() {
        super(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //先要设置setBackgroundDrawable,然后setOutsideTouchable才有效果
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //点击外部可消失
        setOutsideTouchable(true);
        //不可点击背景的其他按钮
        setFocusable(true);
        mPopView = LayoutInflater.from(BaseApplication.getAppContext()).inflate(R.layout.pop_play_list, null);
        setContentView(mPopView);
        //设置窗口进入和退出的动画
        setAnimationStyle(R.style.pop_animation);
        initView();
        initEvent();
        upDateOrderIcon(false);
    }

    private void initView() {
        mClose_btn = mPopView.findViewById(R.id.play_list_close_btn);
        mPlayListRv = mPopView.findViewById(R.id.play_list_rv);
        mPlayListModeIv = mPopView.findViewById(R.id.play_list_play_mode_iv);
        mPlayListModeTv = mPopView.findViewById(R.id.play_list_play_mode_tv);
        mPlayModeContainer = mPopView.findViewById(R.id.play_list_play_mode_container);
        mPlayOrderContainer = mPopView.findViewById(R.id.play_list_order_container);
        mPlayListOrderIv = mPopView.findViewById(R.id.play_list_order_iv);
        mPlayListOrderTv = mPopView.findViewById(R.id.play_list_order_tv);
        //适配器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(BaseApplication.getAppContext());
        mPlayListRv.setLayoutManager(linearLayoutManager);
        mPopListAdapter = new PopListAdapter();
        mPlayListRv.setAdapter(mPopListAdapter);
    }

    private void initEvent() {
        mClose_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SobPopWindow.this.dismiss();
            }
        });

        mPlayModeContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnPlayListActionListener.onPlayModeClick();
            }
        });

        mPlayOrderContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnPlayListActionListener.onPlayOrderClick();
            }
        });

    }

    public void setView(int resId){
        mPlayListModeIv.setImageResource(resId);
        switch (resId){
            case R.drawable.selector_play_mode_list_order:
                mPlayListModeTv.setText("顺序播放");
                break;
            case R.drawable.selector_paly_mode_random:
                mPlayListModeTv.setText("随机播放");
                break;
            case R.drawable.selector_paly_mode_single_loop:
                mPlayListModeTv.setText("单曲循环");
                break;
            case R.drawable.selector_paly_mode_list_order_looper:
                mPlayListModeTv.setText("列表循环");
                break;
        }
    }

    public void setListData(List<Track> list) {
        if (mPopListAdapter != null) {
            mPopListAdapter.setData(list);
        }
    }

    public void setCurrentIndex(int index) {
        if (mPopListAdapter != null) {
            mPopListAdapter.setCurrentPosition(index);
            mPlayListRv.scrollToPosition(index);
        }
    }

    public void setOnItemClickListener(onItemClickListener listener){
        mPopListAdapter.setOnItemClickListener(listener);
    }

    public void upDateOrderIcon(boolean isReverse) {
        mPlayListOrderIv.setImageResource(isReverse ? R.drawable.selector_play_mode_list_revers
                : R.drawable.selector_play_mode_list_order);
        mPlayListOrderTv.setText(!isReverse ? "顺序":"逆序");
    }

    public interface onItemClickListener{
        void onClick(int pos);
    }

    public void setPlayListActionListener(playListActionListener listener){
        mOnPlayListActionListener = listener;
    }

    public interface playListActionListener{
        //播放模式改变了
        void onPlayModeClick();

        //播放顺序或逆序
        void onPlayOrderClick();
    }


}
