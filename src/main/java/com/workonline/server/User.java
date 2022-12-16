package com.workonline.server;

import java.util.HashMap;
import java.util.Map;


public class User {
    String id;
    String password;
    //存储所有用户的账号密码
    public static Map<String,User> userlist=new HashMap<>();
    //每个用户所在的房间列表
    public Map<String, Room> roomlist=new HashMap<>();
    //当前登陆用户
    public User(String id,String password){
        this.id=id;
        this.password=password;
    }
}
