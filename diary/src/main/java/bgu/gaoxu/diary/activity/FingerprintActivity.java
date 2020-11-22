package bgu.gaoxu.diary.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.transition.Explode;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.wei.android.lib.fingerprintidentify.FingerprintIdentify;
import com.wei.android.lib.fingerprintidentify.base.BaseFingerprint;
import com.xuexiang.xui.widget.imageview.RadiusImageView;
import com.xuexiang.xui.widget.popupwindow.bar.CookieBar;
import com.xuexiang.xui.widget.toast.XToast;

import bgu.gaoxu.diary.MainActivity;
import bgu.gaoxu.diary.R;

public class FingerprintActivity extends AppCompatActivity {
    private TextView textView;
    private RadiusImageView radiusImageView;
    private FingerprintIdentify mFingerprintIdentify;
    private static final int MAX_AVAILABLE_TIMES = 3;
    private Bundle bundle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init_actionBar();
        Intent its = getIntent();
        bundle = its.getExtras();

        Intent it = new Intent();
        setContentView(R.layout.activity_fingerprint);
        init_layout();
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                it.setClass(FingerprintActivity.this, MainActivity.class);
                it.putExtras(bundle);
                startActivity(it, ActivityOptions.makeSceneTransitionAnimation(FingerprintActivity.this).toBundle());
                finish();
            }
        });
        radiusImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //开始识别
                popMessage();
                start();
            }
        });
        //初始化指纹
        long time = System.currentTimeMillis();
        mFingerprintIdentify = new FingerprintIdentify(getApplicationContext());
        mFingerprintIdentify.setSupportAndroidL(true);
        mFingerprintIdentify.setExceptionListener(new BaseFingerprint.ExceptionListener() {
            @Override
            public void onCatchException(Throwable exception) {
                XToast.success(FingerprintActivity.this,exception.getLocalizedMessage());
                start();
            }
        });
        mFingerprintIdentify.init();
        //验证条件
        if (!mFingerprintIdentify.isFingerprintEnable()) {
            XToast.warning(FingerprintActivity.this,"条件不足，请尝试跳过").show();
            return;
        }
        //默认开启指纹识别
        start();
    }

    public void start() {
        mFingerprintIdentify.startIdentify(MAX_AVAILABLE_TIMES, new BaseFingerprint.IdentifyListener() {
            Intent it = new Intent();
            @Override
            public void onSucceed() {
                XToast.success(FingerprintActivity.this,"验证通过").show();
                it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                it.setClass(FingerprintActivity.this, MainActivity.class);
                it.putExtras(bundle);
                startActivity(it);
                finish();
            }

            @Override
            public void onNotMatch(int availableTimes) {
                XToast.warning(FingerprintActivity.this,"指纹不匹配，剩余"+availableTimes+"次").show();
            }

            @Override
            public void onFailed(boolean isDeviceLocked) {
                XToast.error(FingerprintActivity.this,"验证失败,重新尝试或跳过").show();
                //关闭指纹
                mFingerprintIdentify.cancelIdentify();
            }

            @Override
            public void onStartFailedByDeviceLocked() {
                XToast.error(FingerprintActivity.this,"开始失败，设备暂时锁定").show();
                finish();
            }
        });
    }
    private void init_layout() {
        textView = findViewById(R.id.jumpTomain);
        radiusImageView = findViewById(R.id.check_finger);
    }

    private void init_actionBar() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.login));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }
    @Override
    protected void onPause() {
        super.onPause();
        mFingerprintIdentify.cancelIdentify();
//       XToast.error(FingerprintActivity.this,"指纹验证已停止，请重新打开APP再试").show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFingerprintIdentify.cancelIdentify();
    }
    private void popMessage(){
        CookieBar.builder(FingerprintActivity.this)
                .setTitle("请将手指放在指纹识别器上")
                .setMessage("目前指纹识别还在测试中，如有异常信息导致无法登录，请尝试跳过或询问管理员")
                .setDuration(3000)
                .setBackgroundColor(R.color.purple_200)
                .setMessageColor(R.color.textColor)
                .setTitleColor(R.color.textColor)
                .show();
    }
}