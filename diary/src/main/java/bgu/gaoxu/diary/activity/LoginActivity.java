package bgu.gaoxu.diary.activity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.transition.Explode;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.xuexiang.xui.widget.edittext.materialedittext.MaterialEditText;
import com.xuexiang.xui.widget.toast.XToast;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;

import bgu.gaoxu.diary.MainActivity;
import bgu.gaoxu.diary.R;
import bgu.gaoxu.diary.entity.Message_list;
import bgu.gaoxu.diary.entity.User;
import bgu.gaoxu.diary.utils.GsonUtils;
import bgu.gaoxu.diary.utils.PassWord;
import okhttp3.Call;

public class LoginActivity extends AppCompatActivity {
    private MaterialEditText ed_username,ed_password;
    private Button btn_login;
    private String username,password;
    private SharedPreferences mysharedPreferences;
    String storage = Environment.getExternalStorageDirectory().getPath() + "/LockDiary/User_Photo";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences mySharedPreferences = getSharedPreferences("user_info",MODE_PRIVATE);
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setEnterTransition(new Explode());
        setContentView(R.layout.activity_login);
        init_actionBar();
        init_layout();
        ed_username.setText(mySharedPreferences.getString("user_name",""));
        ed_password.setText(mySharedPreferences.getString("user_psd",""));
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    username = ed_username.getEditValue().toString();
                    password = ed_password.getEditValue().toString();
                    //验证密码
                    if (mySharedPreferences.getString("user_name","null").equals("null")||!username.equals(mySharedPreferences.getString("user_name","null"))){
                        if(verify(username,password)){

                            OkHttpUtils.post()
                                    .url("http://www.ga666666.club:8080/mydiary/getLogin.do")
                                    .addParams("username",username)
                                    .addParams("password",password)
                                    .build()
                                    .execute(new StringCallback() {

                                        @Override
                                        public void onError(Call call, Exception e, int id) {
                                            ed_username.setText("失败");
                                        }

                                        @Override
                                        public void onResponse(String response, int id) {
                                            User user = GsonUtils.fromJson(response,User.class);
                                            int erro_code = user.getErro_code();
                                            if(erro_code==0){

                                                SharedPreferences.Editor editor = mySharedPreferences.edit();
                                                editor.putString("user_name",user.getUser_name());
                                                editor.putInt("user_id",user.getUser_id());
                                                editor.putString("user_photo",user.getUser_photo());
                                                editor.putString("user_friends",user.getUser_friends());
                                                editor.putString("user_psd",user.getUser_psd());
                                                editor.apply();
                                                mysharedPreferences = getSharedPreferences("message_photo",0);
                                                SharedPreferences.Editor ed = mysharedPreferences.edit();
                                                    if(mysharedPreferences.getString(user.getUser_photo(),"null").equals("null")){
                                                        OkHttpUtils//
                                                                .get()
                                                                .url(user.getUser_photo())
                                                                .build()
                                                                .execute(new FileCallBack(Environment.getExternalStorageDirectory().getAbsolutePath(), "/LockDiary/User_Photo/"+user.getUser_id()+".png")//
                                                                {
                                                                    private String filepath;

                                                                    @Override
                                                                    public void onError(Call call, Exception e, int id) {
                                                                        Log.e("load_erro", "onError :" + e.getMessage());
                                                                    }

                                                                    @Override
                                                                    public void onResponse(File response, int id) {
                                                                        filepath =  response.getAbsolutePath()+"";
                                                                        Log.e("Filepath", "onResponse :" + response.getAbsolutePath());
                                                                    }
                                                                });
                                                        ed.putString(user.getUser_photo(),storage+"/"+user.getUser_id()+".png");
                                                        ed.apply();
                                                    }else{
                                                        ed.putString(user.getUser_photo(),storage+"/"+user.getUser_id()+".png");
                                                        ed.apply();
                                                    }

                                            }else if(erro_code==1){
                                                XToast.error(LoginActivity.this,"用户名无效").show();
                                            }else if(erro_code==2){
                                                XToast.error(LoginActivity.this,"密码错误").show();
                                                ed_password.setText("");
                                            }

                                            Intent it = new Intent();
                                            it.setClass(LoginActivity.this, MainActivity.class);
                                            startActivity(it, ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this).toBundle());
                                        }
                                    });
                        }
                    }else{
                        XToast.error(LoginActivity.this,"请勿重复登录").show();
                    }
                }catch (Exception e){
                    Log.e("EDITTEXT",e.toString());
                }
            }
        });

    }

    private boolean verify(String username, String password) {
        PassWord pw = new PassWord();
        if(pw.isValid(password).equals("true")&&pw.isValid(username).equals("true")){
            return true;
        }else if(!pw.isValid(password).equals("true")&&pw.isValid(username).equals("true")){
            ed_password.setText("");
            XToast.error(LoginActivity.this,pw.isValid(password)).show();
            return false;
        }else if(pw.isValid(password).equals("true")&&!pw.isValid(username).equals("true")){
            ed_username.setText("");
            XToast.error(LoginActivity.this,pw.isValid(username)).show();
            return false;
        }else{
            ed_username.setText("");
            ed_password.setText("");
            XToast.error(LoginActivity.this,"不符合规则").show();
            return false;
        }
    }

    private void init_actionBar() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.login));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }
    private void init_layout(){
        ed_username = (MaterialEditText)findViewById(R.id.ed_username);
        ed_password = (MaterialEditText)findViewById(R.id.ed_password);
        btn_login = (Button)findViewById(R.id.btn_login);
    }
}