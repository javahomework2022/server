package com.workonline.server;

public class RoomThread implements Runnable {
    String userid;
    public RoomThread(String id){
        this.userid=id;
    }
    public void run(){
        if(MessageReceiver.commands[0].equals("create_room")){
            new Room(User.userlist.get(userid)).creatRoom();

        }
    }
}
