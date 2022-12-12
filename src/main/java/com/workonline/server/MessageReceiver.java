package com.workonline.server;


import com.workonline.util.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class MessageReceiver implements Runnable{
     Socket socket;
     ObjectInputStream objectInputStream;
     String username="";
     public MessageReceiver(Socket socket) throws IOException {
          this.socket = socket;
          this.objectInputStream = new ObjectInputStream(socket.getInputStream());
     }
     /**
     * 这个方法会在新的线程里处理接收到的Message
     */
     @Override
     public void run() {
          while (true){
               try {
                    Message message = (Message) objectInputStream.readObject();
                    String[] commands = message.command.split(" ");
                    User loginUser=null;
                    /*
                    * 这儿接收所有用户的message，需要在登录成功和注册成功后将Program.streams添加对应的人和输出流
                    * TODO
                     */
                    if("register".equals(commands[0])){
                         loginUser=new Register(commands[1],commands[2]).register();
                         //userThread.put(loginUser.id, this);
                         //向客户端发送 sendmessage
                        // Program.SendMessage();
                    }
                    else if("login".equals(commands[0])){
                         new Login(commands[1],commands[2]).login();
                         loginUser=User.loginUser;
                         //向客户端发送 sendmessage
                    }
                    else if("create_room".equals(commands[0])){
                         //new Room().creatRoom();
                         Program.flag=loginUser.id;
                         while(Program.flag.equals(loginUser.id+"_success")){
                              //Program.Sendmeassge();
                              new Room(User.userlist.get(Program.flag));
                         }
                    }
                    else if("enter".equals(commands[0])){
                         new Room(commands[1]).enterRoom();
                    }
                    else if("quit_room".equals(commands[0])){
                         new Room(commands[1]).quitRoom(loginUser.id);
                    }
               } catch (IOException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
               }
          }
     }
}