package com.workonline.server;


import com.workonline.util.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

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

                    /*
                    * 这儿接收所有用户的message，需要在登录成功和注册成功后将Program.streams添加对应的人和输出流
                    * TODO
                     */
               } catch (IOException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
               }
          }
     }
}