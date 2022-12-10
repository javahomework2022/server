package com.workonline.server;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Room implements Serializable {
    User roomOwner;
    String roomId;
    Map<String,User>roomUser=new HashMap<>();
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
    public void creatRoom(){

    }
    //加入房间
    public void enterRoom(){
        if(true)
            ;
    }
}