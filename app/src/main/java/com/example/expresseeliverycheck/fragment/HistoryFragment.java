package com.example.expresseeliverycheck.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.expresseeliverycheck.R;
import com.example.expresseeliverycheck.view.GetSmsListView;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author FlyPanda@若曦
 */
public class HistoryFragment extends Fragment {
    private GetSmsListView getSmsListView;
    @BindView(R.id.sms_list)
    protected RecyclerView sms_list;
    @BindView(R.id.sms_list_refreshLayout)
    protected RefreshLayout sms_list_refreshLayout;
    @BindView(R.id.imageView)
    protected ImageView imageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_now, container, false);
        ButterKnife.bind(this, view);
        imageView.setVisibility(View.GONE);
        initData();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
//        if (getSmsListView != null) {
//            getSmsListView.forceRefresh();
//        }
    }

    public void refresh() {
        if (getSmsListView != null) {
            getSmsListView.forceRefresh();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    private void initData() {
        sms_list_refreshLayout.setEnableLoadMore(false);
        getSmsListView = new GetSmsListView(sms_list, sms_list_refreshLayout, getActivity(), 1, imageView);
        getSmsListView.forceRefresh();
    }
}
