
package bgu.gaoxu.diary.entity;


import java.io.Serializable;

public class Message_list implements Serializable {

    private int msg_id;
    private int user_id;
    private int send_user_id;
    private String send_user_name;
    private String message;
    private String send_time;
    private String send_user_photo;

    public String getSend_user_photo() {
        return send_user_photo;
    }

    public void setSend_user_photo(String send_user_photo) {
        this.send_user_photo = send_user_photo;
    }

    public void setMsg_id(int msg_id) {
         this.msg_id = msg_id;
     }
     public int getMsg_id() {
         return msg_id;
     }

    public void setUser_id(int user_id) {
         this.user_id = user_id;
     }
     public int getUser_id() {
         return user_id;
     }

    public void setSend_user_id(int send_user_id) {
         this.send_user_id = send_user_id;
     }
     public int getSend_user_id() {
         return send_user_id;
     }

    public void setSend_user_name(String send_user_name) {
         this.send_user_name = send_user_name;
     }
     public String getSend_user_name() {
         return send_user_name;
     }

    public void setMessage(String message) {
         this.message = message;
     }
     public String getMessage() {
         return message;
     }

    public void setSend_time(String send_time) {
         this.send_time = send_time;
     }
     public String getSend_time() {
         return send_time;
     }

}