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
        if(correctPassword()){
            User.loginUser =User.userlist.get(id);
        }
    }
    boolean existUser(){
        if(User.userlist.containsKey(id))
            return true;
        else{
            //前端输出"登陆失败"
            Test.sendMessage="login_fail";
            return false;
        }
    }
    boolean correctPassword(){
        if(existUser()){
            if(User.userlist.get(id).password.equals(password)){
                //前端输出“登陆成功"
                Test.sendMessage="login_success";
                return true;
            }
            //前端输出“登陆失败”
            Test.sendMessage="login_fail";
        }
        return false;
    }
}
