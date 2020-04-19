package com.example.himalaya.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.example.himalaya.R;
import com.example.himalaya.base.BaseApplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @author Tian
 * @description UI加载器
 * @date :2020/4/17 16:03
 */
public abstract class UILoader extends FrameLayout {

    private View loadingView, successView, neterrorView, emptyView;
    private onRetryClickListener mOnRetryClickListener = null;


    public enum UIStatus {
        LOADING, SUCCESS, NETWORK_ERROR, EMPTY, NONE;
    }

    public UIStatus mCurrentStatus = UIStatus.NONE;

    //保证了只有唯一的入口
    public UILoader(@NonNull Context context) {
        this(context, null);
    }

    public UILoader(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UILoader(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    //初始化UI
    private void init() {
        SwitchUIByCurrentStatus();
    }

    public void updateStatus(UIStatus uiStatus){
        mCurrentStatus = uiStatus;
        //更新要在主线程
        BaseApplication.getHandler().post(new Runnable() {
            @Override
            public void run() {
                SwitchUIByCurrentStatus();
            }
        });
    }

    private void SwitchUIByCurrentStatus() {
        //加载中
        if (loadingView == null) {
            loadingView = getLoadingView();
            addView(loadingView);
        }
        //设置是否可见
        loadingView.setVisibility(mCurrentStatus == UIStatus.LOADING ? VISIBLE : GONE);

        //成功
        if (successView == null) {
            successView = getSuccessView(this);
            addView(successView);
        }

        successView.setVisibility(mCurrentStatus == UIStatus.SUCCESS ? VISIBLE : GONE);

        //网络错误
        if (neterrorView == null) {
            neterrorView = getNetworkErrorView();
            addView(neterrorView);
        }

        neterrorView.setVisibility(mCurrentStatus == UIStatus.NETWORK_ERROR ? VISIBLE : GONE);


        //空页面
        if (emptyView == null) {
            emptyView = getEmptyView();
            addView(emptyView);
        }

        emptyView.setVisibility(mCurrentStatus == UIStatus.EMPTY ? VISIBLE : GONE);


    }

    protected View getEmptyView() {
        return LayoutInflater.from(getContext()).inflate(R.layout.fragment_empty_view, this, false);
    }

    protected View getNetworkErrorView() {
        View view =  LayoutInflater.from(getContext()).inflate(R.layout.fragment_error_view, this, false);
        view.findViewById(R.id.network_error_icon).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo:重新获取数据
                if (mOnRetryClickListener != null) {
                    mOnRetryClickListener.onRetryClick();
                }
            }
        });
        return view;
    }

    protected abstract View getSuccessView(ViewGroup container);

    private View getLoadingView() {
        return LayoutInflater.from(getContext()).inflate(R.layout.fragment_loading_view, this, false);
    }

    public void setonRetryClickListener(onRetryClickListener listener){
        this.mOnRetryClickListener = listener;
    }

    public interface onRetryClickListener{
        void onRetryClick();
    }

}
