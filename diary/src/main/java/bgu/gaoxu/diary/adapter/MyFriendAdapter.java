package bgu.gaoxu.diary.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xuexiang.xui.widget.imageview.RadiusImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import bgu.gaoxu.diary.R;
import bgu.gaoxu.diary.entity.Diary;
import bgu.gaoxu.diary.entity.Follow_list;

/**
 * Created by GA666666 on 2020/11/9 15:50
 */
public class MyFriendAdapter extends RecyclerView.Adapter<MyFriendAdapter.ItemHoder> {

    private ArrayList<Follow_list> mList;
    private Context mContext;
    private RecyclerView listView;
    SharedPreferences sharedPreferences;

    public MyFriendAdapter(Context context, ArrayList<Follow_list> list) {

        this.mContext = context;
        this.mList = list;
    }

    @NonNull
    @Override
    public ItemHoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.myself_item,parent,false);
        ItemHoder itemHoder = new ItemHoder(view);
        return itemHoder;
    }


    @Override
    public void onBindViewHolder(@NonNull ItemHoder holder, int position) {
        Follow_list follow_list = mList.get(position);
        holder.title.setText(follow_list.getFollow_user_name());

        Bitmap response;
        if (follow_list.getFollow_id()!=-1) {
            sharedPreferences = mContext.getSharedPreferences("message_photo", 0);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            String imagePath = sharedPreferences.getString(follow_list.getFollow_user_img(), "null");
            File dirFile = new File(imagePath);
            if (!dirFile.exists()) {
                editor.remove(follow_list.getFollow_user_img());
                editor.apply();
                response = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.head_image);
            } else {
                response = BitmapFactory.decodeFile(imagePath);

            }
        }else{
            response = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.system_img);
        }
        holder.radiusImageView.setImageBitmap(response);
        //自定义了照片来源
//        try {
//            URL url = new URL(follow_list.getFollow_user_img());
//            holder.radiusImageView.setImageBitmap(BitmapFactory.decodeStream(url.openStream()));
//        } catch (Exception e) {
//            holder.radiusImageView.setImageResource(R.drawable.myself);
//        }
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class ItemHoder extends RecyclerView.ViewHolder{
        TextView title;
        RadiusImageView radiusImageView;
        public ItemHoder(@NonNull View itemView) {
            super(itemView);
            radiusImageView = itemView.findViewById(R.id.myselt_list_pic);
            title = itemView.findViewById(R.id.myselt_list_title);
        }
    }
}


