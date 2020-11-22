package bgu.gaoxu.diary.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.transition.Explode;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import bgu.gaoxu.diary.R;
import bgu.gaoxu.diary.entity.Diary;
/**
 * Created by Xuchaoyue on 2020/11/14 22:06
 */
public class DiaryReadAcyivity extends AppCompatActivity {
    private TextView diary_title,diary_context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setEnterTransition(new Explode());
        init_actionBar();
        setContentView(R.layout.activity_diary_read_acyivity);
        initLayout();
        Intent it = getIntent();
        Bundle bundle = it.getExtras();
        if (bundle!=null){
            Diary diary = (Diary) bundle.getSerializable("result");
            diary_title.setText(diary.getDiary_title());
            diary_context.setText("\u3000\u3000"+diary.getDiary_context());
            diary_context.setMovementMethod(ScrollingMovementMethod.getInstance());
        }else{
            diary_title.setText("没传过来");
        }
    }
    private void initLayout(){
        diary_title = findViewById(R.id.diary_title);
        diary_context = findViewById(R.id.diary_context);
    }
    private void init_actionBar() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.bottom_bgcolor));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

}