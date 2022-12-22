/**
 * 这个包是客户端需要运行的程序包.
 * @author 不要爆零小组
 * @version JDK17
 */
package com.workonline.server;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import com.workonline.util.*;

/**
 * 该类用于构建房间对象并处理相关行为
 */
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

    /**
     * 构造器.
     * @param loginUser 当前登录用户
     */
    public Room(User loginUser){
        this.roomId=Integer.toString(++currentId);
        roomOwner=loginUser;
    }

    /**
     * 这个方法用于创建房间.
     * @param text 构建房间需要提供的文档
     * @return 创建成功的字符串，用于前后端信息交流
     */
    public String creatRoom(String text){
        User.userlist.get(roomOwner.id).roomlist.put(this.roomId, this);
        this.roomUser.put(roomOwner.id, roomOwner);
        roomlist.put(this.roomId, this);
        this.text=text;
        return "create_room_success "+roomId;
    }

    /**
     * 这个方法用于一个用户进入一个房间.
     * @param userid 要进入房间的用户ID
     * @param roomId 用户要进入的房间ID
     * @return 进入房间是否成功的字符串，用于前后端信息交流
     */
    public static String enterRoom(String userid,String roomId){
        if (existroom(roomId)) {
            User.userlist.get(userid).roomlist.put(roomId, roomlist.get(roomId));
            roomlist.get(roomId).roomUser.put(userid,User.userlist.get(userid));
            return "enter_room_success "+roomId;
        }
        else return "enter_room_fail";
    }

    /**
     * 用户退出房间时调用.
     * @param userid 要退出房间的用户的ID
     */
    public void quitRoom(String userid){
        if (inRoom(userid,roomId)) {
            User.userlist.get(userid).roomlist.remove(roomId);
            this.roomUser.remove(userid);
        }
    }

    /**
     * 这个方法用于后端处理一个房间中某个用户对文档进行的操作，并返回转换并发冲突后的操作.
     * @param version 文档版本号
     * @param receivedOperation 服务器接收到的用户的操作
     * @return 服务器需要广播给所有用户的操作
     * @throws Exception 在OT.transform方法中抛出的异常
     */
    public Operation receiveOperation(int version, Operation receivedOperation)throws Exception{
        Operation operation = (Operation) receivedOperation.clone();
        for(int i = version;i<operations.size();i++){
            AbstractMap.SimpleEntry<Operation,Operation> entry = OT.transform(operation,operations.get(i));
            operation = entry.getKey();
        }
        operations.add(operation);
        return operation;
    }

    /**
     * 用于判断房间ID号是否存在.
     * @param roomId 需要判断的房间ID
     * @return 该房间ID是否存在
     */
    static boolean existroom(String roomId){
        if(roomlist.containsKey(roomId))
            return true;
        else {
            return false;
        }
    }

    /**
     * 用于判断用户是否在房间中.
     * @param userid 要退出房间的用户ID
     * @param roomId 用户要退出的房间ID
     * @return 用户是否在房间中
     */
    static boolean inRoom(String userid,String roomId){
        if(User.userlist.get(userid).roomlist.containsKey(roomId))
            return true;
        else{
            return false;
        }
    }
}