package com.example.himalaya.presenters;

import com.example.himalaya.data.XimalayaApi;
import com.example.himalaya.interfaces.ISearchCallback;
import com.example.himalaya.interfaces.ISearchPresenter;
import com.example.himalaya.utils.Constants;
import com.example.himalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.SearchAlbumList;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;
import com.ximalaya.ting.android.opensdk.model.word.HotWordList;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;
import com.ximalaya.ting.android.opensdk.model.word.SuggestWords;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

/**
 * @author Tian
 * @description
 * @date :2020/5/4 23:32
 */
public class SearchPresenter implements ISearchPresenter {

    private List<Album> mSearchResult = new ArrayList<>();
    List<ISearchCallback> mCallbacks = new ArrayList<>();
    private String mCurrentKeyword;
    private XimalayaApi mXimalayaApi;
    private static final int DEFAULT_PAGE = 1;
    private int mCurrentPage = DEFAULT_PAGE;
    private static final String TAG = "SearchPresenter";
    private boolean mIsLoadMore = false;

    private SearchPresenter() {
        mXimalayaApi = XimalayaApi.getInstance();
    }

    private static SearchPresenter sPresenter = null;

    public static SearchPresenter getInstance() {
        if (sPresenter == null) {
            synchronized (SearchPresenter.class) {
                if (sPresenter == null) {
                    sPresenter = new SearchPresenter();
                }
            }
        }
        return sPresenter;
    }

    @Override
    public void doSearch(String keyword) {
        mCurrentPage = DEFAULT_PAGE;
        mSearchResult.clear();
        mCurrentKeyword = keyword;
        search(keyword);
    }

    private void search(String keyword) {
        //加载中
        for (ISearchCallback callback : mCallbacks) {
            callback.onLoading();
        }

        //加载完成
       mXimalayaApi.searchByKeyword(keyword, mCurrentPage, new IDataCallBack<SearchAlbumList>() {
            @Override
            public void onSuccess(@Nullable SearchAlbumList searchAlbumList) {
                List<Album> albums = searchAlbumList.getAlbums();
                LogUtil.e(TAG, albums.toString());
                mSearchResult.addAll(albums);
                LogUtil.d(TAG, "albums size -- > " + albums.size());
                if (mIsLoadMore) {
                    for (ISearchCallback iSearchCallback : mCallbacks) {
                        iSearchCallback.onLoadMoreResult(mSearchResult, albums.size() != 0);
                    }
                    mIsLoadMore = false;
                } else {
                    if (mSearchResult.size() == 0){
                        for (ISearchCallback callback : mCallbacks) {
                            callback.onEmpty();
                        }
                    }else {
                        for (ISearchCallback iSearchCallback : mCallbacks) {
                            iSearchCallback.onSearchResultLoaded(mSearchResult);
                        }
                    }
                }
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                LogUtil.d(TAG, "errorCode -- > " + errorCode);
                LogUtil.d(TAG, "errorMsg -- > " + errorMsg);
                for (ISearchCallback iSearchCallback : mCallbacks) {
                    if (mIsLoadMore) {
                        iSearchCallback.onLoadMoreResult(mSearchResult, false);
                        mCurrentPage--;
                        mIsLoadMore = false;
                    } else {
                        iSearchCallback.onError(errorCode, errorMsg);
                    }
                }

            }
        });
    }

    @Override
    public void reSearch() {
        search(mCurrentKeyword);
    }

    @Override
    public void loadMore() {
        //判断有没有必要进行加载更多
        if (mSearchResult.size() < Constants.COUNT_SEARCH) {
            for (ISearchCallback iSearchCallback : mCallbacks) {
                iSearchCallback.onLoadMoreResult(mSearchResult, false);
            }
        } else {
            mIsLoadMore = true;
            mCurrentPage++;
            search(mCurrentKeyword);
        }
    }

    @Override
    public void getHotWord() {
       mXimalayaApi.getHotWords(new IDataCallBack<HotWordList>() {
            @Override
            public void onSuccess(HotWordList hotWordList) {
                if (hotWordList != null) {
                    List<HotWord> hotWords = hotWordList.getHotWordList();
                    LogUtil.d(TAG, "hotWords size -- > " + hotWords.size());
                    for (ISearchCallback iSearchCallback : mCallbacks) {
                        iSearchCallback.onHotWordLoaded(hotWords);
                    }
                }
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                LogUtil.d(TAG, "getHotWord errorCode -- > " + errorCode);
                LogUtil.d(TAG, "getHotWord errorMsg -- > " + errorMsg);
            }
        });
    }

    @Override
    public void getRecommendWord(String keyword) {
        mXimalayaApi.getSuggestWord(keyword, new IDataCallBack<SuggestWords>() {
            @Override
            public void onSuccess(@Nullable SuggestWords suggestWords) {
                if (suggestWords != null) {
                    List<QueryResult> keyWordList = suggestWords.getKeyWordList();
                    LogUtil.d(TAG, "keyWordList size -- > " + keyWordList.size());
                    for (ISearchCallback iSearchCallback : mCallbacks) {
                        iSearchCallback.onRecommendWordLoaded(keyWordList);
                    }
                }
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                LogUtil.d(TAG, "getRecommendWord errorCode -- > " + errorCode);
                LogUtil.d(TAG, "getRecommendWord errorMsg -- > " + errorMsg);
            }
        });
    }

    @Override
    public void registerViewCallback(ISearchCallback iSearchCallback) {
        if (!mCallbacks.contains(iSearchCallback)) {
            mCallbacks.add(iSearchCallback);
        }
    }

    @Override
    public void unRegisterViewCallback(ISearchCallback iSearchCallback) {
        mCallbacks.remove(iSearchCallback);
    }
}
