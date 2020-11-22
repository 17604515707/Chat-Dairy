package bgu.gaoxu.diary.activity;


import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smart.refresh.header.MaterialHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.xuexiang.xui.widget.toast.XToast;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.Serializable;
import java.util.ArrayList;

import bgu.gaoxu.diary.R;
import bgu.gaoxu.diary.adapter.DiaryAdapter;
import bgu.gaoxu.diary.adapter.MyFriendAdapter;
import bgu.gaoxu.diary.entity.Diary;
import bgu.gaoxu.diary.entity.DiaryJsonBean;
import bgu.gaoxu.diary.entity.FollowJsonBean;
import bgu.gaoxu.diary.entity.Follow_list;
import bgu.gaoxu.diary.utils.GsonUtils;
import bgu.gaoxu.diary.utils.LoginState;
import okhttp3.Call;

public class MyFriendsActivity extends AppCompatActivity {
    private RecyclerView rv;
    private MyFriendAdapter myFriendAdapter;
    private int user_id;
    SmartRefreshLayout mRefreshLayout;
    private MaterialHeader mMaterialHeader;
    SharedPreferences mySharedPreferences;
    ArrayList<Follow_list> list = new ArrayList<Follow_list>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init_actionBar();
        setContentView(R.layout.activity_my_friends);
        mySharedPreferences = getSharedPreferences("user_info", MODE_PRIVATE);
        LoginState loginState = new LoginState();
        user_id = loginState.verify(MyFriendsActivity.this);
        init_layout();
        init_Data();
        initRecyclerView();
        //自动加载
        mRefreshLayout.autoRefresh();
    }

    private void init_layout() {
        //获取RecyclerView
        rv = (RecyclerView) findViewById(R.id.rv_myfriends);
        mRefreshLayout = (SmartRefreshLayout) findViewById(R.id.myfriend_refreshLayout);
        mMaterialHeader = (MaterialHeader) mRefreshLayout.getRefreshHeader();
    }

    private void init_actionBar() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.login));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    private void init_Data() {
        Follow_list userFollow = new Follow_list();
        userFollow.setFollow_id(11);
        userFollow.setFollow_user_id(12);
        userFollow.setUser_id(13);
        userFollow.setFollow_user_name("高旭");
        userFollow.setFollow_user_img("https://gitee.com/gao666666/images/raw/master/image/20201110160925.png");
        list.add(userFollow);
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                list.clear();
                if (user_id != -1) {
                    OkHttpUtils.post().url("http://www.ga666666.club:8080/mydiary/getFollow.do").addParams("userid", user_id + "").build()
                            .execute(new StringCallback() {
                                @Override
                                public void onError(Call call, Exception e, int id) {

                                }

                                @Override
                                public void onResponse(String response, int id) {
                                    FollowJsonBean followJsonBean = GsonUtils.fromJson(response, FollowJsonBean.class);
                                    int erro_code = followJsonBean.getErro_code();

                                    if (erro_code == 0) {
                                        list = followJsonBean.getFollow_list();
                                        SharedPreferences.Editor ed = mySharedPreferences.edit();
                                        ed.putInt("follow_num", list.size());
                                        ed.apply();
                                    } else {
                                        Follow_list follow_list = new Follow_list();
                                        follow_list.setFollow_id(-1);
                                        follow_list.setFollow_user_id(-1);
                                        follow_list.setUser_id(-1);
                                        follow_list.setFollow_user_img("");
                                        follow_list.setFollow_user_name("暂时还没有好友，快使用扫一扫添加好友吧");
                                        list.add(follow_list);
                                    }
                                    initRecyclerView();
                                }
                            });
                    XToast.info(MyFriendsActivity.this, "刷新成功").show();
                    refreshLayout.finishRefresh();
                } else {
                    Follow_list follow_list = new Follow_list();
                    follow_list.setFollow_id(-1);
                    follow_list.setFollow_user_id(-1);
                    follow_list.setUser_id(-1);
                    follow_list.setFollow_user_img("");
                    follow_list.setFollow_user_name("请先登录哦~");
                    list.add(follow_list);
                    initRecyclerView();
                    XToast.info(MyFriendsActivity.this, "还没有登录哦~").show();
                    refreshLayout.finishRefresh();
                }
            }
        });
    }

    private void initRecyclerView() {
        //获取RecyclerView
        rv = (RecyclerView) findViewById(R.id.rv_myfriends);
        //创建adapter
        myFriendAdapter = new MyFriendAdapter(MyFriendsActivity.this, list);
        rv.setAdapter(myFriendAdapter);
        rv.setLayoutManager(new LinearLayoutManager(MyFriendsActivity.this, LinearLayoutManager.VERTICAL, false));
    }
}