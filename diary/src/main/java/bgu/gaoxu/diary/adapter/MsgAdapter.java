package bgu.gaoxu.diary.adapter;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.xuexiang.xui.widget.imageview.RadiusImageView;

import java.io.File;
import java.util.List;

import bgu.gaoxu.diary.R;
import bgu.gaoxu.diary.entity.Message_list;
import bgu.gaoxu.diary.utils.LoginState;

public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.ViewHolder> {
    private Context context;
    private List<Message_list> mMsgList;
    LoginState loginState = new LoginState();
    SharedPreferences sharedPreferences;
    SharedPreferences mySharedPreferences;
    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout leftLayout;
        LinearLayout rightLayout;
        TextView leftMsg;
        TextView rihgtMsg;
        RadiusImageView myself_img_user, send_img_user;
        View view;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            leftLayout = (LinearLayout) view.findViewById(R.id.left_layout);
            rightLayout = (LinearLayout) view.findViewById(R.id.right_layout);
            leftMsg = (TextView) view.findViewById(R.id.left_msg);
            rihgtMsg = (TextView) view.findViewById(R.id.right_msg);
            myself_img_user = view.findViewById(R.id.myself_img_user);
            send_img_user = view.findViewById(R.id.send_img_user);
        }
    }

    public MsgAdapter(Context context,List<Message_list> msgList) {
        this.context = context;
        mMsgList = msgList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        sharedPreferences = context.getSharedPreferences("message_photo", 0);
        mySharedPreferences = context.getSharedPreferences("user_info", 0);
        int userid = loginState.verify(holder.view.getContext());
        Message_list msg = mMsgList.get(position);
        if (msg.getSend_user_id() != userid) {
            Bitmap response;

            SharedPreferences.Editor editor = sharedPreferences.edit();
            String imagePath = sharedPreferences.getString(msg.getSend_user_photo(), "null");
            File dirFile = new File(imagePath);
            if (!dirFile.exists()) {
                editor.remove(msg.getSend_user_photo());
                editor.apply();
                response = BitmapFactory.decodeResource(context.getResources(), R.drawable.head_image);
            } else {
                response = BitmapFactory.decodeFile(imagePath);
            }
            holder.send_img_user.setImageBitmap(response);
            holder.leftLayout.setVisibility(View.VISIBLE);
            holder.rightLayout.setVisibility(View.GONE);
            holder.leftMsg.setText(paiban(msg.getMessage()));
        } else if (msg.getSend_user_id() == userid) {

            String imgpath = mySharedPreferences.getString("user_photo","null");
            Bitmap response = null;
            if (!sharedPreferences.getString(imgpath, "null").equals("null")){
                String imagePath = sharedPreferences.getString(imgpath, "null");
                File dirFile = new File(imagePath);
                if (!dirFile.exists()) {
                    response = BitmapFactory.decodeResource(context.getResources(), R.drawable.head_image);
                }else{
                    response = BitmapFactory.decodeFile(imagePath);
                }
            }
            holder.myself_img_user.setImageBitmap(response);
            holder.rightLayout.setVisibility(View.VISIBLE);
            holder.leftLayout.setVisibility(View.GONE);
            holder.rihgtMsg.setText(paiban(msg.getMessage()));
        }
    }
    public String paiban(String s) {
        int count = 16;
        StringBuilder  s1 = new StringBuilder(s);
        while (s1.length()>16) {
            s1.insert(count, "\n");
            count+=(17);
            if (count>s.length()) {
                break;
            }
        }
        s = s1.toString();
        return s;
    }

    @Override
    public int getItemCount() {
        return mMsgList.size();
    }
}