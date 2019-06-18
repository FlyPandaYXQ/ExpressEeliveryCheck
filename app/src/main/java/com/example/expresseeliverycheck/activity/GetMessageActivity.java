package com.example.expresseeliverycheck.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;

import com.example.expresseeliverycheck.R;
import com.example.expresseeliverycheck.adapter.GetSmsAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.ButterKnife;

/**
 * @author FlyPanda@若曦
 */
public class GetMessageActivity extends AppCompatActivity {
    private BroadcastReceiver broadcastReceiver;
    private IntentFilter filter;
    private Handler handler;
    private String strSms;
    private RecyclerView recyclerView;
    private GetSmsAdapter getSmsAdapter;
    private String patternCoder = "(?<!\\\\d)\\\\d{6}(?!\\\\d)";
    private List<HashMap<String, String>> smsList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getsms);
        ButterKnife.bind(this);
//        getSmsAdapter = new GetSmsAdapter(this,smsList);
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                //处理短息内容
                recyclerView.setAdapter(getSmsAdapter);
            }
        };
        filter = new IntentFilter();
        filter.addAction("android.intent.action.BroadcastReceiver");
        filter.setPriority(Integer.MAX_VALUE);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Object[] objects = (Object[]) intent.getExtras().get("pdus");
                for (Object obj : objects) {
                    byte[] bytes = (byte[]) obj;
                    SmsMessage smsMessage = SmsMessage.createFromPdu(bytes);
                    //短信的内容
                    String message = smsMessage.getMessageBody();
                    Log.d("FlyPanda==========>>>", "message==========>>>" + message);
                    //短信的手机号
                    String smsTell = smsMessage.getOriginatingAddress();
                    Log.d("FlyPanda==========>>>", "smsTell==========>>>" + smsTell);
                    // Time time = new Time();
                    // time.set(sms.getTimestampMillis());
                    // String time2 = time.format3339(true);
                    // Log.d("logo", from + "   " + message + "  " + time2);
                    // strContent = from + "   " + message;
                    // handler.sendEmptyMessage(1);
                    if (!TextUtils.isEmpty(message)) {
                        String code = patternCoder(message);
                        strSms = code;
                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("sms", message);
                        hashMap.put("code", smsTell);
                        smsList.add(hashMap);
                        handler.sendEmptyMessage(1);
                    }
                }
            }
        };
        registerReceiver(broadcastReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    /**
     * 匹配短信中间的6个数字（验证码等）
     *
     * @param patternContent
     * @return
     */
    private String patternCoder(String patternContent) {
        if (TextUtils.isEmpty(patternContent)) {
            return null;
        }
        Pattern pattern = Pattern.compile(patternCoder);
        Matcher matcher = pattern.matcher(patternContent);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

}
