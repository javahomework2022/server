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
    public String login(){
        if (correctPassword()) {
            return "login_success";
        }
        else return "login_fail";
    }
    boolean existUser(){
        if (User.userlist.containsKey(id))
            return true;
        else {
            return false;
        }
    }
    boolean correctPassword() {
        if (existUser()) {
            if (User.userlist.get(id).password.equals(password)) {
                return true;
            }
        }
        return false;
    }
}
