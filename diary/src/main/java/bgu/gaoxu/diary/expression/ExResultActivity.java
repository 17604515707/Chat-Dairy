package bgu.gaoxu.diary.expression;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bgu.gaoxu.diary.R;
import bgu.gaoxu.diary.entity.Emotion;
import bgu.gaoxu.diary.entity.JsonRootBean;
import bgu.gaoxu.diary.entity.Smile;
import bgu.gaoxu.diary.utils.GsonUtils;
import okhttp3.Call;
import okhttp3.Request;

public class ExResultActivity extends AppCompatActivity {
    com.github.mikephil.charting.charts.RadarChart myradarchart;
    private String url;
    private static final String TAG = "ResultActivity";
    private Button btn_return,btn_detect;
    private ImageView imageView,cartoon;
    private TextView resultView;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init_actionBar();
        setContentView(R.layout.expressresult);
        resultView = (TextView)findViewById(R.id.resultView);
        imageView = (ImageView)findViewById(R.id.image);
        myradarchart = findViewById(R.id.zhizhu);

        progressBar = (ProgressBar)findViewById(R.id.progress);
        Intent it = getIntent();
        String imageurl = it.getStringExtra("image_url");
        Bundle bundle = new Bundle();
        bundle.putInt("imageId",1);
        it.putExtras(bundle);
        setResult(0x007,it);

        url = imageurl;
        try {
            resultView.setText(imageurl);
            if (ContextCompat.checkSelfPermission(ExResultActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(ExResultActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
            }
            displayImage(imageurl);
        }catch (Exception e){
            Log.e(TAG, "发送异常:" + e);
        }
        btn_return = (Button)findViewById(R.id.btn_return);
        btn_detect = (Button)findViewById(R.id.btn_detect);
        btn_return.setOnClickListener(l);
        btn_detect.setOnClickListener(l);

    }

    private void init_actionBar() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(ContextCompat.getColor(ExResultActivity.this, R.color.login));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    View.OnClickListener l = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btn_return:
                    finish();
                    break;
                case R.id.btn_detect:
                    Toast.makeText(ExResultActivity.this,"正在分析请稍等哦~",Toast.LENGTH_LONG).show();
                    multiFileUpload();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 显示图片
     * @param imagePath
     * @throws Exception
     */
    private void displayImage(String imagePath) throws Exception {
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            //缩放
            Bitmap newbitmap = zoomImg(bitmap,width/3,height/3);
            imageView.setImageBitmap(newbitmap);
            //Toast.makeText(this, imagePath, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "获取图片失败", Toast.LENGTH_SHORT).show();
        }
    }
    public Bitmap zoomImg(Bitmap bm, int newWidth, int newHeight) {
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbm;
    }
    /**
     *使用okhttputils多文件或单文件上传
     */
    public void multiFileUpload()
    {
        //openAlbum();
        String mBaseUrl ="http://129.28.159.51:8080/NewFace/UploadServlet";
        File file = new File(url);
        //File file = new File(Environment.getExternalStorageDirectory(), "/DCIM/Screenshots/SS.jpg");
        //File file2 = new File(Environment.getExternalStorageDirectory(), "test1#.txt");
        if (!file.exists())
        {
            Toast.makeText(ExResultActivity.this, "文件不存在，请修改文件路径", Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String, String> params = new HashMap<>();
//        params.put("username", "张鸿洋");
//        params.put("password", "123");

        String url = mBaseUrl;
        OkHttpUtils.post()//
                .addFile("mFile", "messenger_01.png", file)//
                //.addFile("mFile", "test1.txt", file2)//
                .url(url)
                .params(params)//
                .build()//
                .connTimeOut(20000)
                .readTimeOut(20000)
                .writeTimeOut(20000)
                .execute(new MyStringCallback());
    }
    public class MyStringCallback extends StringCallback
    {
        @Override
        public void onBefore(Request request, int id)
        {
            setTitle("loading...");
        }

        @Override
        public void onAfter(int id)
        {
            setTitle("Sample-okHttp");
        }

        @Override
        public void onError(Call call, Exception e, int id)
        {
            e.printStackTrace();
            resultView.setText("onError:" + e.getMessage());
        }

        @Override
        public void onResponse(String response, int id)
        {
            String result = "";
            Log.e(TAG, "onResponse：complete");
            /**
             * 解析json
             */
            try {
                JsonRootBean rootBean = GsonUtils.fromJson(response, JsonRootBean.class);
                Emotion emotion = rootBean.getFaces().get(0).getAttributes().getEmotion();
                Smile smile = rootBean.getFaces().get(0).getAttributes().getSmile();
                double Anger = emotion.getAnger();
                double Disgust = emotion.getDisgust();
                double Fear = emotion.getFear();
                double Happiness = emotion.getHappiness();
                double Neutral = emotion.getNeutral();
                double Sadness = emotion.getSadness();
                double Surprise = emotion.getSurprise();

                List<RadarEntry> list = new ArrayList<RadarEntry>();
                RadarEntry da1 = new RadarEntry((float) Anger);
                RadarEntry da2 = new RadarEntry((float) Disgust);
                RadarEntry da3 = new RadarEntry((float) Fear);
                RadarEntry da4 = new RadarEntry((float) Happiness);
                RadarEntry da5 = new RadarEntry((float) Neutral);
                RadarEntry da6 = new RadarEntry((float) Sadness);
                RadarEntry da7 = new RadarEntry((float) Surprise);

                list.add(da1);
                list.add(da2);
                list.add(da3);
                list.add(da4);
                list.add(da5);
                list.add(da6);
                list.add(da7);
                RadarData radarData = new RadarData();

                RadarDataSet radarDataSet = new RadarDataSet(list,"Happiness");
                radarDataSet.setFillColor(Color.BLUE);
                //禁用标签
                radarDataSet.setDrawValues(false);
                //设置填充透明度
                radarDataSet.setFillAlpha(40);
                //设置启用填充
                radarDataSet.setDrawFilled(true);
                radarData.addDataSet(radarDataSet);
                myradarchart.setVisibility(View.VISIBLE);
                myradarchart.setData(radarData);
                result = "生气指数" + Anger + "\n反感指数:" + Disgust + "\n恐惧:" + Fear + "\n高兴:" + Happiness + "\n平静:" + Neutral + "\n伤心:" + Sadness + "\n" +
                        "惊讶:" + Surprise;
            }catch (Exception e){
                result = "哎呀没有找到人脸哦~";
                result = e+"";
            }
            resultView.setText("\n" + result);

            switch (id)
            {
                case 100:
                    Toast.makeText(ExResultActivity.this, "http", Toast.LENGTH_SHORT).show();
                    break;
                case 101:
                    Toast.makeText(ExResultActivity.this, "https", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
        @Override
        public void inProgress(float progress, long total, int id)
        {
            Log.e(TAG, "inProgress:" + progress);
            progressBar.setProgress((int) (100 * progress));
        }

    }
}