package com.example.expresseeliverycheck.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.v7.app.ActionBar;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.expresseeliverycheck.R;
import com.example.expresseeliverycheck.service.TimeService;
import com.example.expresseeliverycheck.until.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DialogActivity extends BaseActivity {
    @BindView(R.id.dialog_ll)
    protected LinearLayout dialog_ll;
    @BindView(R.id.dialog_title)
    protected TextView dialog_title;
    @BindView(R.id.dialog_message)
    protected TextView dialog_message;

    private Intent intent;
    protected MyBinder myBinder;
    protected MyServeiceConn myServeiceConn= new MyServeiceConn();


    private Vibrator vibrate;
    private MediaPlayer mediaPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog);
        ButterKnife.bind(this);
        System.out.println("************************************ onCreate");
        //锁屏亮屏
        final Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        //隐藏标题栏
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null) {
            actionbar.hide();
        }
        setTitle(null);
        //启动service
        intent = new Intent(this, TimeService.class);
//        startService(intent);
        bindService(intent,myServeiceConn,Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("************************************ onStart");
        SharedPreferences sharedPreferences = getSharedPreferences("ExpressNum", 0);
        int nowExpressNum = sharedPreferences.getInt("nowExpressNum", 0);
        dialog_title.setText("拿快递啦！！！");
        dialog_message.setText("今天有" + nowExpressNum + "件快递要拿哦~(@^_^@)~");


        vibrate = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {800, 500, 400, 300};   // 停止 开启 停止 开启
        vibrate.vibrate(pattern, 0);           //重复两次上面的pattern 如果只想震动一次，index设为-1 参数2是从指定下标开始重复
        // 设置铃声
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.cnwav);
        mediaPlayer.start();
        mediaPlayer.setLooping(true);

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStop() {
        super.onStop();
        vibrate.cancel();
        mediaPlayer.stop();
        mediaPlayer.release();//释放资源
        if (myBinder!=null){
            unbindService(myServeiceConn);
        }
        stopService(intent);
        SharedPreferences sharedPreferences1 = getSharedPreferences("time",0);
        SharedPreferences.Editor editor = sharedPreferences1.edit();
        editor.putString("settingTime","");
        editor.commit();
    }
    class MyServeiceConn implements ServiceConnection {
        // 服务被绑定成功之后执行
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //service为onBind方法返回的Service实例
            myBinder = (MyBinder) service;

        }
        // 服务奔溃或者被杀掉执行
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }
    class MyBinder extends Binder{
        MyBinder getService(){
            return MyBinder.this;
        }
    }
}
