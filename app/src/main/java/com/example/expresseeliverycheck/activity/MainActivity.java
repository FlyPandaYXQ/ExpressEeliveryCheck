package com.example.expresseeliverycheck.activity;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.telephony.SmsMessage;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.expresseeliverycheck.R;
import com.example.expresseeliverycheck.fragment.HistoryFragment;
import com.example.expresseeliverycheck.fragment.NowFragment;
import com.example.expresseeliverycheck.service.TimeService;
import com.example.expresseeliverycheck.until.BaseActivity;
import com.example.expresseeliverycheck.until.ConfigUtil;
import com.example.expresseeliverycheck.until.WaringAlertDialog;
import com.felipecsl.gifimageview.library.GifImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author FlyPanda@若曦
 */
public class MainActivity extends BaseActivity implements View.OnClickListener {
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
    @BindView(R.id.activity_main_setting_gif)
    protected GifImageView activity_main_setting_gif;


    //fragment
    private NowFragment nowFragment;
    private HistoryFragment historyFragment;
    private FragmentTransaction ftr;//事务
    //广播
    private IntentFilter intentFilter;
    private GetSmsReceiver getSmsReceiver;
    //    private AlarmReceiver alarmReceiver;
    //设置下拉框
    PopupWindow popupWindow;

    private String settingTime = "";

    private int setperFlag=0;//是否设置悬浮窗权限flag 1:设置
    private boolean isSetClick = false;//是否点击设置

