package bgu.gaoxu.diary.utils;

import android.os.Environment;
import android.util.Log;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;

import okhttp3.Call;

/**
 * Created by GA666666 on 2020/11/10 20:49
 */
public class LoadImg {
    private String filepath;

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public String load(String url,String savepath) throws InterruptedException {
        String result="";
        OkHttpUtils//
                .get()
                .url(url)
                .build()
                .execute(new FileCallBack(Environment.getExternalStorageDirectory().getAbsolutePath(), "test.png")//
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
        while (result.equals("")) Thread.sleep(10);
        return result;
    }
}
