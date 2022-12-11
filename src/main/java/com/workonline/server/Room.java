package com.workonline.server;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Room implements Serializable {
    String text;
    User roomOwner;
    String roomId;
    //存储当前房间中的用户
    Map<String,User>roomUser=new HashMap<>();
    //存储所有的房间
    static Map<String,Room>roomlist=new HashMap<>();
    static String currentId="10000";
    static Room currentRoom;
    //连接房间和文档
    //新建房间
    public Room(){
        this.roomId=Integer.toString(Integer.parseInt(currentId)+1);
        currentId=roomId;
        //this.roomOwner=User.loginUser;
    }
    public Room(String roomId){
        this.roomId=roomId;
    }
    public Room(User loginUser){
        roomOwner=loginUser;
    }
    public void creatRoom(){
        if(alreadyLogged()){
            //User.loginUser.roomlist.put(this.roomId,this);
            User.userlist.get(roomOwner.id).roomlist.put(this.roomId,this);
            this.roomUser.put(roomOwner.id,roomOwner);
            roomlist.put(this.roomId,this);
            Program.flag=roomOwner.id+"_success";
        }
    }
    public void enterRoom(){
        if(existroom()){
            User.loginUser.roomlist.put(roomId,roomlist.get(roomId));
        }

    }
    public void quitRoom(){
        if(inRoom()){
            currentRoom=roomlist.get(roomId);
            if(currentRoom.roomOwner.id.equals(User.loginUser.id)){
                for(String userid:roomUser.keySet()){
                    //多线程涉及到退出
                }
            }
            else {
                User.loginUser.roomlist.remove(roomId, roomlist.get(roomId));
            }
        }
    }
    boolean alreadyLogged(){
        if(User.loginUser!=null)
            return true;
        else{
            return false;
        }
    }
    boolean existroom(){
        if(roomlist.containsKey(roomId))
            return true;
        else {
            Test.sendMessage="room_not_exist";
            return false;
        }
    }
    boolean inRoom(){
        if(User.loginUser.roomlist.containsKey(roomId))
            return true;
        else{
            Test.sendMessage="room_not_exist";
            return false;
        }
    }
}