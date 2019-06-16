package com.example.expresseeliverycheck.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.expresseeliverycheck.R;
import com.example.expresseeliverycheck.fragment.HistoryFragment;
import com.example.expresseeliverycheck.fragment.NowFragment;
import com.example.expresseeliverycheck.view.GetSmsListView;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.activity_main_fragment)
    protected FrameLayout activity_main_fragment;
    @BindView(R.id.activity_main_now_tv)
    protected TextView activity_main_now_tv;
    @BindView(R.id.activity_main_history_tv)
    protected TextView activity_main_history_tv;
    @BindView(R.id.activity_main_now)
    protected LinearLayout activity_main_now;
    @BindView(R.id.activity_main_history)
    protected LinearLayout activity_main_history;

    private NowFragment nowFragment;
    private HistoryFragment historyFragment;
    private FragmentTransaction ftr;//事务

    @Override
    protected void onCreate(@NonNull Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        activity_main_now_tv.setSelected(true);
        activity_main_history_tv.setSelected(false);
        setSelected(0,0);
        textViewSelect(0);
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        setSelected(0,1);
    }

    //自定义一个方法，设定布局中间的FrameLayout的选择状态
    private void setSelected(int i,int isFrist) {

        //需要将按钮变亮，且需要切换fragment的状体
        //获取事务
        FragmentManager fm = getSupportFragmentManager();
        ftr = fm.beginTransaction();//开启一个事务
        hideTransaction(ftr);//自定义一个方法，来隐藏所有的fragment

        switch (i) {
            case 0:
                if (nowFragment == null) {
                    //实例化每一个fragment
                    nowFragment = new NowFragment();
                    //千万别忘记将该fragment加入到ftr中
                    ftr.add(R.id.activity_main_fragment, nowFragment);
                    if (isFrist==1){
                        nowFragment.refresh();
                    }
                }
                ftr.show(nowFragment);
                break;
            case 1:
                if (historyFragment == null) {
                    historyFragment = new HistoryFragment();
                    ftr.add(R.id.activity_main_fragment, historyFragment);
                    if (isFrist==1){
                        historyFragment.refresh();
                    }
                }
                ftr.show(historyFragment);
                break;
        }
        ftr.commit();//最后千万别忘记提交事务
    }

    //隐藏fragment
    private void hideTransaction(FragmentTransaction ftr) {

        if (nowFragment != null) {
            ftr.hide(nowFragment);//隐藏该fragment
        }
        if (historyFragment != null) {
            ftr.hide(historyFragment);
        }
    }

    @OnClick(R.id.activity_main_now)
    protected void nowClick() {
        textViewSelect(0);
        setSelected(0,1);
    }
    @OnClick(R.id.activity_main_history)
    protected void historyClick() {
        textViewSelect(1);
        setSelected(1,1);
    }

    private void textViewSelect(int flag){
        switch (flag){
            case 0:
                activity_main_now.setBackgroundResource(R.color.colorBlue);
                activity_main_now_tv.setTextColor(getResources().getColor(R.color.white));
                activity_main_history.setBackgroundResource(R.color.transparent);
                activity_main_history_tv.setTextColor(getResources().getColor(R.color.black));
                break;
            case 1:
                activity_main_history.setBackgroundResource(R.color.colorBlue);
                activity_main_history_tv.setTextColor(getResources().getColor(R.color.white));
                activity_main_now.setBackgroundResource(R.color.transparent);
                activity_main_now_tv.setTextColor(getResources().getColor(R.color.black));
                break;
        }

    }
}
