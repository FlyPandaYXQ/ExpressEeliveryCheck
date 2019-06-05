package com.example.expresseeliverycheck.until;

import android.annotation.SuppressLint;
import android.app.Activity;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.util.Log;

public class SmsReadUntil {

    private int getSmsType;
    private Activity activity;
    final String SMS_URI_ALL = "content://sms/"; // 所有短信
    final String SMS_URI_INBOX = "content://sms/inbox"; // 收件箱
    final String SMS_URI_SEND = "content://sms/sent"; // 已发送
    final String SMS_URI_DRAFT = "content://sms/draft"; // 草稿
    final String SMS_URI_OUTBOX = "content://sms/outbox"; // 发件箱
    final String SMS_URI_FAILED = "content://sms/failed"; // 发送失败
    final String SMS_URI_QUEUED = "content://sms/queued"; // 待发送列表

    public SmsReadUntil(int getSmsType,Activity activity){
        this.getSmsType = getSmsType;
        this.activity = activity;
    }

    @SuppressLint("LongLogTag")
    public List<HashMap<String, String>> getSmsInPhone() {
        List<HashMap<String, String>> smsList = new ArrayList<>();
        StringBuilder smsBuilder = new StringBuilder();
        HashMap<String,String> hashMap = new HashMap();
        int index = 0;
        try {
            Uri uri = Uri.parse(SMS_URI_INBOX);
            String[] projection = new String[] { "_id", "address", "person",
                    "body", "date", "type", };
            Cursor cur = activity.getContentResolver().query(uri, projection, null,
                    null, "date desc"); // 获取手机内部短信
            System.out.println("==============================读取中"+cur);
            // 获取短信中最新的未读短信
            // Cursor cur = getContentResolver().query(uri, projection,
            // "read = ?", new String[]{"0"}, "date desc");
            if (cur.moveToFirst()) {
                System.out.println("==============================读取中"+cur.getCount());
                int index_Address = cur.getColumnIndex("address");
                int index_Person = cur.getColumnIndex("person");
                int index_Body = cur.getColumnIndex("body");
                int index_Date = cur.getColumnIndex("date");
                int index_Type = cur.getColumnIndex("type");

                do {
                    String strbody = cur.getString(index_Body);
                    long longDate = cur.getLong(index_Date);
//                    String strAddress = cur.getString(index_Address);
//                    int intPerson = cur.getInt(index_Person);
//                    int intType = cur.getInt(index_Type);

                    SimpleDateFormat dateFormat = new SimpleDateFormat(
                            "yyyy/MM/dd");
                    Date d = new Date(longDate);
                    String strDate = dateFormat.format(d);

                    String strType = "";
                    if (getSmsType == 1) {
                        strType = "接收";
                    } else if (getSmsType == 2) {
                        strType = "发送";
                    } else if (getSmsType == 3) {
                        strType = "草稿";
                    } else if (getSmsType == 4) {
                        strType = "发件箱";
                    } else if (getSmsType == 5) {
                        strType = "发送失败";
                    } else if (getSmsType == 6) {
                        strType = "待发送列表";
                    } else if (getSmsType == 0) {
                        strType = "所以短信";
                    } else {
                        strType = "null";
                    }
//                    smsBuilder.append("strbody:"+strbody + ", \t");
//                    smsBuilder.append(strDate + ", \t");
//                    smsBuilder.append("[ ");
//                    smsBuilder.append(strAddress + ", ");
//                    smsBuilder.append("intPerson:"+intPerson + ", \t");
//                    smsBuilder.append(strType);
//                    smsBuilder.append(" ]\n\n");
                    index++;
                    hashMap= new HashMap();
                    hashMap.put("strbody",strbody);
                    hashMap.put("strDate",strDate);
                    hashMap.put("index",index+"");
                    System.out.println("==============================读取中");
                    smsList.add(hashMap);
                } while (cur.moveToNext());

                if (!cur.isClosed()) {
                    cur.close();
                    cur = null;
                }
            } else {
                smsBuilder.append("no result!");
            }

            smsBuilder.append("getSmsInPhone has executed!");

        } catch (SQLiteException ex) {
            Log.d("SQLiteException in getSmsInPhone", ex.getMessage());
        }

        return smsList;
    }
}
