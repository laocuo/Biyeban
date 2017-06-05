package com.laocuo.biyeban.main.freshnews;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.laocuo.biyeban.R;
import com.laocuo.biyeban.base.BaseFragment;
import com.laocuo.biyeban.utils.L;

import butterknife.BindView;

/**
 * Created by hoperun on 6/5/17.
 */

public class FreshNewsFragment extends BaseFragment<FreshNewsPresenter> {
    private static final String TAG = "FreshNews";
    private static final String TYPE_KEY = "TypeKey";
    private String mTitle;
    @BindView(R.id.tool_bar)
    Toolbar mToolbar;
    @BindView(R.id.freshnews_list)
    RecyclerView mRecyclerView;

    public static Fragment newInstance(String resId) {
        FreshNewsFragment fragment = new FreshNewsFragment();
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.freshnews_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_publish:
                debug("menu_publish");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_freshnews;
    }

    @Override
    protected void doInject() {
        DaggerFreshNewsComponent.builder()
                .freshNewsModule(new FreshNewsModule(this))
                .build()
                .inject(this);
    }

    @Override
    protected void doInit() {
        initToolBar(mToolbar, false, mTitle);
        setHasOptionsMenu(true);
    }

    @Override
    protected void doLoadData(boolean isRefresh) {
        debug("doLoadData="+isRefresh);
        if (isRefresh == true) {
            finishSwipeRefresh();
        }
    }

    @Override
    public void updateUI() {

    }

    private void debug(String log) {
        L.d(TAG+"-"+log);
    }
}
