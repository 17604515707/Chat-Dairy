package bgu.gaoxu.diary.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GA666666 on 2020/11/10 14:27
 */
public class FollowJsonBean {


    private int erro_code;
    private ArrayList<Follow_list> follow_list;

    public void setErro_code(int erro_code) {
        this.erro_code = erro_code;
    }

    public int getErro_code() {
        return erro_code;
    }

    public void setFollow_list(ArrayList<Follow_list> follow_list) {
        this.follow_list = follow_list;
    }

    public ArrayList<Follow_list> getFollow_list() {
        return follow_list;
    }
}
