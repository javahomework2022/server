package com.workonline.server;
public class Register {
    String id,password ;
    public Register(String id,String password){
        this.id=id;
        this.password=password;
    }
    public String register(){
        if (unexistUser()) {
            User newUser = new User(id, password);
            User.userlist.put(id, newUser);
            return "register_success";
        }
        else return "register_fail_id_used";
    }
    boolean unexistUser(){
        if(User.userlist.containsKey(id)){
            return false;
        }
        else return true;
    }
}