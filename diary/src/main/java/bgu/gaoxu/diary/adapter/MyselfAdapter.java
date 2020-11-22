package bgu.gaoxu.diary.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xuexiang.xui.widget.imageview.RadiusImageView;

import java.util.ArrayList;

import bgu.gaoxu.diary.R;
import bgu.gaoxu.diary.entity.MyselfList;

/**
 * Created by GA666666 on 2020/11/9 15:50
 */
public class MyselfAdapter extends BaseAdapter {

    private ArrayList<MyselfList> mList;
    private Context mContext;
    private ListView listView;

    public MyselfAdapter(Context context, ArrayList<MyselfList> list, ListView listView) {
        this.listView = listView;
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int i) {
        return mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        if(convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.myself_item,null);
            holder = new ViewHolder();
            holder.mTextView = (TextView)convertView.findViewById(R.id.myselt_list_title);
            holder.mPic = (com.xuexiang.xui.widget.imageview.RadiusImageView)convertView.findViewById(R.id.myselt_list_pic);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }

        holder.mTextView.setText(mList.get(position).getListtext());

        holder.mPic.setImageResource(mList.get(position).getListpic());

        return convertView;
    }

    static class ItemHoder extends RecyclerView.ViewHolder {
        private TextView rv_title, rv_context;

        public ItemHoder(@NonNull View itemView) {
            super(itemView);
            this.rv_title = itemView.findViewById(R.id.rv_title);
            this.rv_context = itemView.findViewById(R.id.rv_context);
        }
    }

    class ViewHolder{
        public TextView mTextView;
        public RadiusImageView mPic;
    }


}
