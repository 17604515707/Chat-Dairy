package bgu.gaoxu.diary.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by GA666666 on 2020/11/15 1:56
 */

public class LoginState {
    private static final int MODE_PRIVATE = 0;
    private SharedPreferences mySharedPreferences,sharedPreferences;
    public int verify(Context context){
        mySharedPreferences = context.getSharedPreferences("user_info", MODE_PRIVATE);
        if (mySharedPreferences.getInt("user_id", -1)!=-1){
            return mySharedPreferences.getInt("user_id", -1);
        }else{
            return -1;
        }
    }
}
