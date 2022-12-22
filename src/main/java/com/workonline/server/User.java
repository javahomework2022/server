/**
 * 这个包是客户端需要运行的程序包.
 * @author 不要爆零小组
 * @version JDK17
 */
package com.workonline.server;

import java.util.HashMap;
import java.util.Map;

/**
 * 这个类用于构建用户对象.
 */
public class User {
    String id;
    String password;
    /**
     * 用于存储所有用户的账号密码.
     */
    public static Map<String,User> userlist=new HashMap<>();
    /**
     * 每个用户所在房间的列表.
     */
    public Map<String, Room> roomlist=new HashMap<>();

    /**
     * 构造器.
     * @param id 用户ID
     * @param password 用户ID对应的密码
     */
    public User(String id,String password){
        this.id=id;
        this.password=password;
    }
}
