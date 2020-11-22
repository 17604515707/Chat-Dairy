package bgu.gaoxu.diary.activity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.transition.Explode;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smart.refresh.header.MaterialHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xui.widget.toast.XToast;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.Serializable;
import java.security.Signature;
import java.util.ArrayList;
import java.util.List;

import bgu.gaoxu.diary.R;
import bgu.gaoxu.diary.adapter.DiaryAdapter;
import bgu.gaoxu.diary.entity.Diary;
import bgu.gaoxu.diary.entity.DiaryJsonBean;
import bgu.gaoxu.diary.tts.TtsDemo;
import bgu.gaoxu.diary.utils.GsonUtils;
import bgu.gaoxu.diary.utils.LoginState;
import okhttp3.Call;

/**
 * Created by Xuchaoyue on 2020/11/14 22:06
 */
public class MyDiaryList extends AppCompatActivity {
    private TextView diary_title, diary_context;
    private DiaryAdapter diaryAdapter;
    private RecyclerView rv;
    private TitleBar mTitleBar;
    private Bundle bundle;
    private int user_id;
    SmartRefreshLayout mRefreshLayout;
    private MaterialHeader mMaterialHeader;
    private SharedPreferences mySharedPreferences;
    private List<Diary> list = new ArrayList<Diary>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setEnterTransition(new Explode());
        init_actionBar();
        setContentView(R.layout.mydiary);
        mySharedPreferences = getSharedPreferences("user_info", MODE_PRIVATE);
        LoginState loginState = new LoginState();
        user_id = loginState.verify(MyDiaryList.this);

        init_layout();
        initData();
        initRecyclerView();
        init_Refresh();
        //自动加载
        mRefreshLayout.autoRefresh();
    }

    private void init_Refresh() {
        mMaterialHeader.setShowBezierWave(true);
        mRefreshLayout.setEnableHeaderTranslationContent(true);
        mRefreshLayout.setHeaderHeight(100);
    }

    private void init_layout() {
        //获取RecyclerView
        rv = (RecyclerView) findViewById(R.id.rv_mydiarylist);
        mRefreshLayout = (SmartRefreshLayout) findViewById(R.id.refreshLayout);
        mMaterialHeader = (MaterialHeader) mRefreshLayout.getRefreshHeader();
    }


    private void initLayout() {
        diary_title = findViewById(R.id.diary_title);
        diary_context = findViewById(R.id.diary_context);
    }

    private void init_actionBar() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.login));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    private void initData() {
//        bundle  = getArguments();
//        if (bundle!=null){
//            list = (List<Diary>) bundle.getSerializable("result");
//        }
        Diary diary = new Diary();
        diary.setDiary_title("正在拼命加载中...");
        diary.setDiary_context("系统提示");
        diary.setDiary_time("Now!");
        diary.setDiary_ID(1);
        list.add(diary);

        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                list.clear();
                if (user_id != -1) {
                    OkHttpUtils.post().url("http://www.ga666666.club:8080/mydiary/getDiaries.do").addParams("userid", user_id + "").build()
                            .execute(new StringCallback() {
                                @Override
                                public void onError(Call call, Exception e, int id) {

                                }

                                @Override
                                public void onResponse(String response, int id) {
                                    DiaryJsonBean diaryJsonBean = GsonUtils.fromJson(response, DiaryJsonBean.class);
                                    list = diaryJsonBean.getDiary();
                                    SharedPreferences.Editor ed = mySharedPreferences.edit();
                                    if (list.size()==0){
                                        Diary diary = new Diary();
                                        diary.setDiary_title("哎呀没有找到...");
                                        diary.setDiary_context("系统提示");
                                        diary.setDiary_time("Now!");
                                        diary.setDiary_ID(1);
                                        list.add(diary);
                                        ed.putInt("diary_num", list.size());
                                    }else{
                                        ed.putInt("diary_num", list.size());
                                    }
                                    ed.apply();
                                    initRecyclerView();
                                }
                            });
                    XToast.info(MyDiaryList.this, "刷新成功").show();
                    refreshLayout.finishRefresh();
                } else {
                    Diary diary = new Diary();
                    diary.setDiary_title("哎呀没有找到...");
                    diary.setDiary_context("系统提示");
                    diary.setDiary_time("Now!");
                    diary.setDiary_ID(1);
                    list.add(diary);
                    initRecyclerView();
                    XToast.info(MyDiaryList.this, "还没有登录哦~").show();
                    refreshLayout.finishRefresh();
                }
            }
        });

    }

    private void initRecyclerView() {

        //创建adapter
        diaryAdapter = new DiaryAdapter(MyDiaryList.this, list);
        diaryAdapter.setMyClickListener(new DiaryAdapter.IMyClickListener() {
            @Override
            public void myOnclick(int postion) {
                Intent it = new Intent(MyDiaryList.this, DiaryReadAcyivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("result", (Serializable) list.get(postion));
                it.putExtras(bundle);
                startActivity(it, ActivityOptions.makeSceneTransitionAnimation(MyDiaryList.this).toBundle());
            }

            @Override
            public void onItemLongClick(View view, int position) {
                popMessage(list.get(position));
            }
        });
        Log.e("count", diaryAdapter.getItemCount() + "");

        rv.setAdapter(diaryAdapter);
        //设置item的分割线
        //rv.addItemDecoration(new DividerItemDecoration(MyDiaryList.this, DividerItemDecoration.VERTICAL));

        rv.setLayoutManager(new LinearLayoutManager(MyDiaryList.this, LinearLayoutManager.VERTICAL, false));
    }

    public void popMessage(Diary diary) {
        Intent it = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("diary", (Serializable) diary);
        it.setClass(MyDiaryList.this, TtsDemo.class);
        it.putExtras(bundle);
        new MaterialDialog.Builder(MyDiaryList.this)
                .title(diary.getDiary_title())
                .content("点击 “确认” 即可语音朗读")
                .positiveText("确认").onPositive((dialog, which) -> MyDiaryList.this.startActivity(it))
                .negativeText("取消")
                .show();
    }
}
