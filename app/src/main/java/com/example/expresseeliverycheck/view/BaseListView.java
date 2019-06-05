package com.example.expresseeliverycheck.view;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.expresseeliverycheck.adapter.BaseListAdapter;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;


import java.util.Arrays;
import java.util.List;

/**
 */
public abstract class BaseListView<T> {

    /**
     * 每一页展示多少条数据
     */

    protected static final int REQUEST_COUNT = 10;

    protected RecyclerView mRecyclerView = null;

    RefreshLayout mRefreshLayout = null;

    protected BaseListAdapter adapter = null;

    protected Context mContext;

    protected boolean isRefresh = true;

    private LinearLayoutManager layoutManager;

    public BaseListView(RecyclerView recyclerView, RefreshLayout refreshLayout, Context context) {
        this.mContext = context;
        this.mRecyclerView = recyclerView;
        this.mRefreshLayout = refreshLayout;

        mRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL,false);

        mRecyclerView.setLayoutManager(layoutManager);
        setAdapter();
        mRecyclerView.setAdapter(adapter);

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull final RefreshLayout refreshLayout) {
                isRefresh = true;
                mRefreshLayout.resetNoMoreData();
                onRefreshing();
            }
        });

        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                isRefresh = false;
                onLoadingMore();
            }
        });
        refreshLayout.setRefreshHeader(new ClassicsHeader(context));
        refreshLayout.setHeaderHeight(60);
    }

    public void init(int count) {

    }

    public void notifyDataSetChanged() {
        adapter.notifyDataSetChanged();
    }

    public void onUpdate(List<T> resultList) {
        if (getActivity() == null || getActivity().isFinishing()) {
            return;
        }
        // 数据为空
        if (resultList == null || resultList.size() == 0) {
            mRefreshLayout.finishLoadMoreWithNoMoreData();
            if (isRefresh) {
                adapter.clear();
                adapter.notifyItemRemoved(0);
                notifyDataSetChanged();
            }
            mRefreshLayout.finishRefresh(true);
            return;
        } else if (resultList.size() < REQUEST_COUNT) {
            mRefreshLayout.finishLoadMoreWithNoMoreData();
        }
        // 数据不为空
        List<T> list = resultList;
        if (isRefresh) {
            adapter.clear();
        }
        adapter.addAll(list);
        mRefreshLayout.finishRefresh(true);
        notifyDataSetChanged();
    }

    public AppCompatActivity getActivity() {
        return (AppCompatActivity) mContext;
    }

    public abstract void setAdapter();

    public abstract void onRefreshing();

    public abstract void onLoadingMore();

    public abstract void onListItemClick(View view, int position);

    public abstract void onListItemLongClick(View view, int position);

    public void forceRefresh() {
        mRefreshLayout.autoRefresh();
    }

    public LinearLayoutManager getLayoutManager() {
        return layoutManager;
    }

    public void setLayoutManager(LinearLayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }
}
