package com.workonline.server;


import com.workonline.util.Message;

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
     static String result;
     static String[] commands;
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
          while (true){
               try {
                    Message message = (Message) objectInputStream.readObject();
                    commands = message.command.split(" ");
                    /*
                    * 这儿接收所有用户的message，需要在登录成功和注册成功后将Program.streams添加对应的人和输出流
                    * TODO
                     */
                    if("register".equals(commands[0])){
                         synchronized (result) {
                              new Register(commands[1], commands[2]).register();
                              Message newmessage = new Message();
                              //注册成功
                              if (result.equals("register_success")) {
                                   loginUser = User.userlist.get(commands[1]);
                                   newmessage.command = "register_success";
                                   //添加对应输出流
                                   Program.streams.put(loginUser.id, new ObjectOutputStream(socket.getOutputStream()));
                                   SendMessage(newmessage);
                              }
                              //注册失败
                              else if (result.equals("register_fail_id_used")) {
                                   loginUser = null;
                                   newmessage.command = "register_fail_id_used";
                                   //要加一个uid
                                   SendMessage(newmessage);
                              }
                              //唤醒其他线程
                              result.notifyAll();
                         }
                         //让路给其他线程
                         try{
                              Thread.sleep(10);
                         } catch (InterruptedException e){
                              e.printStackTrace();
                         }
                    }
                    else if("login".equals(commands[0])){
                         synchronized (result) {
                              new Login(commands[1], commands[2]).login();
                              loginUser = User.loginUser;
                              Message newmessage = new Message();
                              if (result.equals("login_success")) {
                                   loginUser = User.loginUser;
                                   newmessage.command = result;
                                   //添加对应输出流
                                   Program.streams.put(loginUser.id, new ObjectOutputStream(socket.getOutputStream()));
                                   SendMessage(newmessage);
                              } else if (result.equals("login_fail")) {
                                   loginUser = null;
                                   newmessage.command = result;
                                   //同样要加uid
                                   SendMessage(newmessage);
                              }
                              result.notifyAll();
                              try {
                                   result.wait();
                              } catch (InterruptedException e) {
                                   e.printStackTrace();
                              }
                         }
                    }
                    //创建房间
                    //要返回主线程去创建新的房间线程
                    else if("create_room".equals(commands[0])){
                         Thread RoomThread=new Thread(new RoomThread(loginUser.id));
                    }
                    else if("enter".equals(commands[0])){
                         new Room(commands[1],loginUser.id).enterRoom();
                    }
                    else if("quit_room".equals(commands[0])) {
                         new Room(commands[1],loginUser.id).quitRoom(loginUser.id);
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