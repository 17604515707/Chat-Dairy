package bgu.gaoxu.diary.xui;

import android.app.Application;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.xuexiang.xui.XUI;

/**
 * Created by GA666666 on 2020/10/31 23:45
 */
public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        XUI.init(this); //初始化UI框架
        XUI.debug(true);  //开启UI框架调试日志
        SpeechUtility.createUtility(this, SpeechConstant.APPID +"=5fa40d30");
    }
}