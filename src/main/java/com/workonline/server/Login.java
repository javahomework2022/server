/**
 * 这个包是客户端需要运行的程序包.
 * @author
 * @version JDK17
 */
package com.workonline.server;

/**
 * 该类用于处理用户登录行为.
 */
public class Login {
    String id,password,result;

    /**
     * 构造器.
     * @param id 登录的用户的ID
     * @param password 用户密码
     */
    public Login(String id,String password){
        this.id=id;
        this.password=password;
    }

    /**
     * 用于判断用户登录时用户名与密码是否正确.
     */
    public String login(){
        if (User.userlist.containsKey(id)&&User.userlist.get(id).password.equals(password)) {
            return "login_success";
        }
        else return "login_fail";
    }
}
