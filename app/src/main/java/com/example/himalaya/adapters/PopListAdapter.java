package com.example.himalaya.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.himalaya.R;
import com.example.himalaya.base.BaseApplication;
import com.example.himalaya.views.SobPopWindow;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author Tian
 * @description 弹窗列表的Adapter
 * @date :2020/4/27 23:47
 */
public class PopListAdapter extends RecyclerView.Adapter<PopListAdapter.InnerViewHolder> {
    List<Track> mData = new ArrayList<>();
    private int mCurrentPosition;
    private SobPopWindow.onItemClickListener mOnItemClickListener;

    @NonNull
    @Override
    public InnerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pop_list, parent, false);
        return new InnerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerViewHolder holder, final int position) {
        Track track = mData.get(position);
        holder.mTrackTitleTv = holder.itemView.findViewById(R.id.track_title_tv);
        holder.mPlayIconIv = holder.itemView.findViewById(R.id.play_icon_iv);
        //设置字体颜色
        holder.mTrackTitleTv.setTextColor(BaseApplication.getAppContext().getResources().getColor(
                position == mCurrentPosition? R.color.second_color : R.color.play_list_text_color));
        holder.mTrackTitleTv.setText(track.getTrackTitle());
        //社会图标是否显示
        holder.mPlayIconIv.setVisibility(position == mCurrentPosition ? View.VISIBLE : View.GONE);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mData != null) {
            return mData.size();
        }
        return 0;
    }

    public void setData(List<Track> list) {
        mData.clear();
        mData.addAll(list);
        notifyDataSetChanged();
    }

    public void setCurrentPosition(int index) {
        mCurrentPosition = index;
        //注意要加这句
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(SobPopWindow.onItemClickListener listener){
        this.mOnItemClickListener = listener;
    }

    public class InnerViewHolder extends RecyclerView.ViewHolder {
        TextView mTrackTitleTv;
        ImageView mPlayIconIv;
        public InnerViewHolder(@NonNull View itemView) {
            super(itemView);

        }
    }


}
