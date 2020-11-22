package bgu.gaoxu.diary.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.xuexiang.xqrcode.XQRCode;
import com.xuexiang.xui.widget.imageview.RadiusImageView;

import java.io.File;

import bgu.gaoxu.diary.R;

public class CodeCardActivity extends AppCompatActivity {
    SharedPreferences mySharedPreferences,sharedPreferences;
    private RadiusImageView code_img_user;
    private TextView code_username;
    private ImageView xcode_img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init_actionBar();
        setContentView(R.layout.activity_code_card);
        mySharedPreferences = getSharedPreferences("user_info", MODE_PRIVATE);
        sharedPreferences = getSharedPreferences("message_photo", 0);
        init_layout();
        int user_id = mySharedPreferences.getInt("user_id", -1);
        String user_name = mySharedPreferences.getString("user_name", "null");
        String imgpath = mySharedPreferences.getString("user_photo", "null");
        String imagePath = sharedPreferences.getString(imgpath, "null");
        Bitmap response;
        File dirFile = new File(imagePath);
        if (!dirFile.exists()) {
            response = BitmapFactory.decodeResource(getResources(), R.drawable.head_image);
        } else {
            response = BitmapFactory.decodeFile(imagePath);
        }
        code_img_user.setImageBitmap(response);
        code_username.setText(user_name);
        String context = "http://www.ga666666.club:8080/mydiary/addFollow.do?userid="+user_id;
        Bitmap result = XQRCode.createQRCodeWithLogo(context, 700, 700, response);
        xcode_img.setImageBitmap(result);
    }

    private void init_layout() {
        code_img_user = findViewById(R.id.code_img_user);
        code_username = findViewById(R.id.code_username);
        xcode_img = findViewById(R.id.xcode_img);
    }
    private void init_actionBar() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.grays));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }
}