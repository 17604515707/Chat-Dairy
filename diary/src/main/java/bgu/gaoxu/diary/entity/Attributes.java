/**
  * Copyright 2020 bejson.com 
  */
package bgu.gaoxu.diary.entity;


public class Attributes {

    private Smile smile;
    private Emotion emotion;
    public void setSmile(Smile smile) {
         this.smile = smile;
     }
     public Smile getSmile() {
         return smile;
     }

    public void setEmotion(Emotion emotion) {
         this.emotion = emotion;
     }
     public Emotion getEmotion() {
         return emotion;
     }

}