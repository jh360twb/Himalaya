package com.example.himalaya.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.himalaya.R;
import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author Tian
 * @description 推荐板块的内容的适配器
 * @date :2020/4/16 15:40
 */
public class RecommendListAdapter extends RecyclerView.Adapter<RecommendListAdapter.InnerHolder> {

    List<Album> mData = new ArrayList<>();
    private static final String TAG = "RecommendListAdapter";
    private onRecommendItemClickListener mOnRecommendItemClickListener = null;

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recommend,parent,false);
        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecommendListAdapter.InnerHolder holder, final int position) {
        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = (int) v.getTag();
                if (mOnRecommendItemClickListener != null) {
                    mOnRecommendItemClickListener.onItemClick(pos,mData.get(pos));
                }
               // LogUtil.e(TAG,"click -> "+ v.getTag());
            }
        });
        holder.setData(mData.get(position));
    }

    @Override
    public int getItemCount() {
        if (mData != null) {
            return mData.size();
        }
        return 0;
    }

    public void setData(List<Album> albumList) {
        if (albumList != null) {
            mData.clear();
            mData.addAll(albumList);
        }
        //更新一下UI
        notifyDataSetChanged();
    }

    public class InnerHolder extends RecyclerView.ViewHolder {
        public InnerHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void setData(Album album) {
            //找到各个控件,设置数据
            ImageView albumCoverIv = itemView.findViewById(R.id.album_cover);
            //title
            TextView albumTitleTv = itemView.findViewById(R.id.album_title_tv);
            //描述
            TextView albumDesTv = itemView.findViewById(R.id.album_description_tv);
            //播放数量
            TextView albumPlayCountTv = itemView.findViewById(R.id.album_play_count);
            //专辑内容数量
            TextView albumContentCountTv = itemView.findViewById(R.id.album_content_size);

            albumTitleTv.setText(album.getAlbumTitle());
            albumDesTv.setText(album.getAlbumIntro());
            if (album.getPlayCount()>=10000) {
                albumPlayCountTv.setText(album.getPlayCount() / 10000 + "万");
            }else {
                albumPlayCountTv.setText(album.getPlayCount()+"");
            }
            albumContentCountTv.setText(album.getIncludeTrackCount() + "");

            if (!album.getCoverUrlMiddle().isEmpty()) {
                Picasso.with(itemView.getContext())
                        .load(album.getCoverUrlMiddle())
                        .placeholder(R.mipmap.logo)
                        .into(albumCoverIv);
            }else {
                Picasso.with(itemView.getContext())
                        .load(R.mipmap.logo)
                        .into(albumCoverIv);
            }
        }
    }

    public  void setonRecommendItemClickListener(onRecommendItemClickListener listener){
        this.mOnRecommendItemClickListener = listener;
    }

    public interface onRecommendItemClickListener{
        void onItemClick(int position, Album album);
    }
}
