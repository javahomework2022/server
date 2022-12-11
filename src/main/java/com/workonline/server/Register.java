package com.workonline.server;
public class Register {
    String id,password ;
    public Register(String id,String password){
        this.id=id;
        this.password=password;
    }
    public void register(){
        if(unexistUser()){
            User newUser=new User(id,password);
            User.userlist.put(id,newUser);
        }
    }
    boolean unexistUser(){
        if(User.userlist.containsKey(id)){
            //向前端界面发送“该账号已被注册”
            //Test.sendMessage="register_fail_id_used";
            return false;
        }
        else return true;
    }
}