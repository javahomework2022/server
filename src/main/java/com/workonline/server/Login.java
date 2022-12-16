package com.workonline.server;


public class Login {
    String id,password,result;
    public Login(String id,String password){
        this.id=id;
        this.password=password;
    }

    /**
     *
     */
    public void login(){
        if (correctPassword()) {
            MessageReceiver.result = "login_success";
            Program.flag = id + " login_success";
        }
    }
    boolean existUser(){
        if (User.userlist.containsKey(id))
            return true;
        else {
            //前端输出"登陆失败"
            MessageReceiver.result = "login_fail";
            return false;
        }
    }
    boolean correctPassword() {
        if (existUser()) {
            if (User.userlist.get(id).password.equals(password)) {
                //前端输出“登陆成功"
                return true;
            }
            //前端输出“登陆失败”
            MessageReceiver.result = "login_fail";
        }
        return false;
    }
}
