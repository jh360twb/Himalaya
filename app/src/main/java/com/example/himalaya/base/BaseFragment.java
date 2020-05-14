package com.example.himalaya.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

//做成一个抽象类,这样下面的就都要去实现
public abstract class BaseFragment extends Fragment {

    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = onSubViewLoaded(inflater, container);
        setRetainInstance(true);
        return view;
    }

    protected abstract View onSubViewLoaded(LayoutInflater layoutInflater, ViewGroup container);


}
