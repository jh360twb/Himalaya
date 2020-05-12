package com.example.himalaya.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.himalaya.R;
import com.example.himalaya.fragments.SubscriptionFragment;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import org.w3c.dom.Text;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @author Tian
 * @description
 * @date :2020/5/9 00:57
 */
public class ConfirmDialog extends Dialog {

    private TextView mDialogCancelTv;
    private TextView mDialogContinueTv;
    private ItemLongClickListener longClickListener;

    public ConfirmDialog(@NonNull Context context) {
        this(context, 0);
    }

    public ConfirmDialog(@NonNull Context context, int themeResId) {
        this(context, true, null);
    }

    protected ConfirmDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_confirm);
        mDialogCancelTv = findViewById(R.id.dialog_check_box_cancel);
        mDialogContinueTv = findViewById(R.id.dialog_check_box_continue);
        initEvent();
    }

    private void initEvent() {
        mDialogCancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                longClickListener.onItemCancel();
                dismiss();
            }
        });

        mDialogContinueTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                longClickListener.onItemContinue();
                dismiss();
            }
        });

    }

    public void setItemLongClickListener(ItemLongClickListener longClickListener){
        this.longClickListener = longClickListener;
    }

    public interface ItemLongClickListener{
        void onItemCancel();
        void onItemContinue();
    }
}
