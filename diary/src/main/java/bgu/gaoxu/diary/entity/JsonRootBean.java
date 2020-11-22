/**
  * Copyright 2020 bejson.com 
  */
package bgu.gaoxu.diary.entity;
import java.util.List;

/**
 * Auto-generated: 2020-09-25 21:16:52
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class JsonRootBean {

    private String request_id;
    private int time_used;
    private List<Faces> faces;
    private String image_id;
    private int face_num;
    public void setRequest_id(String request_id) {
         this.request_id = request_id;
     }
     public String getRequest_id() {
         return request_id;
     }

    public void setTime_used(int time_used) {
         this.time_used = time_used;
     }
     public int getTime_used() {
         return time_used;
     }

    public void setFaces(List<Faces> faces) {
         this.faces = faces;
     }
     public List<Faces> getFaces() {
         return faces;
     }

    public void setImage_id(String image_id) {
         this.image_id = image_id;
     }
     public String getImage_id() {
         return image_id;
     }

    public void setFace_num(int face_num) {
         this.face_num = face_num;
     }
     public int getFace_num() {
         return face_num;
     }

}