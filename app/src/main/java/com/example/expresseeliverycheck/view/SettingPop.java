package com.example.expresseeliverycheck.view;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;

import com.example.expresseeliverycheck.R;

public class SettingPop extends PopupWindow{
    private PopupWindow popupWindow;

    public SettingPop(Context context) {
        super(context);
    }

    public SettingPop(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SettingPop(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SettingPop(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
    //    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.setting_pop);
//        View inflate = LayoutInflater.from(this).inflate(R.layout.setting_pop,null,false);
//        popupWindow = new PopupWindow(inflate,140,258,true);
//        popupWindow.setBackgroundDrawable(getDrawable(R.mipmap.setting));
//        popupWindow.setFocusable(false);
//    }

}
