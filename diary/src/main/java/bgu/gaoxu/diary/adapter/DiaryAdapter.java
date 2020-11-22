package bgu.gaoxu.diary.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xuexiang.xui.widget.button.shinebutton.ShineButton;

import java.util.List;

import bgu.gaoxu.diary.R;
import bgu.gaoxu.diary.entity.Diary;


/**
 * Created by GA666666 on 2020/11/3 19:05
 */
public class DiaryAdapter extends RecyclerView.Adapter<DiaryAdapter.ItemHoder> {
    private Context context;
    private List<Diary> diaryList;
    private IMyClickListener myClickListener;//引入接口对象

    public interface IMyClickListener {
        void myOnclick(int postion);

        void onItemLongClick(View view, int position);
    }

    public DiaryAdapter(Context context, List<Diary> diaryList) {
        this.context = context;
        this.diaryList = diaryList;
    }

    public void setMyClickListener(IMyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    @NonNull
    @Override
    public ItemHoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.diary_item, parent, false);
        return new ItemHoder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHoder holder, int position) {
        Diary diary = diaryList.get(position);
        holder.rv_title.setText(diary.getDiary_title());
        String context = diary.getDiary_context() + "...";
        if (diary.getDiary_context().length() > 20) {
            context = diary.getDiary_context().substring(0, 20) + "...".replaceAll("\r", "6");
        }
        String time = "";
        if (diary.getDiary_time().length() > 16) {
            time = diary.getDiary_time().substring(5, 16);
        } else {
            time = diary.getDiary_time();
        }

        holder.rv_context.setText(context);
        holder.rv_time.setText(time);
        double random = Math.random();
        if (random > 0.5) {
            holder.shine_button.setChecked(true);
            holder.shine_star.setChecked(false);
        } else {
            holder.shine_button.setChecked(false);
            holder.shine_star.setChecked(true);
        }
        holder.rv_context.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myClickListener.myOnclick(position);
            }
        });
        holder.rv_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myClickListener.myOnclick(position);
            }
        });
        holder.rv_title.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                myClickListener.onItemLongClick(view, position);
                return true;
            }
        });

//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialog(diary.getDiary_context());
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return diaryList.size();
    }

    static class ItemHoder extends RecyclerView.ViewHolder {
        private TextView rv_title, rv_context, rv_time;
        private ShineButton shine_button, shine_star;

        public ItemHoder(@NonNull View itemView) {
            super(itemView);
            this.rv_title = itemView.findViewById(R.id.rv_title);
            this.rv_context = itemView.findViewById(R.id.rv_context);
            this.rv_time = itemView.findViewById(R.id.rv_time);
            this.shine_button = itemView.findViewById(R.id.shine_button);
            this.shine_star = itemView.findViewById(R.id.shine_star);
        }
    }

    public void dialog(String str) {
        AlertDialog.Builder alertdialogbuilder = new AlertDialog.Builder(context);
        alertdialogbuilder.setMessage(str);
        alertdialogbuilder.setPositiveButton("确定", null);
        alertdialogbuilder.setNeutralButton("取消", null);
        final AlertDialog alertdialog1 = alertdialogbuilder.create();
        alertdialog1.show();
    }
}
