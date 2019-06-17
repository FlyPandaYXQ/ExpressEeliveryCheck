package com.example.expresseeliverycheck.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.expresseeliverycheck.R;
import com.example.expresseeliverycheck.until.MiuiUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WelComeActivity extends AppCompatActivity {
    private static final int PERMISSON_REQUESTCODE = 0;
    /**
     * 需要进行检测的权限数组
     */
    protected String[] needPermissions = {
            Manifest.permission.READ_SMS,
    };
    /**
     * 判断是否需要检测，防止不停的弹框
     */
    private boolean isNeedCheck = true;

    @BindView(R.id.activity_welcome_min)
    protected TextView activity_welcome_min;
    @BindView(R.id.activity_welcome_skipping)
    protected LinearLayout activity_welcome_skipping;

    private int MIXMIN = 5000;
    private CountDownTimer timer;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);
        SharedPreferences sharedPreferences = getSharedPreferences("flypanda", 0);
        if (1 == sharedPreferences.getInt("first", 0)) {
            //非第一次进倒计时1秒
            activity_welcome_skipping.setVisibility(View.GONE);
            countDown(1000);
        } else {
            //如果第一个倒计时5秒 获取权限
            activity_welcome_min.setText("(5)秒");
            if (Build.VERSION.SDK_INT >= 23) {
                if (isNeedCheck) {
                    checkPermissions(needPermissions);
                } else {
                    countDown(MIXMIN);
                }
            } else {
                countDown(MIXMIN);
            }
        }
    }


    //倒计时器
    private void countDown(int time) {
        /** 倒计时，一次1秒 */
        timer = new CountDownTimer(time, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // TODO Auto-generated method stub
                activity_welcome_min.setText("(" + millisUntilFinished / 1000 + ")秒");
            }

            @Override
            public void onFinish() {
                skippingClick();
            }
        }.start();
    }

    @OnClick(R.id.activity_welcome_skipping)
    protected void skippingClick() {
        startActivity(new Intent(this, MainActivity.class));
        timer.cancel();
        finish();
    }

    //==================================== 授权 ============================================//


    @Override
    protected void onRestart() {
        super.onRestart();
        isNeedCheck = true;
        if (Build.VERSION.SDK_INT >= 23) {
            if (isNeedCheck) {
                checkPermissions(needPermissions);
            } else {
                countDown(MIXMIN);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Build.VERSION.SDK_INT >= 23) {
            if (isNeedCheck) {
                checkPermissions(needPermissions);
            } else {
                countDown(MIXMIN);
            }
        }
    }

    /**
     * @param
     * @since 2.5.0
     */
    @TargetApi(23)
    private void checkPermissions(String... permissions) {
        try {
            if (Build.VERSION.SDK_INT >= 23 && getApplicationInfo().targetSdkVersion >= 23) {
                List<String> needRequestPermissonList = findDeniedPermissions(permissions);
                for (String s : needRequestPermissonList) {
                }
                if (null != needRequestPermissonList && needRequestPermissonList.size() > 0) {
                    try {
                        String[] array = needRequestPermissonList.toArray(new String[needRequestPermissonList.size()]);
                        Method method = getClass().getMethod("requestPermissions", new Class[]{String[].class, int.class});
                        method.invoke(this, array, 0);
                    } catch (Throwable e) {

                    }
                } else {
                    countDown(MIXMIN);
                }
            } else {
                countDown(MIXMIN);
            }

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取权限集中需要申请权限的列表
     *
     * @param permissions
     * @return
     * @since 2.5.0
     */
    @TargetApi(23)
    private List<String> findDeniedPermissions(String[] permissions) {
        try {
            List<String> needRequestPermissonList = new ArrayList<String>();
            if (Build.VERSION.SDK_INT >= 23 && getApplicationInfo().targetSdkVersion >= 23) {
                for (String perm : permissions) {
                    if (checkMySelfPermission(perm) != PackageManager.PERMISSION_GRANTED
                            || shouldShowMyRequestPermissionRationale(perm)) {
                        needRequestPermissonList.add(perm);
                    }
                }
            }
            return needRequestPermissonList;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    private int checkMySelfPermission(String perm) {
        try {
            Method method = getClass().getMethod("checkSelfPermission", new Class[]{String.class});
            Integer permissionInt = (Integer) method.invoke(this, perm);
            return permissionInt;
        } catch (Throwable e) {
        }
        return -1;
    }

    private boolean shouldShowMyRequestPermissionRationale(String perm) {
        try {
            Method method = getClass().getMethod("shouldShowRequestPermissionRationale", new Class[]{String.class});
            Boolean permissionInt = (Boolean) method.invoke(this, perm);
            return permissionInt;
        } catch (Throwable e) {
        }
        return false;
    }

    /**
     * 检测是否说有的权限都已经授权
     *
     * @param grantResults
     * @return
     * @since 2.5.0
     */
    private boolean verifyPermissions(int[] grantResults) {
        try {
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return true;
    }

    @TargetApi(23)
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] paramArrayOfInt) {
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                if (requestCode == PERMISSON_REQUESTCODE) {
                    if (!verifyPermissions(paramArrayOfInt)) {
                        getNoticeSmsPermissionDialog();
                        isNeedCheck = false;
                    } else {
                        // 用户同意
                        getNoticeSmsPermissionDialog();
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示提示信息
     *
     * @since 2.5.0
     */
    private void showMissingPermissionDialog() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("提示");
            builder.setMessage("当前应用缺少必要权限。请点击 > 设置 > 权限 > 打开所需权限");
            isNeedCheck = true;
            // 拒绝, 退出应用
            builder.setNegativeButton("取消",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                finish();
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }
                        }
                    });

            builder.setPositiveButton("设置",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                startAppSettings();
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }
                        }
                    });

            builder.setCancelable(false);

            builder.show();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void getNoticeSmsPermissionDialog() {
        if (MiuiUtils.SYS_MIUI.equals(MiuiUtils.getSystem())) {
            try {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("提示");
                builder.setMessage("需要获取【短息】【通知类短息】权限。请点击 > 设置 > 权限 > 打开所需权限");
                isNeedCheck = true;
                // 拒绝, 退出应用
                builder.setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    finish();
                                } catch (Throwable e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                builder.setPositiveButton("设置",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    startAppSettings();
                                } catch (Throwable e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                builder.setCancelable(false);

                builder.show();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        } else {
            getNoticeSmsPermissionDialog();
        }
    }

    /**
     * 启动应用的设置
     *
     * @since 2.5.0
     */
    private void startAppSettings() {
        try {
            Intent intent = new Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
