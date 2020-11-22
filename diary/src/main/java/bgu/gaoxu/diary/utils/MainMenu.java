package bgu.gaoxu.diary.utils;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.xuexiang.xqrcode.XQRCode;

import bgu.gaoxu.diary.MainActivity;
import bgu.gaoxu.diary.R;
import bgu.gaoxu.diary.activity.AboutActivity;
import bgu.gaoxu.diary.activity.DiaryWriteActivity;
import bgu.gaoxu.diary.activity.LoginActivity;

public class MainMenu extends PopupMenu implements PopupMenu.OnMenuItemClickListener {

    private static final int REQUEST_CODE = 111;
    private MainActivity mainActivity;

    public MainMenu(Context context, View anchor) {

        super(context, anchor);
        //获取菜单填充器
        MenuInflater menuInflater = this.getMenuInflater();
        //设置菜单布局
        menuInflater.inflate(R.menu.main_menu, getMenu());
        //设置监听器
        setOnMenuItemClickListener(this);

        mainActivity = (MainActivity) context;
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {

        Intent it = new Intent();
        switch (menuItem.getItemId()) {
            case R.id.menu_item_set_diary:
                it.setClass(mainActivity, DiaryWriteActivity.class);
                mainActivity.startActivity(it, ActivityOptions.makeSceneTransitionAnimation(mainActivity).toBundle());
//                Toast.makeText(mainActivity, "写日记", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_item_get_friend:
                startScan();
//                Toast.makeText(mainActivity, "添加朋友", Toast.LENGTH_SHORT).show();
                break;

            case R.id.menu_item_help:
                it.setClass(mainActivity, AboutActivity.class);
                mainActivity.startActivity(it, ActivityOptions.makeSceneTransitionAnimation(mainActivity).toBundle());
//             Toast.makeText(mainActivity, "帮助与反馈", Toast.LENGTH_SHORT).show();
                break;

        }
        return false;
    }

    private void startScan() {
        XQRCode.startScan(mainActivity, REQUEST_CODE);
    }

}
