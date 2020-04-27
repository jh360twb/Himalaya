package com.example.himalaya.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.himalaya.R;
import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

/**
 * @author Tian
 * @description 音乐播放器页面滑动适配器
 * @date :2020/4/25 22:54
 */
public class PlayerTrackPagerAdapter extends PagerAdapter {

    private List<Track> mData = new ArrayList<>();

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View itemView = LayoutInflater.from(container.getContext()).inflate(R.layout.item_track_pager,container,false);
        container.addView(itemView);
        //设置数据
        ImageView item_pager = itemView.findViewById(R.id.track_item_pager);
        //设置图片
        Track track = mData.get(position);
        String coverUrlLarge = track.getCoverUrlLarge();
        Picasso.with(container.getContext()).load(coverUrlLarge).into(item_pager);
        return itemView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    public void setData(List<Track> listTrack) {
        mData.clear();
        mData.addAll(listTrack);
        notifyDataSetChanged();
    }
}
