package bgu.gaoxu.diary.fragment;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.scwang.smart.refresh.footer.BallPulseFooter;
import com.scwang.smart.refresh.header.BezierRadarHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshHeader;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog;
import com.xuexiang.xui.widget.textview.MarqueeTextView;
import com.xuexiang.xui.widget.textview.marqueen.DisplayEntity;
import com.xuexiang.xui.widget.toast.XToast;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.Callback;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import bgu.gaoxu.diary.R;
import bgu.gaoxu.diary.activity.ChatActivity;
import bgu.gaoxu.diary.adapter.MessageAdapter;
import bgu.gaoxu.diary.entity.Message_list;
import bgu.gaoxu.diary.entity.MessgaeJsonRoot;
import bgu.gaoxu.diary.utils.GsonUtils;
import bgu.gaoxu.diary.utils.LoginState;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by GA666666 on 2020/11/7 22:08
 */
public class MessageFragment extends Fragment {


    private String mParam1;
    private String mParam2;
    SmartRefreshLayout mRefreshLayout;
    private Bundle bundle;
    private View view;
    private int user_id;
    private Handler handler = new Handler();
    private RecyclerView rv;
    private BezierRadarHeader mBezierRadarHeader;
    private MarqueeTextView mTvMarquee;
    private MessageAdapter messageAdapter;
    private ArrayList<Message_list> msglist = new ArrayList<Message_list>();
    private List<String> datas = new ArrayList<String>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_message, container, false);

        LoginState loginState = new LoginState();
        user_id = loginState.verify(getActivity());
        if (user_id != -1) {
            init_Data();
        } else {
            init_LoginData();
        }
        inin_marque();
        initRecyclerView();
        inin_Refresh();
        handler.postDelayed(runnable, 5000);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(runnable);
    }

    @Override
    public void onPause() {
        super.onPause();
        //handler.removeCallbacks(runnable);
    }

    @Override
    public void onStop() {
        super.onStop();
       // handler.removeCallbacks(runnable);
    }

    private Runnable runnable = new Runnable() {
        public void run() {
            try {
                if (user_id != -1) {
                    OkHttpUtils.post().url("http://www.ga666666.club:8080/mydiary/getNewMessage.do").addParams("userid", user_id + "").build()
                            .execute(new StringCallback() {
                                @Override
                                public void onError(Call call, Exception e, int id) {

                                }

                                @Override
                                public void onResponse(String response, int id) {
                                    try {
                                        Log.e("re", response);
                                        MessgaeJsonRoot messgaeJsonRoot = GsonUtils.fromJson(response, MessgaeJsonRoot.class);
                                        msglist = messgaeJsonRoot.getMessage_list();
                                    } catch (Exception e) {
                                        Log.e("re", e + "");
                                        msglist.clear();
                                        Message_list message_list = new Message_list();
                                        message_list.setSend_user_photo("https://gitee.com/gao666666/images/raw/master/image/20201110201430.png");
                                        message_list.setSend_user_id(1);
                                        message_list.setMessage("可能是还没有消息哦~");
                                        message_list.setSend_user_name("系统通知");
                                        message_list.setMsg_id(0);
                                        msglist.add(message_list);
                                    }
                                    //刷新
                                    initRecyclerView();
                                }
                            });
                } else {
                }
            } catch (Exception e) {

            }
            handler.postDelayed(this, 5000);//定时时间
        }
    };

    private void init_LoginData() {
        msglist.clear();
        Message_list message_list = new Message_list();
        message_list.setSend_user_photo("https://gitee.com/gao666666/images/raw/master/image/20201110201430.png");
        message_list.setSend_user_id(1);
        message_list.setMessage("还没有登录哦~");
        message_list.setSend_user_name("系统通知");
        message_list.setMsg_id(0);
        msglist.add(message_list);
    }

    private void inin_Refresh() {
        mRefreshLayout = view.findViewById(R.id.message_refreshLayout);
        mBezierRadarHeader = (BezierRadarHeader) mRefreshLayout.getRefreshHeader();
        mRefreshLayout.setEnableHeaderTranslationContent(true);
        mRefreshLayout.setHeaderHeight(100);
//        //自动加载
//        mRefreshLayout.autoRefresh();
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (user_id != -1) {
                    OkHttpUtils.post().url("http://www.ga666666.club:8080/mydiary/getNewMessage.do").addParams("userid", user_id + "").build()
                            .execute(new StringCallback() {
                                @Override
                                public void onError(Call call, Exception e, int id) {

                                }

                                @Override
                                public void onResponse(String response, int id) {
                                    try {
                                        Log.e("re", response);
                                        MessgaeJsonRoot messgaeJsonRoot = GsonUtils.fromJson(response, MessgaeJsonRoot.class);
                                        msglist = messgaeJsonRoot.getMessage_list();
                                    } catch (Exception e) {
                                        Log.e("re", e + "");
                                        msglist.clear();
                                        Message_list message_list = new Message_list();
                                        message_list.setSend_user_photo("https://gitee.com/gao666666/images/raw/master/image/20201110201430.png");
                                        message_list.setSend_user_id(1);
                                        message_list.setMessage("可能是还没有消息哦~");
                                        message_list.setSend_user_name("系统通知");
                                        message_list.setMsg_id(0);
                                        msglist.add(message_list);
                                    }

                                    //刷新
                                    initRecyclerView();
                                }
                            });
                    XToast.success(getActivity(), "刷新成功").show();
                    refreshLayout.finishRefresh();
                } else {
                    XToast.success(getActivity(), "请先登录哦~").show();
                    refreshLayout.finishRefresh();
                }
            }
        });
    }


    private void inin_marque() {
        datas.add("子曰:学而时习之，不亦说乎?");
        datas.add("有朋自远方来，不亦乐乎?");
        datas.add("人不知而不愠，不亦君子乎？");

        mTvMarquee = view.findViewById(R.id.tv_marquee);
        mTvMarquee.setSpeed(4);
        mTvMarquee.setOnMarqueeListener(new MarqueeTextView.OnMarqueeListener() {
            @Override
            public DisplayEntity onStartMarquee(DisplayEntity displayMsg, int index) {
                if (displayMsg.toString().equals("不亦君子乎？")) {
                    return null;
                } else {
                    //XToast.success(getActivity(),"开始滚动").show();
                    return displayMsg;
                }
            }

            @Override
            public List<DisplayEntity> onMarqueeFinished(List<DisplayEntity> displayDatas) {
                //XToast.success(getActivity(),"滚动完毕").show();
                return displayDatas;
            }
        });
        mTvMarquee.startSimpleRoll(datas);
    }

    private void init_Data() {
        bundle = getArguments();
        if (bundle != null && msglist.size() == 0) {
            msglist = (ArrayList<Message_list>) bundle.getSerializable("result");
        }
        Log.e("result", msglist.size() + "");

        //子曰：“学而时习之，不亦说乎？有朋自远方来，不亦乐乎？人不知而不愠，不亦君子乎？”

    }

    private void initRecyclerView() {
        //获取RecyclerView
        rv = (RecyclerView) view.findViewById(R.id.rv_message);
        //创建adapter
        messageAdapter = new MessageAdapter(getActivity(), msglist);
        messageAdapter.setMyClickListener(new MessageAdapter.IMyClickList() {
            ArrayList<Message_list> msglist2 = new ArrayList<Message_list>();
            Intent it = new Intent();

            @Override
            public void OnClick(int position) {
                if (!msglist.get(position).getSend_user_name().equals("系统通知")) {
                    OkHttpUtils.post().url("http://www.ga666666.club:8080/mydiary/getMessageById.do")
                            .addParams("userid", msglist.get(position).getUser_id() + "")
                            .addParams("sendid", msglist.get(position).getSend_user_id() + "")
                            .build().execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {

                        }

                        @Override
                        public void onResponse(String response, int id) {
                            Log.e("respone", response);
                            MessgaeJsonRoot messgaeJsonRoot = GsonUtils.fromJson(response, MessgaeJsonRoot.class);
                            msglist2 = messgaeJsonRoot.getMessage_list();
                            it.setClass(getActivity(), ChatActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putInt("send_id", msglist.get(position).getSend_user_id());
                            bundle.putString("sendname", msglist.get(position).getSend_user_name());
                            bundle.putSerializable("message", msglist2);
                            it.putExtras(bundle);
                            getActivity().startActivity(it);
                        }
                    });
                    //XToast.success(getActivity(), msglist.get(position).getMessage()).show();
                } else {
                    XToast.info(getActivity(), "尝试刷新一下哦~").show();
                }
            }
        });

        Log.e("count", messageAdapter.getItemCount() + "");
        //给RecyclerView设置adapter
        rv.setAdapter(messageAdapter);
        //设置layoutManager,可以设置显示效果，是线性布局、grid布局，还是瀑布流布局
        //参数是：上下文、列表方向（横向还是纵向）、是否倒叙
        rv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        //设置item的分割线
        if (rv.getItemDecorationCount()==0){
            rv.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        }
         //RecyclerView中没有item的监听事件，需要自己在适配器中写一个监听事件的接口。参数根据自定义

    }

    public void popMessage(int id) {
        new MaterialDialog.Builder(getContext())
                .title(id)
                .content(id)
                .positiveText("确认")
                .negativeText("取消")
                .show();
    }

    public static String getString(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            int responseCode = urlConnection.getResponseCode();
            if (responseCode == 200) {
                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                StringBuilder stringBuilder = new StringBuilder();
                String str = "";
                //拼接字符串
                while ((str = bufferedReader.readLine()) != null) {
                    stringBuilder.append(str);
                }
                //返回字符串
                String string = stringBuilder.toString();
                Log.e("response", string);
                return string;
            } else {
                Log.e("response", responseCode + "");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    ///判断是否有网返回boolean
//true 有网
//false 无网
    public static boolean getNet(Context context) {
        boolean net = false;
        ConnectivityManager conManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conManager.getActiveNetworkInfo();
        if (networkInfo != null) {
            net = networkInfo.isAvailable();
        }
        return net;
    }
}