package bgu.gaoxu.diary.entity;

import java.util.ArrayList;

/**
 * Created by GA666666 on 2020/11/10 18:29
 */
public class MessgaeJsonRoot {
    private int erro_code;
    private ArrayList<Message_list> message_list;

    public void setErro_code(int erro_code) {
        this.erro_code = erro_code;
    }

    public int getErro_code() {
        return erro_code;
    }

    public void setMessage_list(ArrayList<Message_list> message_list) {
        this.message_list = message_list;
    }

    public ArrayList<Message_list> getMessage_list() {
        return message_list;
    }

}