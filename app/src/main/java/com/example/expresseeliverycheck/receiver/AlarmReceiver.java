package com.example.expresseeliverycheck.receiver;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.os.Build;
import android.os.PowerManager;
import android.os.Vibrator;

import com.example.expresseeliverycheck.activity.DialogActivity;
import com.example.expresseeliverycheck.activity.MainActivity;
import com.example.expresseeliverycheck.service.TimeService;
import com.example.expresseeliverycheck.until.ActivityCollector;
import com.example.expresseeliverycheck.until.AlertDialog;

import java.util.Calendar;


public class AlarmReceiver extends BroadcastReceiver {
    private AlertDialog alertDialog;
    private int nowExpressNum = 0;
    private Ringtone mRingTone;
    private Vibrator mVibrator;
    private MediaPlayer mediaPlayer;

    @Override
    public void onReceive(Context context, Intent intent) {
//        int code = intent.getIntExtra("expressNumCode", 0);
        if (intent.getAction().equals("com.example.expresseeliverycheck.startService")) {
            startUploadService(context);
        }
        System.out.println("*************************--AlarmReceiver--");

//        if ("dialog".equals(intent.getAction())){
//            Intent intent1 = new Intent(context,DialogActivity.class);
//            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(intent1);
//        }
//        if (ActivityCollector.isActivityExist(MainActivity.class)){
//            Intent intent1 = new Intent(context,MainActivity.class);
//            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(intent1);
//        }
//        SharedPreferences sharedPreferences = context.getSharedPreferences("ExpressNum", 0);
//        nowExpressNum = sharedPreferences.getInt("nowExpressNum", 0);

//        LayoutInflater inflater = LayoutInflater.from(context);
//        View view = inflater.inflate(R.layout.dialog_custom, null);
//        alertDialog = new AlertDialog(context, "拿快递啦！！！", "今天有" + nowExpressNum + "件快递要拿哦~(@^_^@)~", "", "", new alertDialogClick());
//        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
//        alertDialog.getWindow().setContentView(view);
//        alertDialog.getWindow().setBackgroundDrawableResource(R.mipmap.aler_bg);
//        alertDialog.show();
        /*
         * 想设置震动大小可以通过改变pattern来设定，如果开启时间太短，震动效果可能感觉不到
         * */

//        mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
//        long[] pattern = {800, 500, 400, 300};   // 停止 开启 停止 开启
//        mVibrator.vibrate(pattern, 0);           //重复两次上面的pattern 如果只想震动一次，index设为-1 参数2是从指定下标开始重复
//        // 设置铃声
//        mediaPlayer = MediaPlayer.create(context, R.raw.cnwav);
//       if (alertDialog.isShowing()){
//           mediaPlayer.start();
//           wakeUpAndUnlock(context);
//       }
//        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//            @Override
//            public void onDismiss(DialogInterface dialog) {
//                mVibrator.cancel();
//                mediaPlayer.stop();
//                mediaPlayer.release();//释放资源
//            }
//        });
    }


    class alertDialogClick implements AlertDialog.OnDialogButtonClickListener {
        @Override
        public void onDialogButtonClick(boolean isPositive) {
            alertDialog.dismiss();
        }
    }

    public static void wakeUpAndUnlock(Context context) {
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
   private void  startUploadService(Context context){
       SharedPreferences sharedPreferences = context.getSharedPreferences("time", 0);
       int hourOfDay = sharedPreferences.getInt("hour", 0);
       int minute = sharedPreferences.getInt("min", 0);
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
