package bgu.gaoxu.diary.expression;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import bgu.gaoxu.diary.R;

public class ExpressionActivity extends AppCompatActivity {
    private ImageView imageview, cartoon;
    private Button openphoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init_actionBar();
        setContentView(R.layout.activity_expression);
        //imageview = (ImageView) findViewById(R.id.image1);
        cartoon = (ImageView) findViewById(R.id.cartoon);
        cartoon.setImageResource(R.drawable.ic_gee_me_016);
        openphoto = (Button) findViewById(R.id.opentphoto);
        openphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //动态申请获取访问 读写磁盘的权限
                if (ContextCompat.checkSelfPermission(ExpressionActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ExpressionActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
                } else {
                    //打开相册
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    //Intent.ACTION_GET_CONTENT = "android.intent.action.GET_CONTENT"
                    intent.setType("image/*");
                    //startActivityForResult(intent, 1004); // 打开相册
                    openAlbum();
                }
            }
        });
    }

    private void init_actionBar() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(ContextCompat.getColor(ExpressionActivity.this, R.color.login));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, 1004);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0x007:
                Bundle bundle = data.getExtras();
                int imageId = bundle.getInt("imageId");
                //Toast.makeText(MainActivity.this,"imageId"+imageId,Toast.LENGTH_LONG).show();
                if (imageId == 1) {
                    cartoon.setImageResource(R.drawable.ic_gee_me_021);
                } else if (imageId == 2) {
                    cartoon.setImageResource(R.drawable.ic_gee_me_016);
                } else {
                    cartoon.setImageResource(R.drawable.ic_gee_me_021);
                }
            case 1004:
                if (resultCode == RESULT_OK) { // 判断手机系统版本号
                    if (Build.VERSION.SDK_INT >= 19) {
                        // 4.4及以上系统使用这个方法处理图片
                        try {
                            handleImageOnKitKat(data);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else {
                        // 4.4以下系统使用这个方法处理图片
                        try {
                            handleImageBeforeKitKat(data);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                break;
            default:
                break;
        }
    }

    private void handleImageBeforeKitKat(Intent data) throws Exception {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        //displayImage(imagePath);
        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.gee_me_012);
        int width = (int) (bitmap.getWidth() / 0.1);
        int height = (int) (bitmap.getHeight() / 0.1);
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) throws Exception {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            // 如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];
                // 解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content: //downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // 如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        // 根据图片路径显示图片
        //Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(),R.drawable.ic_gee_me_012);
        //displayImage(imagePath);
        Intent it = new Intent(ExpressionActivity.this, ExResultActivity.class);
        it.putExtra("image_url", imagePath);
        startActivityForResult(it, 0x007);
    }


}
