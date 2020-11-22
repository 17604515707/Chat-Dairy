package bgu.gaoxu.diary;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.transition.Explode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.just.agentweb.widget.AgentWebView;
import com.xuexiang.xqrcode.XQRCode;
import com.xuexiang.xui.widget.toast.XToast;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import bgu.gaoxu.diary.adapter.DiaryAdapter;
import bgu.gaoxu.diary.entity.Diary;
import bgu.gaoxu.diary.entity.DiaryJsonBean;
import bgu.gaoxu.diary.entity.Message_list;
import bgu.gaoxu.diary.entity.MessgaeJsonRoot;
import bgu.gaoxu.diary.fragment.DiaryFragment;
import bgu.gaoxu.diary.fragment.IndexFragment;
import bgu.gaoxu.diary.fragment.MessageFragment;
import bgu.gaoxu.diary.fragment.MyselfFragment;
import bgu.gaoxu.diary.utils.GsonUtils;
import bgu.gaoxu.diary.utils.LoginState;
import bgu.gaoxu.diary.utils.MainMenu;
import okhttp3.Call;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int REQUEST_CODE = 111;
    private ImageButton btn_index, btn_diary, btn_message, btn_myself, btn_menu;
    private TextView tv_index, tv_diary, tv_message, tv_myself, tv_title;
    private ArrayList<Fragment> fragmentArrayList; //Fragment管理器
    private IndexFragment indexFragment;
    private DiaryFragment diaryFragment;
    private MessageFragment messageFragment;
    private MyselfFragment myselfFragment;
    String storage = Environment.getExternalStorageDirectory().getPath() + "/LockDiary/User_Photo";
    private AgentWebView anWebView;
    private String result = "";
    private List<Diary> diarieslist;
    private RecyclerView rv;
    private DiaryAdapter diaryAdapter;
    private Handler mHandler;
    private long mExitTime;
    private ArrayList<Message_list> msglist = new ArrayList<Message_list>();
    private SharedPreferences mysharedPreferences;
    private int index_Fragment = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPermissions();
        this.inin_rescoreD();//初始化存储图片文件夹
        this.init_actionBar();
        this.setUI();
        this.init_view();//初始化组件
        this.initFragment();
        this.setViewListener();
        this.init_RvMessage();//初始化Message信息，并检查系统文件夹有没有加载好的文件
        this.init_RvListData();

        //sendData_indexFragment();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //处理二维码扫描结果
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            //处理扫描结果（在界面上显示）
            handleScanResult(data);
        }
    }

    /**
     * 处理二维码扫描结果
     *
     * @param data
     */
    private void handleScanResult(Intent data) {
        if (data != null) {
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                if (bundle.getInt(XQRCode.RESULT_TYPE) == XQRCode.RESULT_SUCCESS) {
                    String result = bundle.getString(XQRCode.RESULT_DATA);
                    LoginState loginState = new LoginState();
                    int user_id = loginState.verify(MainActivity.this);
                    //Toast.makeText(MainActivity.this, "解析结果:" + result, Toast.LENGTH_LONG).show();
                    if (user_id != -1) {
                        try{
                            OkHttpUtils.post().url(result + "&followed_userid="+user_id).build().execute(new StringCallback() {
                                @Override
                                public void onError(Call call, Exception e, int id) {

                                }

                                @Override
                                public void onResponse(String response, int id) {
                                    if (response.equals("SUCCESS")) {
                                        XToast.success(MainActivity.this, "添加成功").show();
                                    } else if(response.equals("FOLLOWED")){
                                        XToast.info(MainActivity.this, "已经添加过了哟").show();
                                    }else{
                                        XToast.error(MainActivity.this, "添加失败").show();
                                    }
                                }
                            });
                        }catch (Exception e){
                            XToast.error(MainActivity.this, "添加失败,二维码无效").show();
                        }

                    } else {
                        XToast.info(MainActivity.this, "请先登录").show();
                    }
                } else if (bundle.getInt(XQRCode.RESULT_TYPE) == XQRCode.RESULT_FAILED) {
                    XToast.error(MainActivity.this, "添加失败").show();
                }
            }
        }
    }

    private void inin_rescoreD() {
        File dirFile = new File(storage);
        Log.d("dirFile", "" + dirFile);
        if (!dirFile.exists()) {
            boolean mkdirs = dirFile.mkdirs();
            if (!mkdirs) {
                Log.e("TAG", "文件夹创建失败");
            } else {
                Log.e("TAG", "文件夹创建成功");
            }
        }
    }

    private void init_RvMessage() {
        LoginState loginState = new LoginState();
        int user_id = loginState.verify(MainActivity.this);
        if (user_id != -1) {
            OkHttpUtils
                    .post()
                    .url("http://www.ga666666.club:8080/mydiary/getNewMessage.do")
                    .addParams("userid", user_id + "")
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {

                        }

                        @Override
                        public void onResponse(String response, int id) {

                            try {
                                MessgaeJsonRoot messgaeJsonRoot = GsonUtils.fromJson(response, MessgaeJsonRoot.class);
                                msglist = messgaeJsonRoot.getMessage_list();
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("result", (Serializable) msglist);
                                messageFragment.setArguments(bundle);
                                mysharedPreferences = getSharedPreferences("message_photo", 0);
                                SharedPreferences.Editor editor = mysharedPreferences.edit();
                                for (Message_list msg : msglist) {
                                    if (mysharedPreferences.getString(msg.getSend_user_photo(), "null").equals("null")) {
                                        OkHttpUtils//
                                                .get()
                                                .url(msg.getSend_user_photo())
                                                .build()
                                                .execute(new FileCallBack(Environment.getExternalStorageDirectory().getAbsolutePath(), "/LockDiary/User_Photo/" + msg.getSend_user_id() + ".png")//
                                                {
                                                    private String filepath;

                                                    @Override
                                                    public void onError(Call call, Exception e, int id) {
                                                        Log.e("load_erro", "onError :" + e.getMessage());
                                                    }

                                                    @Override
                                                    public void onResponse(File response, int id) {
                                                        filepath = response.getAbsolutePath() + "";
                                                        Log.e("Filepath", "onResponse :" + response.getAbsolutePath());
                                                    }
                                                });
                                        editor.putString(msg.getSend_user_photo(), storage + "/" + msg.getSend_user_id() + ".png");
                                        editor.apply();
                                    } else {

                                    }
                                }

                            } catch (Exception e) {
                                init_LoginData();
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("result", (Serializable) msglist);
                                messageFragment.setArguments(bundle);
                                Log.e("Erro", e + "");
                            }
                        }
                    });
        }
    }

    private void init_LoginData() {
        msglist.clear();
        Message_list message_list = new Message_list();
        message_list.setSend_user_photo("https://gitee.com/gao666666/images/raw/master/image/20201110201430.png");
        message_list.setSend_user_id(1);
        message_list.setMessage("还没有登录哦~");
        message_list.setSend_user_name("系统通知");
        message_list.setMsg_id(0);
        msglist.add(message_list);
    }

    private void sendData_indexFragment() {
        Bundle bundle = getIntent().getExtras();
        indexFragment.setArguments(bundle);
    }

    private void init_RvListData() {
        Intent its = getIntent();
        Bundle bu = its.getExtras();
        diaryFragment.setArguments(bu);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Object mHelperUtils;
                Toast.makeText(this, "再按一次退出APP", Toast.LENGTH_SHORT).show();
                //System.currentTimeMillis()系统当前时间
                mExitTime = System.currentTimeMillis();
            } else {
                CookieSyncManager.createInstance(this.getApplicationContext());
                CookieManager cookieManager = CookieManager.getInstance();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    cookieManager.removeSessionCookies(null);
                    cookieManager.removeAllCookie();
                    cookieManager.flush();
                } else {
                    cookieManager.removeSessionCookies(null);
                    cookieManager.removeAllCookie();
                    CookieSyncManager.getInstance().sync();
                }
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    private void init_actionBar() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.bottom_bgcolor));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    private void setUI() {
        setContentView(R.layout.activity_main);
        ActionBar action_bar = getSupportActionBar();
        if (action_bar != null)
            action_bar.hide();/*隐藏activity头*/
    }

    private void setViewListener() {
        btn_index.setOnClickListener(this);
        btn_diary.setOnClickListener(this);
        btn_message.setOnClickListener(this);
        btn_myself.setOnClickListener(this);

        tv_index.setOnClickListener(this);
        tv_diary.setOnClickListener(this);
        tv_message.setOnClickListener(this);
        tv_myself.setOnClickListener(this);

        btn_menu.setOnClickListener(this);

    }

    private void initFragment() {
        fragmentArrayList = new ArrayList<>();
        indexFragment = new IndexFragment();
        diaryFragment = new DiaryFragment();
        messageFragment = new MessageFragment();
        myselfFragment = new MyselfFragment();
        fragmentArrayList.add(indexFragment);
        fragmentArrayList.add(diaryFragment);
        fragmentArrayList.add(messageFragment);
        fragmentArrayList.add(myselfFragment);
        getSupportFragmentManager()
                .beginTransaction().add(R.id.id_content, fragmentArrayList.get(0))   // 此处的R.id.id_content是要盛放fragment的父容器
                .commit();
        setImgBtnPressIcon(0);

    }

    private void init_view() {
        //View view = getLayoutInflater().inflate(R.layout.bottom,null);
        btn_index = (ImageButton) findViewById(R.id.myhome);
        btn_diary = (ImageButton) findViewById(R.id.rv_mydiarylist);
        btn_message = (ImageButton) findViewById(R.id.mymessage);
        btn_myself = (ImageButton) findViewById(R.id.myself);
        tv_index = (TextView) findViewById(R.id.tv_myhome);
        tv_diary = (TextView) findViewById(R.id.tv_dairy);
        tv_message = (TextView) findViewById(R.id.tv_message);
        tv_myself = (TextView) findViewById(R.id.tv_myself);
        tv_title = (TextView) findViewById(R.id.tv_title);

        btn_menu = (ImageButton) findViewById(R.id.btn_menu);
    }

    /**
     * 高亮显示选择的按钮图标和图标下方文字颜色
     *
     * @param fragmentIndex
     */
    private void setImgBtnPressIcon(int fragmentIndex) {

        //先重置所有图标，再高亮选择的图标
        btn_diary.setImageResource(R.drawable.diary);
        btn_index.setImageResource(R.drawable.home);
        btn_message.setImageResource(R.drawable.message);
        btn_myself.setImageResource(R.drawable.myself);

        //先重置所有图标下面的文字，再高亮选择的图标下面的文字
        tv_index.setTextColor(getResources().getColor(R.color.black));
        tv_diary.setTextColor(getResources().getColor(R.color.black));
        tv_message.setTextColor(getResources().getColor(R.color.black));
        tv_myself.setTextColor(getResources().getColor(R.color.black));

        switch (fragmentIndex) {
            case 0:
                btn_index.setImageResource(R.drawable.home_press);
                tv_index.setTextColor(getResources().getColor(R.color.colorTvPress));
                setMenuMessage("主页");
                break;
            case 1:
                btn_diary.setImageResource(R.drawable.diary_press);
                tv_diary.setTextColor(getResources().getColor(R.color.colorTvPress));
                setMenuMessage("日记");
                break;
            case 2:
                btn_message.setImageResource(R.drawable.message_press);
                tv_message.setTextColor(getResources().getColor(R.color.colorTvPress));
                setMenuMessage("消息");
                break;
            case 3:
                btn_myself.setImageResource(R.drawable.myself_press);
                tv_myself.setTextColor(getResources().getColor(R.color.colorTvPress));
                setMenuMessage("我");
                break;
        }
    }

    public void setMenuMessage(String s) {
        ((TextView) findViewById(R.id.tv_title)).setText(s);
    }

    /**
     * 改变id_content里面的Fragment
     *
     * @param fragmentIndex Fragment索引值
     */
    private void changeFragment(int fragmentIndex) {
        if (fragmentIndex > index_Fragment) {
            getSupportFragmentManager()
                    .beginTransaction().setCustomAnimations(R.anim.slide_right_in, R.anim.slide_left_out)
                    .replace(R.id.id_content, fragmentArrayList.get(fragmentIndex))
                    .commit();
        } else if (fragmentIndex < index_Fragment) {
            getSupportFragmentManager()
                    .beginTransaction().setCustomAnimations(R.anim.slide_left_in, R.anim.slide_right_out)
                    .replace(R.id.id_content, fragmentArrayList.get(fragmentIndex))
                    .commit();
        } else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.id_content, fragmentArrayList.get(fragmentIndex))
                    .commit();
        }
        index_Fragment = fragmentIndex;

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.myhome:
            case R.id.tv_myhome:
                changeFragment(0);
                setImgBtnPressIcon(0);
                break;
            case R.id.rv_mydiarylist:
            case R.id.tv_dairy:
                changeFragment(1);
                setImgBtnPressIcon(1);
                break;
            case R.id.mymessage:
            case R.id.tv_message:
                changeFragment(2);
                setImgBtnPressIcon(2);
                break;
            case R.id.myself:
            case R.id.tv_myself:
                changeFragment(3);
                setImgBtnPressIcon(3);
                break;
            case R.id.btn_menu:
                new MainMenu(this, view).show(); //弹出菜单栏
                break;
        }
    }

    private String streamToStr(InputStream inputStream) throws IOException {
        String result = "";
        InputStreamReader isr = new InputStreamReader(inputStream);
        BufferedReader br = new BufferedReader(isr);
        String str = "";
        while ((str = br.readLine()) != null) {
            result += str + "\n";
        }
        return result;
    }

    private void requestPermissions() {
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                int permission = ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (permission != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.ACCESS_COARSE_LOCATION,
                                    Manifest.permission.LOCATION_HARDWARE, Manifest.permission.READ_PHONE_STATE,
                                    Manifest.permission.WRITE_SETTINGS, Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_CONTACTS}, 0x0010);
                }

                if (permission != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION}, 0x0010);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}