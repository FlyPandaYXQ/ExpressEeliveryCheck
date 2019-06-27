package com.example.expresseeliverycheck.service;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.KeyguardManager;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.expresseeliverycheck.MyApplication;
import com.example.expresseeliverycheck.R;
import com.example.expresseeliverycheck.activity.DialogActivity;
import com.example.expresseeliverycheck.activity.MainActivity;
import com.example.expresseeliverycheck.until.ActivityCollector;
import com.example.expresseeliverycheck.until.AlertDialog;


public class TimeService extends Service {
    private final static String TAG = "TimeService";

    private Vibrator vibrate;
    private MediaPlayer mediaPlayer;
    private AlertDialog alertDialog;

    //    private AlarmReceiver alarmReceiver;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //创建Service时调用该方法，只调用一次
    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("************************--onCreate()--");
    }

    //每次启动Servcie时都会调用该方法
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("*************************--onStartCommand()--");
        wakeUpAndUnlock(getApplicationContext());
        SharedPreferences sharedPreferences = getSharedPreferences("time", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("settingTime","");
        editor.commit();
//        SharedPreferences sharedPreferences = getSharedPreferences("ExpressNum", 0);
//        int nowExpressNum = sharedPreferences.getInt("nowExpressNum", 0);
//        SharedPreferences sharedPreferences1 = getSharedPreferences("setper", 0);
//        int setperFlag = sharedPreferences1.getInt("setperFlag", 0);
        Intent intent1 = new Intent(this, DialogActivity.class);
        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplication().startActivity(intent1);
        if (!ActivityCollector.isActivityExist(DialogActivity.class)){
            vibrate = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            long[] pattern = {800, 500, 400, 300};   // 停止 开启 停止 开启
            vibrate.vibrate(pattern, -1);           //重复两次上面的pattern 如果只想震动一次，index设为-1 参数2是从指定下标开始重复
        }
//
//        vibrate = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
//        long[] pattern = {800, 500, 400, 300};   // 停止 开启 停止 开启
//        vibrate.vibrate(pattern, 0);           //重复两次上面的pattern 如果只想震动一次，index设为-1 参数2是从指定下标开始重复
//        // 设置铃声
//        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.cnwav);
//        mediaPlayer.start();
//        mediaPlayer.setLooping(true);


//        LayoutInflater inflater = LayoutInflater.from(getBaseContext());
//        View view = inflater.inflate(R.layout.dialog_custom, null);
//        alertDialog = new AlertDialog(getBaseContext(), "拿快递啦！！！", "今天有" + nowExpressNum + "件快递要拿哦~(@^_^@)~", "", "", new alertDialogClick());
//        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
//        alertDialog.getWindow().setContentView(view);
//        alertDialog.getWindow().setBackgroundDrawableResource(R.mipmap.aler_bg);
//        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//            @Override
//            public void onDismiss(DialogInterface dialog) {
//                vibrate.cancel();
//                mediaPlayer.stop();
//                mediaPlayer.release();//释放资源
//            }
//        });
//        alertDialog.show();

        return super.onStartCommand(intent, flags, startId);
    }


    //解绑Servcie调用该方法
    @Override
    public boolean onUnbind(Intent intent) {
        System.out.println("--onUnbind()--");
        return super.onUnbind(intent);
    }


    //退出或者销毁时调用该方法
    @Override
    public void onDestroy() {
        super.onDestroy();
        vibrate.cancel();
//        mediaPlayer.stop();
//        mediaPlayer.release();//释放资源
    }

    public void wakeUpAndUnlock(Context context) {
        //屏锁管理器
        KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock kl = km.newKeyguardLock("unLock");
        //解锁
        kl.disableKeyguard();
        //获取电源管理器对象
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        //获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag
        @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "bright");
        //点亮屏幕
        wl.acquire();
        //释放
//        wl.release();
    }

    class alertDialogClick implements AlertDialog.OnDialogButtonClickListener {
        @Override
        public void onDialogButtonClick(boolean isPositive) {
            alertDialog.dismiss();
        }
    }
}
