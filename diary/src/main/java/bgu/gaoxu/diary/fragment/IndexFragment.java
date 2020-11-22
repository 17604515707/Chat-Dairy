package bgu.gaoxu.diary.fragment;

import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.xuexiang.xui.widget.banner.widget.banner.BannerItem;
import com.xuexiang.xui.widget.banner.widget.banner.SimpleImageBanner;
import com.xuexiang.xui.widget.banner.widget.banner.base.BaseBanner;

import java.util.ArrayList;
import java.util.List;

import bgu.gaoxu.diary.R;


/**
 * Created by GA666666 on 2020/11/7 22:07
 */
public class IndexFragment extends Fragment {

    private View view;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private WebView webView;

    public static String[] titles = new String[]{
            "重磅！拜登赢得大选 修改推特认证：美国当选总统",
            "拜登两次败选 为何能胜特朗普?媒体:有两大天赐良机",
            "谭龙闪击亚泰3-0夺中甲冠军 时隔728天重返中超",
            "朝鲜战争美军司令给彭德怀下战书 让志愿军吃尽苦头",
            "何猷君形象崩塌 节目中公然说暧昧语言 奚梦瑶狠瞪",
    };
    private List<BannerItem> mData;
    public static String[] urls = new String[]{//640*360 360/640=0.5625
            "http://cms-bucket.ws.126.net/2020/1108/af159ae0j00qjfq2u000uc000s600e3c.jpg",//伪装者:胡歌演绎"痞子特工"
            "http://dingyue.ws.126.net/2020/1108/1949cda5j00qjgkhb000ac0009c005uc.jpg",//无心法师:生死离别!月牙遭虐杀
            "http://cms-bucket.ws.126.net/2020/1108/2de1d2aep00qjh0e3003xc0009c0070c.png",//花千骨:尊上沦为花千骨
            "http://dingyue.ws.126.net/2020/1108/216304cej00qjgq20000oc000b4007fg.jpg",//综艺饭:胖轩偷看夏天洗澡掀波澜
            "http://cms-bucket.ws.126.net/2020/1108/f4fcfd70j00qjgq75000vc000s600e3c.jpg",//碟中谍4:阿汤哥高塔命悬一线,超越不可能
    };

    public static IndexFragment newInstance(String param1, String param2) {
        IndexFragment fragment = new IndexFragment();
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

        view = inflater.inflate(R.layout.fragment_index, container, false);
        webView = view.findViewById(R.id.webView);
        webView.getSettings().setUserAgentString("Android");
        webView.getSettings().setJavaScriptEnabled(true);//启用js
        webView.getSettings().setBlockNetworkImage(false);//解决图片不显示
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    //按返回键操作并且能回退网页
                    if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
                        //后退
                        webView.goBack();
                        return true;
                    }
                }
                return false;
            }
        });
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("https://blog.csdn.net/");
        return view;
    }
}