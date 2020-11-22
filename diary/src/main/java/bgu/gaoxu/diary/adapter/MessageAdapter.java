package bgu.gaoxu.diary.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;
import java.util.ArrayList;

import bgu.gaoxu.diary.R;
import bgu.gaoxu.diary.entity.Message_list;
import okhttp3.Call;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ItemHoder> {
    final private String storage = Environment.getExternalStorageDirectory().getPath() + "/LockDiary/User_Photo/";
    private ArrayList<Message_list> messagelist;
    private Context context;
    private IMyClickList MyClickListener;
    SharedPreferences sharedPreferences;
    public interface IMyClickList {
        void OnClick(int position);
    }

    public void setMyClickListener(IMyClickList myClickListener) {
        this.MyClickListener = myClickListener;
    }

    public MessageAdapter(Context context, ArrayList<Message_list> messagelist) {
        this.context = context;
        this.messagelist = messagelist;
    }

    public ItemHoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(this.context).inflate(R.layout.message_item, parent, false);

//        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,185);//设置宽度和高度,此处将宽度设为最大，高度设为70
//        view.setLayoutParams(params);
        return new ItemHoder(view);

    }

    public void onBindViewHolder(@NonNull ItemHoder holder, final int position) {
        final Message_list message = messagelist.get(position);
        holder.title.setText(message.getSend_user_name());
        String ms = message.getMessage();
        if (ms.length()>15){
            ms = ms.substring(0,16)+"···";
        }
        holder.content.setText(ms);
        holder.msg_time.setText(message.getSend_time());
        holder.title.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MyClickListener.OnClick(position);
            }
        });

        Bitmap response;
        if (!message.getSend_user_name().equals("系统通知")) {
            sharedPreferences = context.getSharedPreferences("message_photo", 0);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            String imagePath = sharedPreferences.getString(message.getSend_user_photo(), "null");
            File dirFile = new File(imagePath);
            if (!dirFile.exists()) {
                editor.remove(message.getSend_user_photo());
                editor.apply();
                response = BitmapFactory.decodeResource(context.getResources(), R.drawable.head_image);
            } else {
                response = BitmapFactory.decodeFile(imagePath);

            }
        }else{
            response = BitmapFactory.decodeResource(context.getResources(), R.drawable.system_img);
        }
        holder.user_photo.setImageBitmap(response);
        holder.content.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MyClickListener.OnClick(position);
            }
        });
    }


    @Override
    public int getItemCount() {
        return messagelist.size();
    }
    static class ItemHoder extends RecyclerView.ViewHolder {
        TextView title;
        TextView content;
        TextView msg_time;
        ImageView user_photo;
        public ItemHoder(View view) {
            super(view);
            this.title = view.findViewById(R.id.messagename);
            this.content = view.findViewById(R.id.messagecontent);
            this.msg_time = view.findViewById(R.id.message_time);
            this.user_photo = view.findViewById(R.id.msg_userphoto);
        }
    }


}
