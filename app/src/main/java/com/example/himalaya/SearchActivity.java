package com.example.himalaya;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.himalaya.adapters.AlbumListAdapter;
import com.example.himalaya.adapters.SearchRecommendAdapter;
import com.example.himalaya.base.BaseApplication;
import com.example.himalaya.interfaces.ISearchCallback;
import com.example.himalaya.presenters.AlbumDetailPresenter;
import com.example.himalaya.presenters.SearchPresenter;
import com.example.himalaya.utils.LogUtil;
import com.example.himalaya.views.FlowTextLayout;
import com.example.himalaya.views.UILoader;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT;

public class SearchActivity extends AppCompatActivity implements ISearchCallback,
        AlbumListAdapter.onAlbumItemClickListener {

    private UILoader mUiLoader;
    private FrameLayout mSearchContainer;
    private TwinklingRefreshLayout mRefreshLayout;
    private RecyclerView mResultListView;
    private AlbumListAdapter mRecommendListAdapter;
    private ImageView mSearchBack;
    private EditText mSearchInput;
    private ImageView mSearchInputDelete;
    private TextView mSearchBtn;
    private SearchPresenter mInstance;
    private static final String TAG = "SearchActivity";
    private FlowTextLayout mFlowTextLayout;
    private InputMethodManager imm;
    private RecyclerView mSearchRecommendList;
    //是否需要推荐词
    private boolean isNeedSuggestWords = true;
    private SearchRecommendAdapter mSearchRecommendAdapter;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getWindow().setStatusBarColor(Color.WHITE);
        //键盘
        imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        initView();
        initEvent();
        initPresenter();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mInstance != null) {
            mInstance.unRegisterViewCallback(this);
            mInstance = null;
        }
    }

    private void initPresenter() {
        mInstance = SearchPresenter.getInstance();
        mInstance.registerViewCallback(this);
        mInstance.getHotWord();
    }

    private void initEvent() {
        mRecommendListAdapter.setonAlbumItemClickListener(this);
        //重新点击加载
        mUiLoader.setonRetryClickListener(new UILoader.onRetryClickListener() {
            @Override
            public void onRetryClick() {
                mInstance.reSearch();
            }
        });
        //返回按钮
        mSearchBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //搜索按钮
        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(mSearchInput.getText())) {
                    String keyWord = mSearchInput.getText().toString().trim();
                    LogUtil.e(TAG, keyWord + "key");
                    mInstance.doSearch(keyWord);
                } else {
                    BaseApplication.getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(SearchActivity.this, "请输入要搜索的内容", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });

        //输入框的监听
        mSearchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                    //获取热词
                    mInstance.getHotWord();
                    //删除按钮为gone
                    mSearchInputDelete.setVisibility(View.GONE);
                } else {
                    mSearchInputDelete.setVisibility(View.VISIBLE);
                    if (isNeedSuggestWords) {
                        getSuggestWords(s.toString());
                    } else {
                        isNeedSuggestWords = true;
                    }

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //删除按钮
        mSearchInputDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchInput.setText("");
            }
        });
        //热词
        mFlowTextLayout.setClickListener(new FlowTextLayout.ItemClickListener() {
            @Override
            public void onItemClick(String text) {
                //不需要相关的联想词
                isNeedSuggestWords = false;
                switch2Search(text);
            }
        });

        //推荐词的点击
        mSearchRecommendAdapter.setItemClickListener(new SearchRecommendAdapter.ItemClickListener() {
            @Override
            public void onItemClick(String keyword) {
                //不需要相关的联想词
                isNeedSuggestWords = false;
                //推荐热词的点击
                switch2Search(keyword);
            }
        });

        mRefreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                mInstance.loadMore();
            }
        });

    }

    //搜索
    private void switch2Search(String text) {
        if (TextUtils.isEmpty(text)) {
            //可以给个提示
            Toast.makeText(this, "搜索关键字不能为空.", Toast.LENGTH_SHORT).show();
            return;
        }
        //设置文字
        mSearchInput.setText(text);
        //设置光标
        mSearchInput.setSelection(text.length());
        //发起搜索
        mInstance.doSearch(text);
    }

    //获取推荐词
    private void getSuggestWords(String recommendKeyWord) {
        if (mInstance != null) {
            mInstance.getRecommendWord(recommendKeyWord);
        }
    }

    private void initView() {
        mSearchBack = findViewById(R.id.search_back);
        mSearchInput = findViewById(R.id.search_input);
        mSearchInput.postDelayed(new Runnable() {
            @Override
            public void run() {
                //获取到焦点
                mSearchInput.requestFocus();
                //显示键盘
                imm.showSoftInput(mSearchInput, SHOW_IMPLICIT);
            }
        }, 500);
        mSearchInputDelete = findViewById(R.id.search_input_delete);
        mSearchBtn = findViewById(R.id.search_btn);
        mSearchContainer = findViewById(R.id.search_container);
        //一开始是没有的
        mSearchInputDelete.setVisibility(View.GONE);
        if (mUiLoader == null) {
            mUiLoader = new UILoader(this) {
                @Override
                protected View getSuccessView(ViewGroup container) {
                    return createSuccessView(container);
                }
            };
        }
        mSearchContainer.removeAllViews();
        mSearchContainer.addView(mUiLoader);
    }

    private View createSuccessView(ViewGroup container) {
        View resultView = LayoutInflater.from(this).inflate(R.layout.succ_search, container, false);
        mRefreshLayout = resultView.findViewById(R.id.search_result_refresh_layout);
        mRefreshLayout.setEnableRefresh(false);
        mFlowTextLayout = resultView.findViewById(R.id.recommend_hot_word_view);
        mResultListView = resultView.findViewById(R.id.result_list_view);
        mSearchRecommendList = resultView.findViewById(R.id.search_recommend_list);
        //设置布局管理
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mResultListView.setLayoutManager(layoutManager);
        //设置适配器
        mRecommendListAdapter = new AlbumListAdapter();
        mResultListView.setAdapter(mRecommendListAdapter);
        mResultListView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.bottom = UIUtil.dip2px(view.getContext(), 2);
                outRect.top = UIUtil.dip2px(view.getContext(), 2);
                outRect.left = UIUtil.dip2px(view.getContext(), 5);
                outRect.right = UIUtil.dip2px(view.getContext(), 5);
            }
        });

        //推荐词的
        //设置布局管理器
        LinearLayoutManager recommendLayoutManager = new LinearLayoutManager(this);
        mSearchRecommendList.setLayoutManager(recommendLayoutManager);
        //设置适配器
        mSearchRecommendAdapter = new SearchRecommendAdapter();
        mSearchRecommendList.setAdapter(mSearchRecommendAdapter);
        mSearchRecommendList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.top = UIUtil.dip2px(view.getContext(), 2);
                outRect.bottom = UIUtil.dip2px(view.getContext(), 2);
                outRect.left = UIUtil.dip2px(view.getContext(), 5);
                outRect.right = UIUtil.dip2px(view.getContext(), 5);
            }
        });
        return resultView;
    }


    @Override
    public void onSearchResultLoaded(List<Album> result) {
        handleSearchResult(result);
        //隐藏键盘
        if (imm != null) {
            imm.hideSoftInputFromWindow(mSearchInput.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }

    }

    private void handleSearchResult(List<Album> result) {
        hideSuccessView();
        mRefreshLayout.setVisibility(View.VISIBLE);
        mResultListView.setVisibility(View.VISIBLE);
        if (result != null) {
            if (result.size() == 0) {
                mUiLoader.updateStatus(UILoader.UIStatus.EMPTY);
            } else {
                mUiLoader.updateStatus(UILoader.UIStatus.SUCCESS);
                mRecommendListAdapter.setData(result);
            }
        }

    }

    @Override
    public void onHotWordLoaded(List<HotWord> hotWordList) {
        hideSuccessView();
        mFlowTextLayout.setVisibility(View.VISIBLE);
        if (mUiLoader != null) {
            mUiLoader.updateStatus(UILoader.UIStatus.SUCCESS);
        }
        List<String> hotWords = new ArrayList<>();
        for (HotWord hotWord : hotWordList) {
            hotWords.add(hotWord.getSearchword());
        }
        //排序
        Collections.sort(hotWords);
        mFlowTextLayout.setTextContents(hotWords);
    }

    @Override
    public void onLoadMoreResult(List<Album> result, boolean isOkay) {

        if (isOkay) {
            handleSearchResult(result);
        } else {
            Toast.makeText(SearchActivity.this, "没有更多内容", Toast.LENGTH_SHORT).show();
        }

        //停止加载
        if (mRefreshLayout != null) {
            mRefreshLayout.finishLoadmore();
        }
    }

    @Override
    public void onRecommendWordLoaded(List<QueryResult> keyWordList) {
        //关键字的联想词
        LogUtil.d(TAG, "keyWordList size == > " + keyWordList.size());
        if (mSearchRecommendAdapter != null) {
            mSearchRecommendAdapter.setData(keyWordList);
        }
        //控制UI的状态和隐藏显示
        if (mUiLoader != null) {
            mUiLoader.updateStatus(UILoader.UIStatus.SUCCESS);
        }
        //控制显示和隐藏
        hideSuccessView();
        mSearchRecommendList.setVisibility(View.VISIBLE);
    }

    //隐藏成功的view
    private void hideSuccessView() {
        mSearchRecommendList.setVisibility(View.GONE);
        mRefreshLayout.setVisibility(View.GONE);
        mResultListView.setVisibility(View.GONE);
        mFlowTextLayout.setVisibility(View.GONE);
    }

    @Override
    public void onError(int errorCode, String errorMsg) {
        if (mUiLoader != null) {
            mUiLoader.updateStatus(UILoader.UIStatus.NETWORK_ERROR);
        }
    }

    @Override
    public void onEmpty() {
        if (mUiLoader != null) {
            mUiLoader.updateStatus(UILoader.UIStatus.EMPTY);
        }
    }

    @Override
    public void onLoading() {
        if (mUiLoader != null) {
            mUiLoader.updateStatus(UILoader.UIStatus.LOADING);
        }
    }

    @Override
    public void onItemClick(int position, Album album) {
        AlbumDetailPresenter.getInstance().setTargetAlbum(album);
        //item被点击了，跳转到详情界面
        Intent intent = new Intent(this, DetailActivity.class);
        startActivity(intent);
    }
}
