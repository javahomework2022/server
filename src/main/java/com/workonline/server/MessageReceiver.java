package com.workonline.server;


import com.workonline.util.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class MessageReceiver implements Runnable{
     Socket socket;
     ObjectInputStream objectInputStream;
     ObjectOutputStream objectOutputStream;
     String username="";
     User loginUser=null;
     String result;
     static String[] commands;
     boolean ifLogined=true;
     public MessageReceiver(Socket socket) throws IOException {
          this.socket = socket;
          this.objectInputStream = new ObjectInputStream(socket.getInputStream());
          this.objectOutputStream=new ObjectOutputStream(socket.getOutputStream());
     }
     /**
     * 这个方法会在新的线程里处理接收到的Message
     */
     @Override
     public void run() {
          while (ifLogined){
               try {
                    Message message = (Message) objectInputStream.readObject();
                    commands = message.command.split(" ");
                    /*
                    * 这儿接收所有用户的message，需要在登录成功和注册成功后将Program.streams添加对应的人和输出流
                    * TODO
                     */
                    if("register".equals(commands[0])){
                         result=new Register(commands[1], commands[2]).register();
                         Message newmessage = new Message();
                         //注册成功
                         if (result.equals("register_success")) {
                              loginUser = User.userlist.get(commands[1]);
                              newmessage.command = "register_success";
                              SendMessage(newmessage);
                         }
                         //注册失败
                         else if (result.equals("register_fail_id_used")) {
                              loginUser = null;
                              newmessage.command = "register_fail_id_used";
                              SendMessage(newmessage);
                         }
                    }
                    else if("login".equals(commands[0])){
                         result=new Login(commands[1], commands[2]).login();
                         loginUser = User.loginUser;
                         Message newmessage = new Message();
                         if (result.equals("login_success")) {
                              loginUser = User.userlist.get(commands[1]);
                              newmessage.command = result;
                              SendMessage(newmessage);
                         } else if (result.equals("login_fail")) {
                              loginUser = null;
                              newmessage.command = result;
                              SendMessage(newmessage);
                         }
                    }
                    //创建房间
                    //要返回主线程去创建新的房间线程
                    else if("create_room".equals(commands[0])){
                         result=new Room(loginUser).creatRoom();
                         String[] results=result.split(" ");
                         Message newmessage=new Message();
                         if(results[1].equals("creatRoom_success")){
                              newmessage.command=results[1];
                              SendMessage(newmessage);
                         }
                    }
                    else if("enter".equals(commands[0])){
                         result=new Room(commands[1],loginUser.id).enterRoom();
                         String[] results=result.split(" ");
                         Message newmessage=new Message();
                         if(results[1].equals("enter_room_success")||result.equals("room_not_exist")){
                              newmessage.command=result;
                              SendMessage(newmessage);
                         }
                         Room room=Room.roomlist.get(commands[1]);
                         newmessage=new Message();
                         newmessage.command="send_document "+room.operations.size()+" "+room.roomId;
                         newmessage.document=room.text;
                         SendMessage(newmessage);
                    }
                    else if("quit_room".equals(commands[0])) {
                         Room room=Room.roomlist.get(commands[1]);
                         room.quitRoom(loginUser.id);
                    }
                    else if("close_room".equals(commands[0])){
                         for(String userid:Room.roomlist.get(commands[1]).roomUser.keySet()){
                              Message newmessage=new Message();
                              newmessage.command="room_closed "+commands[1];
                              SendMessage(newmessage);
                         }
                    }
                    else if("operation".equals(commands[0])){
                         Room room=Room.roomlist.get(commands[1]);
                         Operation operation=new Operation();
                         try{
                              operation=room.receiveOperation(message.operation.version,message.operation.operation);
                         }
                         catch (Exception e){
                              e.printStackTrace();
                         }
                         Message newmessage=new Message();
                         Text_Operation text_operation=new Text_Operation();
                         text_operation.operation=operation;
                         text_operation.username=loginUser.id;
                         //添加版本号
                         newmessage.operation=text_operation;
                         newmessage.command="broadcast "+room.roomId;
                         SendMessage(newmessage);
                    }
                    else if("log_out".equals(commands[0])){
                         if(commands[1].equals(loginUser.id)){
                              ifLogined=false;
                         }
                    }
               } catch (IOException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
               }
          }
     }
     public void SendMessage(Message message){
          synchronized (result){
               try{
                    objectOutputStream.writeObject(message);
                    objectOutputStream.flush();
               }
               catch (IOException e){
                    e.printStackTrace();
               }
          }
     }
}