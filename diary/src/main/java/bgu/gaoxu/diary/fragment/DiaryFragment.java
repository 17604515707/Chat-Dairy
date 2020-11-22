package bgu.gaoxu.diary.fragment;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smart.refresh.header.MaterialHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xui.widget.popupwindow.popup.XUIPopup;
import com.xuexiang.xui.widget.toast.XToast;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import bgu.gaoxu.diary.R;
import bgu.gaoxu.diary.activity.DiaryReadAcyivity;
import bgu.gaoxu.diary.activity.MyDiaryList;
import bgu.gaoxu.diary.adapter.DiaryAdapter;
import bgu.gaoxu.diary.entity.Diary;
import bgu.gaoxu.diary.entity.DiaryJsonBean;
import bgu.gaoxu.diary.tts.TtsDemo;
import bgu.gaoxu.diary.utils.GsonUtils;
import okhttp3.Call;

/**
 * Created by GA666666 on 2020/11/7 22:06
 */
public class DiaryFragment extends Fragment {
    private View view;
    private XUIPopup mNormalPopup;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private Bundle bundle;
    SmartRefreshLayout mRefreshLayout;
    private MaterialHeader mMaterialHeader;
    private DiaryAdapter diaryAdapter;
    private RecyclerView rv;
    private List<Diary> list = new ArrayList<Diary>();

    public static DiaryFragment newInstance(String param1, String param2) {
        DiaryFragment fragment = new DiaryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_diary_list, container, false);
        init_Refresh();
        initData();
        initRecyclerView();
        Log.e("list", list.size() + "");
        return view;
    }

    private void init_Refresh() {
        mRefreshLayout = (SmartRefreshLayout) view.findViewById(R.id.diary_refreshLayout);
        mMaterialHeader = (MaterialHeader) mRefreshLayout.getRefreshHeader();
        mMaterialHeader.setShowBezierWave(true);
        mRefreshLayout.setEnableHeaderTranslationContent(true);
        mRefreshLayout.setHeaderHeight(100);
        //自动加载
        mRefreshLayout.autoRefresh();
    }

    private void initData() {
        bundle = getArguments();
        if (bundle != null) {
            list = (List<Diary>) bundle.getSerializable("result");
        }
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                OkHttpUtils.post().url("http://www.ga666666.club:8080/mydiary/getDiaries.do").build().execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }
                    @Override
                    public void onResponse(String response, int id) {
                        //list.clear();
                        DiaryJsonBean diaryJsonBean = GsonUtils.fromJson(response, DiaryJsonBean.class);
                        list = diaryJsonBean.getDiary();
                        initRecyclerView();
                    }
                });
                XToast.info(getActivity(), "刷新成功").show();
                refreshLayout.finishRefresh();
            }
        });
    }

    private void initRecyclerView() {
        //获取RecyclerView
        rv = (RecyclerView) view.findViewById(R.id.recyclerView);
        //创建adapter
        diaryAdapter = new DiaryAdapter(getActivity(), list);
        diaryAdapter.setMyClickListener(new DiaryAdapter.IMyClickListener() {
            @Override
            public void myOnclick(int postion) {
                Intent it = new Intent(getActivity(), DiaryReadAcyivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("result", (Serializable) list.get(postion));
                it.putExtras(bundle);
                startActivity(it, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
            }

            @Override
            public void onItemLongClick(View view, int position) {
                popMessage(list.get(position));
            }
        });
        Log.e("count", diaryAdapter.getItemCount() + "");

        rv.setAdapter(diaryAdapter);
        //设置item的分割线
        //rv.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        rv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
    }

    public void popMessage(Diary diary) {
        Intent it = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("diary", (Serializable) diary);
        it.setClass(getActivity(), TtsDemo.class);
        it.putExtras(bundle);
        new MaterialDialog.Builder(getContext())
                .title(diary.getDiary_title())
                .content("点击 “确认” 即可语音朗读")
                .positiveText("确认").onPositive((dialog, which) -> getActivity().startActivity(it))
                .negativeText("取消")
                .show();
    }
}