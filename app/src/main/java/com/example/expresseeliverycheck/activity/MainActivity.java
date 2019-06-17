package com.example.expresseeliverycheck.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.expresseeliverycheck.R;
import com.example.expresseeliverycheck.fragment.HistoryFragment;
import com.example.expresseeliverycheck.fragment.NowFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.activity_main_fragment)
    protected FrameLayout activity_main_fragment;
    @BindView(R.id.activity_main_now_tv)
    protected TextView activity_main_now_tv;
    @BindView(R.id.activity_main_history_tv)
    protected TextView activity_main_history_tv;
    @BindView(R.id.activity_main_now_iv)
    protected ImageView activity_main_now_iv;
    @BindView(R.id.activity_main_history_iv)
    protected ImageView activity_main_history_iv;
    @BindView(R.id.activity_main_now)
    protected LinearLayout activity_main_now;
    @BindView(R.id.activity_main_history)
    protected LinearLayout activity_main_history;
    @BindView(R.id.activity_main)
    protected LinearLayout activity_main;

    private NowFragment nowFragment;
    private HistoryFragment historyFragment;
    private FragmentTransaction ftr;//事务

    @Override
    protected void onCreate(@NonNull Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        //记录是否第一次进入
        SharedPreferences sharedPreferences = getSharedPreferences("flypanda",0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("first",1);
        editor.commit();

        activity_main_now_tv.setSelected(true);
        activity_main_history_tv.setSelected(false);
        setSelected(0,0);
        textViewSelect(0);

        activity_main.setOnTouchListener(new OntouchNextPage());
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
                } else {
                    nowFragment.refresh();
                }
                ftr.show(nowFragment);
                break;
            case 1:
                if (historyFragment == null) {
                    historyFragment = new HistoryFragment();
                    ftr.add(R.id.activity_main_fragment, historyFragment);
                } else {
                    historyFragment.refresh();
                }
                ftr.show(historyFragment);
                break;
        }
//        ftr.commit;
        ftr.commitAllowingStateLoss();//最后千万别忘记提交事务 restart保存状态
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
                activity_main_now_iv.setImageResource(R.mipmap.topen);
//                activity_main_now_tv.setTextColor(getResources().getColor(R.color.white));
                activity_main_history_iv.setImageResource(R.mipmap.tclose);
                activity_main_history_tv.setTextColor(getResources().getColor(R.color.black));
                break;
            case 1:
                activity_main_history_iv.setImageResource(R.mipmap.topen);
//                activity_main_history_tv.setTextColor(getResources().getColor(R.color.white));
                activity_main_now_iv.setImageResource(R.mipmap.tclose);
                activity_main_now_tv.setTextColor(getResources().getColor(R.color.black));
                break;
        }
    }


    class OntouchNextPage implements View.OnTouchListener {

        private float mPosX;
        private float mPosY;

        private float mCurPosX;
        private float mCurPosY;
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // TODO Auto-generated method stub
            System.out.println("------------------- "+event.getAction());
            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN:
                    mPosX = event.getX();
                    mPosY = event.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    mCurPosX = event.getX();
                    mCurPosY = event.getY();
                    break;
                case MotionEvent.ACTION_UP:
                    System.out.println("------------------- "+mPosX);
                    System.out.println("------------------- "+mCurPosX);
                    if (mCurPosX - mPosX > 0
                            && (Math.abs(mCurPosX - mPosX) > 25)) {
                        setSelected(1,1);
                        textViewSelect(1);

                    } else if (mCurPosX - mPosX < 0
                            && (Math.abs(mCurPosX - mPosX) > 25)) {
                        setSelected(0,1);
                        textViewSelect(0);
                    }

                    break;
            }
            return true;
        }
    }

}
