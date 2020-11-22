package bgu.gaoxu.diary.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.xuexiang.xui.widget.toast.XToast;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import bgu.gaoxu.diary.R;
import bgu.gaoxu.diary.adapter.MsgAdapter;
import bgu.gaoxu.diary.entity.Message_list;
import bgu.gaoxu.diary.utils.LoginState;
import okhttp3.Call;

public class ChatActivity extends AppCompatActivity {
    private int user_id;
    private String name;
    private TextView tv_chat_name;
    private List<Message_list> msgList = new ArrayList<>();
    private EditText inputText;
    private ImageButton send;
    private RecyclerView msgRecyclerView;
    private MsgAdapter adapter;
    private SharedPreferences mySharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init_actionBar();
        setContentView(R.layout.activity_chat);
        init_layout();
        mySharedPreferences = this.getSharedPreferences("user_info", 0);
        LoginState loginState = new LoginState();
        user_id = loginState.verify(ChatActivity.this);
        //获取外部传入数据
        Intent intent = getIntent();
        Bundle bundle  = intent.getExtras();
        String sendname = bundle.getString("sendname");
        msgList = (List<Message_list>) bundle.getSerializable("message");
        tv_chat_name.setText(sendname);
        int send_id = bundle.getInt("send_id");
        //设置返回键
        ImageButton imageButton = (ImageButton)findViewById(R.id.btn_chat_back);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        inputText = (EditText) findViewById(R.id.input_text);
        send =  findViewById(R.id.btn_chat_send);
        msgRecyclerView = (RecyclerView) findViewById(R.id.msg_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        msgRecyclerView.setLayoutManager(layoutManager);
        adapter = new MsgAdapter(ChatActivity.this,msgList);

        msgRecyclerView.setAdapter(adapter);
        //自动滑动到底部
        msgRecyclerView.smoothScrollToPosition(adapter.getItemCount()-1);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = inputText.getText().toString();
                if (!"".equals(content)) {
                    Message_list list = new Message_list();
                    list.setMsg_id(0);
                    String user_name = mySharedPreferences.getString("user_name","null");
                    int user_id = mySharedPreferences.getInt("user_id",-1);
                    String imgpath = mySharedPreferences.getString("user_photo","null");
                    list.setSend_user_name(user_name);

                    list.setMessage(paiban(content));
                    list.setSend_user_id(user_id);
                    list.setSend_user_photo(imgpath);
                    list.setUser_id(send_id);
                    list.setSend_time("2020");
                    msgList.add(list);
                    adapter.notifyItemInserted(msgList.size()-1);
                    msgRecyclerView.scrollToPosition(msgList.size()-1);
                    inputText.setText("");
                    OkHttpUtils.post().url("http://www.ga666666.club:8080/mydiary/insertMessage")
                            .addParams("user_id",send_id+"")
                            .addParams("send_user_photo",imgpath)
                            .addParams("send_user_name",user_name)
                            .addParams("send_user_id",user_id+"")
                            .addParams("message",content).build().execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {

                        }

                        @Override
                        public void onResponse(String response, int id) {
                                if (response.equals("SUCCESS")){
                                    XToast.success(ChatActivity.this,"发送成功").show();
                                }else{
                                    XToast.error(ChatActivity.this,"发送失败").show();
                                }
                        }
                    });
                }
            }
        });

    }
    private void init_layout() {
        tv_chat_name = findViewById(R.id.tv_chat_name);
    }
    public static String paiban(String s) {
        int count = 16;
        StringBuilder  s1 = new StringBuilder(s);
        while (s1.length()>16) {
            s1.insert(count, "\n");
            count+=(17);
            if (count>s.length()) {
                break;
            }
        }
        s = s1.toString();
        return s;
    }
    private void init_actionBar() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.login));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }
}