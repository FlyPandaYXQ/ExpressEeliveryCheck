package com.example.expresseeliverycheck.service;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

import com.coolerfall.daemon.Daemon;
import com.example.expresseeliverycheck.R;
import com.example.expresseeliverycheck.activity.DialogActivity;
import com.example.expresseeliverycheck.activity.MainActivity;
import com.example.expresseeliverycheck.receiver.AlarmReceiver;
import com.example.expresseeliverycheck.until.ActivityCollector;
import com.example.expresseeliverycheck.until.AlertDialog;

import java.util.Calendar;


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
        //守护进程以及灰色保活来保证后台闹钟服务不被Kill掉
        Daemon.run(TimeService.this,
                TimeService.class, Daemon.INTERVAL_ONE_MINUTE);
        System.out.println("************************--onCreate()--");
    }

    //每次启动Servcie时都会调用该方法
    @SuppressLint("WrongConstant")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        flags = START_STICKY;
        System.out.println("*************************--onStartCommand()--");
//        String CHANNEL_ONE_ID = "com.primedu.cn";
//        String CHANNEL_ONE_NAME = "Channel One";
//        NotificationChannel notificationChannel = null;
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            notificationChannel = new NotificationChannel(CHANNEL_ONE_ID,
//                    CHANNEL_ONE_NAME, NotificationManager.IMPORTANCE_HIGH);
//            notificationChannel.enableLights(true);
//            notificationChannel.setLightColor(Color.RED);
//            notificationChannel.setShowBadge(true);
//            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
//            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//            manager.createNotificationChannel(notificationChannel);
//        }
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);
//
//        Notification notification = new Notification.Builder(this)
//                .setTicker("Nature")
//                .setSmallIcon(R.mipmap.logo)
//                .setContentTitle(getString(R.string.app_name))
//                .setContentIntent(pendingIntent)
//                .getNotification();
//        notification.flags |= Notification.FLAG_NO_CLEAR;
//        startForeground(0x111, notification);
        if (intent.getIntExtra("start", 0) == 2) {
            wakeUpAndUnlock(getApplicationContext());
            SharedPreferences sharedPreferences = getSharedPreferences("time", 0);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("settingTime", "");
            editor.commit();
            Intent intent1 = new Intent(this, DialogActivity.class);
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplication().startActivity(intent1);
            if (!ActivityCollector.isActivityExist(DialogActivity.class)) {
                vibrate = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                long[] pattern = {800, 500, 400, 300};   // 停止 开启 停止 开启
                vibrate.vibrate(pattern, -1);           //重复两次上面的pattern 如果只想震动一次，index设为-1 参数2是从指定下标开始重复
            }
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
        // 通过广播启动
        stopForeground(true);
        Intent intent = new Intent("com.example.expresseeliverycheck.startService");
        sendBroadcast(intent);
        // 直接启动service
//         Intent sevice = new Intent(this, TimeService.class);
//         this.startService(sevice);
//        startUploadService(getApplicationContext());
        // mediaPlayer.stop();
        // mediaPlayer.release();//释放资源
    }

    public void wakeUpAndUnlock(Context context) {
        // 屏锁管理器
        KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock kl = km.newKeyguardLock("unLock");
        // 解锁
        kl.disableKeyguard();
        // 获取电源管理器对象
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        // 获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag
        @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "bright");
        // 点亮屏幕
        wl.acquire();
        // 释放
        // wl.release();
    }

    class alertDialogClick implements AlertDialog.OnDialogButtonClickListener {
        @Override
        public void onDialogButtonClick(boolean isPositive) {
            alertDialog.dismiss();
        }
    }

    private void startUploadService(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("time", 0);
        int hourOfDay = sharedPreferences.getInt("hour", 0);
        int minute = sharedPreferences.getInt("min", 0);
        System.out.println("***************" + hourOfDay);
        System.out.println("***************" + minute);
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        //建立Intent和PendingIntent来调用闹钟管理器
        Intent intent1 = new Intent(context, TimeService.class);
        intent1.setAction("dialog");

        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);

        //获取闹钟管理器
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        // 设置闹钟
        // pendingIntent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }
}
