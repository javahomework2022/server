package com.workonline.server;


import java.io.Serializable;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import com.workonline.util.*;
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
    ArrayList<Operation> operations = new ArrayList<>();
    public Room(String roomId,String userid){
        this.roomId=roomId;
        this.userid=userid;
    }
    public Room(User loginUser){
        this.roomId=Integer.toString(currentId+1);
        roomOwner=loginUser;
    }
    public String creatRoom(String text){
        User.userlist.get(roomOwner.id).roomlist.put(this.roomId, this);
        this.roomUser.put(roomOwner.id, roomOwner);
        roomlist.put(this.roomId, this);
        this.text=text;
        return "create_room_success "+roomId;
    }
    public String enterRoom(){
        if (existroom()) {
            User.userlist.get(userid).roomlist.put(roomId, roomlist.get(roomId));
            roomlist.get(roomId).roomUser.put(userid,User.userlist.get(userid));
            return "enter_room_success "+roomId;
        }
        else return "enter_room_fail";
    }
    public void quitRoom(String userid){
        if (inRoom()) {
            User.userlist.get(userid).roomlist.remove(roomId);
            this.roomUser.remove(userid);
        }
    }
    public Operation receiveOperation(int version, Operation receivedOperation)throws Exception{
        Operation operation = (Operation) receivedOperation.clone();
        for(int i = version;i<operations.size();i++){
            AbstractMap.SimpleEntry<Operation,Operation> entry = OT.transform(operation,operations.get(i));
            operation = entry.getKey();
        }
        operations.add(operation);
        return operation;
    }
    boolean existroom(){
        if(roomlist.containsKey(roomId))
            return true;
        else {
            return false;
        }
    }
    boolean inRoom(){
        if(User.userlist.get(userid).roomlist.containsKey(roomId))
            return true;
        else{
            return false;
        }
    }
}