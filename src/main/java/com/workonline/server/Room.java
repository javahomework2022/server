package com.workonline.server;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Room implements Serializable {
    String text;
    User roomOwner;
    String roomId;
    String userid;
    //存储当前房间中的用户
    Map<String,User>roomUser=new HashMap<>();
    //存储所有的房间
    static Map<String,Room>roomlist=new HashMap<>();
    static int currentId=10000;
    static Room currentRoom;
    //连接房间和文档
    //新建房间
    public Room(String roomId,String userid){
        this.roomId=roomId;
        this.userid=userid;
    }
    public Room(User loginUser){
        this.roomId=Integer.toString(currentId+1);
        roomOwner=loginUser;
    }
    public void creatRoom(){
        synchronized (MessageReceiver.result) {
            User.userlist.get(roomOwner.id).roomlist.put(this.roomId, this);
            this.roomUser.put(roomOwner.id, roomOwner);
            roomlist.put(this.roomId, this);
            Program.flag = roomOwner.id + " creatRoom_success";
            MessageReceiver.result = Program.flag + " " + roomId;
        }
    }
    public void enterRoom(){
        synchronized (MessageReceiver.result) {
            if (existroom()) {
                User.userlist.get(userid).roomlist.put(roomId, roomlist.get(roomId));
                roomlist.get(roomId).roomUser.put(userid,User.userlist.get(userid));
                MessageReceiver.result="enter_room_success "+roomId;
            }
        }
    }
    public void quitRoom(String userid){
        synchronized (MessageReceiver.result) {
            if (inRoom()) {
                User.userlist.get(userid).roomlist.remove(roomId, roomlist.get(roomId));
                roomlist.get(roomId).roomUser.remove(userid);
            }
        }
    }
    boolean existroom(){
        if(roomlist.containsKey(roomId))
            return true;
        else {
            MessageReceiver.result="room_not_exist";
            return false;
        }
    }
    boolean inRoom(){
        if(User.userlist.get(userid).roomlist.containsKey(roomId))
            return true;
        else{
            MessageReceiver.result="room_not_exist";
            return false;
        }
    }
}