    protected MyBinder myBinder;
    protected MyServeiceConn myServeiceConn= new MyServeiceConn();
    @Override
    protected void onCreate(@NonNull Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        //记录是否第一次进入
        SharedPreferences sharedPreferences = getSharedPreferences("flypanda", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("first", 1);
        editor.commit();

        initData();
        registerMyReceiver();
    }

    //初期化
    private void initData() {
        //tab初始化
        activity_main_now_tv.setSelected(true);
        activity_main_history_tv.setSelected(false);
        setSelected(0);
        textViewSelect(0);
        //监听
        activity_main.setOnTouchListener(new OntouchNextPage());
        //设置gif
        gifImageView();

        SharedPreferences sharedPreferences = getSharedPreferences("setper",0);
        setperFlag = sharedPreferences.getInt("setperFlag",0);
    }

    //广播注册
    private void registerMyReceiver() {
        intentFilter = new IntentFilter();
        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        getSmsReceiver = new GetSmsReceiver();
        registerReceiver(getSmsReceiver, intentFilter);
//        Intent intent = new Intent(String.valueOf(ConfigUtil.RECEIVERED_MSG));
    }

    //短信广播
    public class GetSmsReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String[] strings = ConfigUtil.EXPRESSNAME.split("，");
            Object[] objects = (Object[]) intent.getExtras().get("pdus");//获取短信
            String messageBody = "";
            //获取短信内容
            for (Object object : objects) {
                byte[] pdusByte = (byte[]) object;
                SmsMessage smsMessage = SmsMessage.createFromPdu(pdusByte);
                smsMessage.getOriginatingAddress();//发送短信手机号
                messageBody = smsMessage.getMessageBody();
            }
            //如果短信内容不包括快递信息 不刷新
            for (String s : strings) {
                if (messageBody.indexOf(s) != -1) {
                    setSelected(0);
                }
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        SharedPreferences sharedPreferences1 = getSharedPreferences("setper",0);
        setperFlag = sharedPreferences1.getInt("setperFlag",0);
        if (isSetClick){
            if (setperFlag==1){
                setTime();
            }
        }
        setSelected(0);
        textViewSelect(0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //取消广播
        unregisterReceiver(getSmsReceiver);
    }


    //自定义一个方法，设定布局中间的FrameLayout的选择状态
    private void setSelected(int i) {
        //设置 adapter显示背景flag
        SharedPreferences sharedPreferences = getSharedPreferences("flag", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("select", i);
        editor.commit();
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
        setSelected(0);
    }

    @OnClick(R.id.activity_main_history)
    protected void historyClick() {
        textViewSelect(1);
        setSelected(1);
    }

    //ui变化
    private void textViewSelect(int flag) {
        switch (flag) {
            case 0:
                activity_main_now_iv.setImageResource(R.mipmap.topen);
                activity_main_now_tv.setTextColor(getResources().getColor(R.color.colorBlue));
                activity_main_history_iv.setImageResource(R.mipmap.tclose);
                activity_main_history_tv.setTextColor(getResources().getColor(R.color.black));
                break;
            case 1:
                activity_main_history_iv.setImageResource(R.mipmap.topen);
                activity_main_history_tv.setTextColor(getResources().getColor(R.color.colorBlue));
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
                    if (mCurPosX - mPosX > 0
                            && (Math.abs(mCurPosX - mPosX) > 25)) {
                        setSelected(1);
                        textViewSelect(1);

                    } else if (mCurPosX - mPosX < 0
                            && (Math.abs(mCurPosX - mPosX) > 25)) {
                        setSelected(0);
                        textViewSelect(0);
                    }

                    break;
            }
            return true;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tab_layout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * GifImageView获取图片资源并通过流的形式传递到
     */
    private void gifImageView() {
        try {
            @SuppressLint("ResourceType") InputStream is = getResources().openRawResource(R.mipmap.setting);//获取动图资源
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[2048];
            int len = 0;
            while (((len = is.read(b, 0, 2048)) != -1)) {
                bos.write(b, 0, len);
            }
            bos.flush();//刷新流，确保传递完全
            byte[] bytes = bos.toByteArray();//转换成Byte数组
            activity_main_setting_gif.setBytes(bytes);//设置gif图片
            activity_main_setting_gif.startAnimation();//运行动画
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.activity_main_setting_gif)
    protected void settingClick() {
        SharedPreferences sharedPreferences = getSharedPreferences("time", 0);
        if (!"".equals(sharedPreferences.getString("settingTime", ""))) {
            settingTime = sharedPreferences.getString("settingTime", "");
        }
        SharedPreferences sharedPreferences1 = getSharedPreferences("setper",0);
        setperFlag = sharedPreferences1.getInt("setperFlag",0);
        LinearLayout linearLayout = new LinearLayout(this);
        View inflate = LayoutInflater.from(this).inflate(R.layout.setting_pop, linearLayout, false);
        popupWindow = new PopupWindow(inflate);
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.mipmap.pop_bg));
        popupWindow.setFocusable(true);  //该值为false时，点击弹窗框外面window不会消失，即使设置了背景也无效，只能由dismiss()关闭
        TextView timeTv = inflate.findViewById(R.id.setting_pop_setting_time);
        //显示时间
        if (("").equals(settingTime)) {
            popupWindow.setHeight(100);
            timeTv.setVisibility(View.GONE);
        } else {
            popupWindow.setHeight(180);
            timeTv.setVisibility(View.VISIBLE);
            timeTv.setText("今天 " + settingTime);
        }
        popupWindow.setWidth(210);
        popupWindow.update();
        popupWindow.showAsDropDown(activity_main_setting_gif);
        TextView settingTv = inflate.findViewById(R.id.setting_pop_setting_tv);
        settingTv.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting_pop_setting_tv:
                isSetClick = true;
                if (setperFlag!=1){
                    showMissingPermissionDialog();
                } else {
                    setTime();
                }
                popupWindow.dismiss();
                break;
        }
    }
    class MyServeiceConn implements ServiceConnection {
        // 服务被绑定成功之后执行
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //service为onBind方法返回的Service实例
            myBinder = (MyBinder) service;
            System.out.println("***************************** onServiceConnected");
            final Window win = getWindow();
            win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                    | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        }
        // 服务奔溃或者被杀掉执行
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }
    class MyBinder extends Binder {
        MyBinder getService(){
            return MyBinder.this;
        }
    }

    /**
     * 显示提示信息
     *
     * @since 2.5.0
     */
    private void showMissingPermissionDialog() {
        try {
          WaringAlertDialog waringAlertDialog =  new WaringAlertDialog(this, "当前应用缺少必要权限。请点击 > 设置 > 权限 > 打开【锁屏显示】 【后台弹出界面】 【显示悬浮窗】权限", "去设置", "已设置", new WaringAlertDialog.OnDialogButtonClickListener() {
                @Override
                public void onDialogButtonClick(boolean isPositive) {
                    if (isPositive){
                        SharedPreferences sharedPreferences = getSharedPreferences("setper",0);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("setperFlag",1);
                        editor.commit();
                        startAppSettings();
                    }else {
                        SharedPreferences sharedPreferences = getSharedPreferences("setper",0);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("setperFlag",1);
                        editor.commit();
                        setTime();
                    }
                }
            });
            waringAlertDialog.setCancelable(false);
            waringAlertDialog.show();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
    //设置闹钟
    private void setTime(){
        isSetClick =false;
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
//                AlarmManager alarmManager = (AlarmManager) getSystemService(Service.ALARM_SERVICE);
        new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker arg0, int hourOfDay, int minute) {
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                //建立Intent和PendingIntent来调用闹钟管理器
                Intent intent = new Intent(MainActivity.this, TimeService.class);
                intent.setAction("dialog");

                PendingIntent pendingIntent = PendingIntent.getService(MainActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                //获取闹钟管理器
                AlarmManager alarmManager = (AlarmManager) getSystemService(Service.ALARM_SERVICE);
                //设置闹钟
                // pendingIntent 为发送广播
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                }
                String h1 = null, m1 = null;
                if (String.valueOf(hourOfDay).length() < 2) {
                    h1 = "0" + String.valueOf(hourOfDay);
                } else {
                    h1 = String.valueOf(hourOfDay);
                }
                if (String.valueOf(minute).length() < 2) {
                    m1 = "0" + String.valueOf(minute);
                } else {
                    m1 = String.valueOf(minute);
                }
                settingTime = h1 + ":" + m1;
                SharedPreferences sharedPreferences1 = getSharedPreferences("time", 0);
                SharedPreferences.Editor editor = sharedPreferences1.edit();
                editor.putString("settingTime", settingTime);
                editor.commit();
                Toast.makeText(MainActivity.this, "设置闹钟的时间为：" + h1 + ":" + m1, Toast.LENGTH_SHORT).show();
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
    }
}
