package bgu.gaoxu.diary.utils;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by GA666666 on 2020/11/8 13:37
 */
public class HttpUtils {
    private Handler mHandler;
    private String result="";
    public  String getJsonFrom(String url) throws InterruptedException {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
                        //拿到数据,完成主界面更新
                        result = (String) msg.obj;
                }
            }
        };
        ExecutorService pool = Executors.newCachedThreadPool();
        pool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String path = url;
                    //String path = "https://api.oioweb.cn/api/wyypl.php";
                    URL url = new URL(path);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.connect();
                    int responcode = conn.getResponseCode();
                    if (responcode == HttpURLConnection.HTTP_OK) {
                        InputStream inputStream = conn.getInputStream();
                        result = streamToStr(inputStream);
                        Log.e("服务器结果：", result);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (!result.equals("")) {
                    //将数据传递给主线程
                    mHandler.sendEmptyMessage(0);
                    //需要数据传递，用下面方法；
                    Message msg = new Message();
                    msg.obj = result;//可以是基本类型，可以是对象，可以是List、map等；
                    mHandler.sendMessage(msg);
                }
            }
        });
        /**
         * !!!!!等一等子线程获取到结果
         */
        while(result.equals("")){
            Thread.sleep(10);
        }

        return result;
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
