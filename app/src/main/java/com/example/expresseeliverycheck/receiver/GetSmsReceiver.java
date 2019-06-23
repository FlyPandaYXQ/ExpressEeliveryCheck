package com.example.expresseeliverycheck.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.example.expresseeliverycheck.activity.MainActivity;
import com.example.expresseeliverycheck.until.ConfigUtil;

/**
 * @author FlyPanda@若曦
 */
public class GetSmsReceiver extends BroadcastReceiver {
    Handler handler = new Handler();
    MainActivity mainActivity;

    @Override
    public void onReceive(Context context, Intent intent) {
        Object[] objects = (Object[]) intent.getExtras().get("pdus");
        StringBuilder stringBuilder = new StringBuilder();
//        for (Object object:objects){
//            byte[] pdusByte = (byte[]) object;
//            SmsMessage smsMessage = SmsMessage.createFromPdu(pdusByte);
//            smsMessage.getOriginatingAddress();//发送短信手机号
//        }
        Message message = new Message();
        message.what = ConfigUtil.RECEIVERED_MSG;
        handler.sendMessage(message);
        Intent intent1 = new Intent(String.valueOf(ConfigUtil.RECEIVERED_MSG));
        context.sendBroadcast(intent1);
    }
}
