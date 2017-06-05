package com.laocuo.biyeban.main.chatroom;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.laocuo.biyeban.R;
import com.laocuo.biyeban.base.BaseFragment;

import butterknife.BindView;

/**
 * Created by hoperun on 6/5/17.
 */

public class ChatRoomFragment extends BaseFragment {
    private static final String TYPE_KEY = "TypeKey";
    private String mTitle;
    @BindView(R.id.chatroom_list)
    RecyclerView mRecyclerView;

    public static Fragment newInstance(String resId) {
        ChatRoomFragment fragment = new ChatRoomFragment();
        Bundle bundle = new Bundle();
        bundle.putString(TYPE_KEY, resId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTitle = getArguments().getString(TYPE_KEY);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_chatroom;
    }

    @Override
    protected void doInject() {

    }

    @Override
    protected void doInit() {

    }

    @Override
    protected void doLoadData(boolean isRefresh) {

    }

    @Override
    public void updateUI() {

    }
}
