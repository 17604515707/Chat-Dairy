package bgu.gaoxu.diary.entity;

import java.io.Serializable;

/**
 * Created by GA666666 on 2020/11/3 22:06
 */
public class Diary implements Serializable {
    private int diary_ID;
    private String diary_title;
    private String diary_context;
    private String diary_time;


    public String getDiary_title() {
        return diary_title;
    }

    public void setDiary_title(String diary_title) {
        this.diary_title = diary_title;
    }

    public String getDiary_context() {
        return diary_context;
    }

    public void setDiary_context(String diary_context) {
        this.diary_context = diary_context;
    }

    public String getDiary_time() {
        return diary_time;
    }

    public void setDiary_time(String diary_time) {
        this.diary_time = diary_time;
    }

    public int getDiary_ID() {
        return diary_ID;
    }

    public void setDiary_ID(int diary_ID) {
        this.diary_ID = diary_ID;
    }
}
