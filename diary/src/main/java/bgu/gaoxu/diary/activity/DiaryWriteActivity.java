package bgu.gaoxu.diary.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.transition.Explode;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xui.widget.toast.XToast;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;

import java.io.Serializable;
import java.util.Date;

import bgu.gaoxu.diary.R;
import bgu.gaoxu.diary.entity.Diary;
import bgu.gaoxu.diary.tts.TtsDemo;
import okhttp3.Call;
import okhttp3.Response;
/**
 * Created by Xuchaoyue on 2020/11/14 22:06
 */
public class DiaryWriteActivity  extends AppCompatActivity {
    private EditText insert_title,insert_content;
    private Button insert,back;
    private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        sharedPreferences = getSharedPreferences("user_info", MODE_PRIVATE);
        super.onCreate(savedInstanceState);
        init_actionBar();
        setContentView(R.layout.diary_write);
        insertDiary();
        insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int user_id = sharedPreferences.getInt("user_id",-1);
                String title = insert_title.getText().toString();   // 把输入框中的内容保存起来
                String content = insert_content.getText().toString();
                if (title.length()>1&&content.length()>1&&user_id!=-1){

                    OkHttpUtils
                            .post()
                            .url("http://129.28.159.51:8080/mydiary/insertDairy")
                            .addParams("title", title)
                            .addParams("context", content)
                            .addParams("user_id",user_id+"")
                            .build()
                            .execute(new Callback() {
                                @Override
                                public Object parseNetworkResponse(Response response, int id) throws Exception {
                                    return null;
                                }
                                @Override
                                public void onError(Call call, Exception e, int id) {
                                }
                                @Override
                                public void onResponse(Object response, int id) {
                                    Log.e("result",response+","+title);
                                            Intent it = new Intent();
                                            it.setClass(DiaryWriteActivity.this,DiaryReadAcyivity.class);
                                            Bundle bundle = new Bundle();
                                            Diary diary = new Diary();
                                            diary.setDiary_title(title);
                                            diary.setDiary_context(content);
                                            diary.setDiary_time("2020");
                                            diary.setDiary_ID(10000);
                                            bundle.putSerializable("result",(Serializable)diary);
                                            it.putExtras(bundle);
                                            startActivity(it);
                                            finish();

                                }
                            });
                }else if(user_id==-1){
                    Intent it = new Intent();
                    it.setClass(DiaryWriteActivity.this,LoginActivity.class);
                    popMessage(it);
                }else{
                    XToast.error(DiaryWriteActivity.this,"用心书写，方得始终").show();
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
    public void insertDiary() {
        insert_title = findViewById(R.id.insert_title);        // 拿到输入框
        insert_content = findViewById(R.id.insert_content);
        insert = findViewById(R.id.btn_insert);
        back = findViewById(R.id.btn_back);
}
    private void init_actionBar() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.bottom_bgcolor));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }
    public void popMessage(Intent it){
        new MaterialDialog.Builder(DiaryWriteActivity.this)
                .title("请先登录，再发表哦")
                .content("点击 “确认” 即跳转登录")
                .positiveText("确认").onPositive((dialog, which) ->startActivity(it))
                .negativeText("取消")
                .show();
    }
}
