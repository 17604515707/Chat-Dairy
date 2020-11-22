package bgu.gaoxu.diary.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.xuexiang.xui.widget.popupwindow.bar.CookieBar;
import com.xuexiang.xui.widget.toast.XToast;

import bgu.gaoxu.diary.R;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        init_actionBar();
    }
    private void init_actionBar() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.login));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }
    public void  update(View view){
        XToast.success(AboutActivity.this,"当前为最新版本").show();
    }

    public void PopMessage(View view) {
        CookieBar.builder(AboutActivity.this)
                .setTitle("指导教师：施一飞")
                .setMessage("开发人员：高旭、徐朝岳、王佩棋")
                .setDuration(3000)
                .setBackgroundColor(R.color.colorPrimary)
                .setMessageColor(R.color.login)
                .setTitleColor(R.color.login)
                .show();
    }
}