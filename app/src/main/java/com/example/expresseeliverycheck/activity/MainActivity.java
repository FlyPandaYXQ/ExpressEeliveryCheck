package com.example.expresseeliverycheck.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import com.example.expresseeliverycheck.R;
import com.example.expresseeliverycheck.view.GetSmsListView;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    private GetSmsListView getSmsListView;
    @BindView(R.id.sms_list)
    protected RecyclerView sms_list;
    @BindView(R.id.sms_list_refreshLayout)
    protected RefreshLayout sms_list_refreshLayout;

    @Override
    protected void onCreate(@NonNull Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        sms_list_refreshLayout.setEnableLoadMore(false);
        getSms();
    }


    private void getSms() {
        getSmsListView = new GetSmsListView(sms_list, sms_list_refreshLayout, this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getSmsListView.forceRefresh();
    }


}
