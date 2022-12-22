/**
 * 这个包是客户端需要运行的程序包.
 * @author 不要爆零小组
 * @version JDK17
 */
package com.workonline.server;

/**
 * 该类用于用户注册.
 */
public class Register {
    String id,password ;

    /**
     * 构造器.
     * @param id 用户ID
     * @param password 用户密码
     */
    public Register(String id,String password){
        this.id=id;
        this.password=password;
    }

    /**
     * 用户申请注册.
     * @return 注册成功或失败的字符串，用于前后端信息交流
     */
    public String register(){
        if (unexistUser()) {
            User newUser = new User(id, password);
            User.userlist.put(id, newUser);
            return "register_success";
        }
        else return "register_fail_id_used";
    }

    /**
     * 判断用户名的有效性.
     * @return 用户名是否有效
     */
    boolean unexistUser(){
        if(User.userlist.containsKey(id)){
            return false;
        }
        else return true;
    }
}