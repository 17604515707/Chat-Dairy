package bgu.gaoxu.diary.fragment;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xui.widget.imageview.RadiusImageView;
import com.xuexiang.xui.widget.toast.XToast;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

import bgu.gaoxu.diary.MainActivity;
import bgu.gaoxu.diary.R;
import bgu.gaoxu.diary.activity.CodeCardActivity;
import bgu.gaoxu.diary.activity.DiaryReadAcyivity;
import bgu.gaoxu.diary.activity.LoginActivity;
import bgu.gaoxu.diary.activity.MyDiaryList;
import bgu.gaoxu.diary.activity.MyFriendsActivity;
import bgu.gaoxu.diary.adapter.MyselfAdapter;
import bgu.gaoxu.diary.entity.Diary;
import bgu.gaoxu.diary.entity.MyselfList;
import bgu.gaoxu.diary.expression.ExpressionActivity;
import bgu.gaoxu.diary.tts.TtsDemo;
import bgu.gaoxu.diary.utils.LoginState;

/**
 * Created by GA666666 on 2020/11/7 22:08
 */
public class MyselfFragment extends Fragment {

    private static final int MODE_PRIVATE = 0;
    private String[] title = new String[]{"我的日记", "我的粉丝", "表情分析", "语音转文字", "登录/注销"};
    private int[] url_pic = new int[]{R.drawable.list_mydiary, R.drawable.list_myfriend, R.drawable.list_myvisit,
            R.drawable.list_mycartoon_1, R.drawable.list_mytts, R.drawable.list_myexit};
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    SharedPreferences mySharedPreferences, sharedPreferences;
    private ArrayList<MyselfList> myselfLists = new ArrayList<MyselfList>();
    private ListView listView;
    private MyselfAdapter myselfAdapter;
    private TextView tv_username,tv_diary_num,tv_follow_num;
    private RadiusImageView img_user;
    private Bitmap response;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private View view;

    public static MyselfFragment newInstance(String param1, String param2) {
        MyselfFragment fragment = new MyselfFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_myself, container, false);
        mySharedPreferences = getActivity().getSharedPreferences("user_info", MODE_PRIVATE);
        sharedPreferences = getActivity().getSharedPreferences("message_photo", 0);
        tv_username = view.findViewById(R.id.myself_username);
        String imgpath = mySharedPreferences.getString("user_photo", "null");
        img_user = view.findViewById(R.id.img_user);
        tv_diary_num  =view.findViewById(R.id.diary_num);
        tv_follow_num = view.findViewById(R.id.follow_num);
        tv_diary_num.setText("日记数量\n"+mySharedPreferences.getInt("diary_num",0));
        tv_follow_num.setText("粉丝\n"+mySharedPreferences.getInt("follow_num",0));
        if (!mySharedPreferences.getString("user_name", "null").equals("null")) {
            tv_username.setText(mySharedPreferences.getString("user_name", ""));
        }
        if (!sharedPreferences.getString(imgpath, "null").equals("null")) {
            String imagePath = sharedPreferences.getString(imgpath, "null");
            File dirFile = new File(imagePath);
            if (!dirFile.exists()) {
                response = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.head_image);
            } else {
                response = BitmapFactory.decodeFile(imagePath);
            }
            img_user.setImageBitmap(response);
        }
        img_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginState loginState = new LoginState();
                int userid = loginState.verify(getActivity());
                if (userid != -1) {
                    Intent it = new Intent();
                    it.setClass(getActivity(), CodeCardActivity.class);
                    getActivity().startActivity(it);
                } else {
                    XToast.info(getActivity(), "请先登录").show();
                }
            }
        });

        listView = view.findViewById(R.id.myselflist);
        myselfLists.clear();
        init_Data();
        myselfAdapter = new MyselfAdapter(getActivity(), myselfLists, listView);
        listView.setAdapter(myselfAdapter);
        init_onclick();
        return view;
    }

    private void init_onclick() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                XToast.success(getActivity(), title[i]).show();
                Intent it = new Intent();
                switch (i) {
                    case 0:
                        it.setClass(getActivity(), MyDiaryList.class);
                        startActivity(it);
                        break;
                    case 1:
                        it.setClass(getActivity(), MyFriendsActivity.class);
                        startActivity(it);
                        break;
                    case 2:
                        it.setClass(getActivity(), ExpressionActivity.class);
                        startActivity(it);
                        break;
                    case 3:
                        it.setClass(getActivity(), TtsDemo.class);
                        startActivity(it);
                        break;
                    case 4:
                        popMessage();
                        break;
                }
            }
        });
    }

    private void remove_userinfo() {
        SharedPreferences mySharedPreferences = getActivity().getSharedPreferences("user_info", MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.remove("user_name");
        editor.remove("user_id");
        editor.remove("user_photo");
        editor.remove("user_friends");
        editor.remove("user_psd");
        editor.apply();
        Intent it = new Intent();
        it.setClass(getActivity(), LoginActivity.class);
        startActivity(it);
        getActivity().finish();
    }

    public void popMessage() {

        LoginState loginState = new LoginState();
        int userid = loginState.verify(getActivity());
        if (userid != -1) {
            new MaterialDialog.Builder(getContext())
                    .title("注销用户")
                    .content("真的要退出吗？点击确认注销登录")
                    .positiveText("确认").onPositive((dialog, which) -> remove_userinfo())
                    .negativeText("取消")
                    .show();
        } else {
            Intent it = new Intent();
            it.setClass(getActivity(), LoginActivity.class);
            getActivity().startActivity(it, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
            getActivity().finish();
        }
    }

    private void init_Data() {

        int count = 0;
        for (String tit : title) {
            MyselfList myselfList = new MyselfList();
            myselfList.setListtext(tit);
            myselfList.setListpic(url_pic[count]);
            myselfLists.add(myselfList);
            count++;
        }
    }
}