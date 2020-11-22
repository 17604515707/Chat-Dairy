
package bgu.gaoxu.diary.entity;


public class Follow_list {

    private int follow_id;
    private int user_id;
    private int follow_user_id;
    private String follow_user_name;
    private String follow_user_img;
    public void setFollow_id(int follow_id) {
         this.follow_id = follow_id;
     }
     public int getFollow_id() {
         return follow_id;
     }

    public void setUser_id(int user_id) {
         this.user_id = user_id;
     }
     public int getUser_id() {
         return user_id;
     }

    public void setFollow_user_id(int follow_user_id) {
         this.follow_user_id = follow_user_id;
     }
     public int getFollow_user_id() {
         return follow_user_id;
     }

    public void setFollow_user_name(String follow_user_name) {
         this.follow_user_name = follow_user_name;
     }
     public String getFollow_user_name() {
         return follow_user_name;
     }

    public void setFollow_user_img(String follow_user_img) {
         this.follow_user_img = follow_user_img;
     }
     public String getFollow_user_img() {
         return follow_user_img;
     }

}