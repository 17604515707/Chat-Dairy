package bgu.gaoxu.diary;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import bgu.gaoxu.diary.activity.FingerprintActivity;
import bgu.gaoxu.diary.entity.Diary;
import bgu.gaoxu.diary.entity.DiaryJsonBean;
import bgu.gaoxu.diary.utils.GsonUtils;

/**
 * 开屏页
 *
 */
public class SplashActivity extends Activity {

    private static final int sleepTime = 2000;
    private Handler mHandler;
    String result = "";
    private List<Diary> diarieslist;
    @Override
    protected void onCreate(Bundle arg0) {

        //透明状态栏
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //透明导航栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        final View view = View.inflate(this, R.layout.activity_splash, null);
        setContentView(view);
        super.onCreate(arg0);
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
                        try {
                            Thread.sleep(300);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        //拿到数据,完成主界面更新
                        diarieslist = (List<Diary>) msg.obj;
                        Intent it = new Intent();
                        it.setClass(SplashActivity.this,FingerprintActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("result", (Serializable) diarieslist);
                        it.putExtras(bundle);
                        startActivity(it);
                        finish();
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        ExecutorService pool = Executors.newCachedThreadPool();
        pool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String path = "http://www.ga666666.club:8080/mydiary/getDiaries.do";
                    //String path = "https://api.oioweb.cn/api/wyypl.php";
                    URL url = new URL(path);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.connect();
                    int responcode = conn.getResponseCode();
                    if (responcode == HttpURLConnection.HTTP_OK) {
                        InputStream inputStream = conn.getInputStream();
                        result = streamToStr(inputStream);
                        //Log.e("服务器结果：", result);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (!result.equals("")) {
                    DiaryJsonBean diaryJsonBean = GsonUtils.fromJson(result, DiaryJsonBean.class);
                    List<Diary> servletlist = diaryJsonBean.getDiary();
                    //将数据传递给主线程
                    mHandler.sendEmptyMessage(0);
                    //需要数据传递，用下面方法；
                    Message msg = new Message();
                    msg.obj = servletlist;//可以是基本类型，可以是对象，可以是List、map等；
                    mHandler.sendMessage(msg);
                }
            }
        });
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
}