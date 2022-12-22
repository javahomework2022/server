/**
 * 这个包是客户端需要运行的程序包.
 * @author 不要爆零小组
 * @version JDK17
 */
package com.workonline.server;

import com.workonline.util.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import static com.workonline.server.Room.enterRoom;

/**
 * 该类用于接收用户发送的信息并对其进行处理.
 */
public class MessageReceiver implements Runnable{
     Socket socket;
     ObjectInputStream objectInputStream;
     ObjectOutputStream objectOutputStream;
     String username="";
     User loginUser=null;
     String result;
     static String[] commands;
     boolean ifLogined=true;
     static Map<String,ObjectOutputStream>streams=new HashMap<>();

     /**
      * 构造器.
      * @param socket 用户传输的套接字
      * @throws IOException 在得到输入输出流方法中抛出的异常
      */
     public MessageReceiver(Socket socket) throws IOException {
          this.socket = socket;
          this.objectInputStream = new ObjectInputStream(socket.getInputStream());
          this.objectOutputStream=new ObjectOutputStream(socket.getOutputStream());
     }
     /**
     * 这个方法会在新的线程里处理接收到的Message.
     */
     @Override
     public void run() {
          while (ifLogined){
               try {
                    Message message = (Message) objectInputStream.readObject();
                    commands = message.command.split(" ");
                    System.out.println("收到命令："+message.command);
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
                              streams.put(loginUser.id,objectOutputStream);
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
                         Message newmessage = new Message();
                         if (result.equals("login_success")) {
                              loginUser = User.userlist.get(commands[1]);
                              newmessage.command = result;
                              streams.put(loginUser.id,objectOutputStream);
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
                         result=new Room(loginUser).creatRoom(message.document);
                         String[] results=result.split(" ");
                         Message newmessage=new Message();
                         if(results[0].equals("create_room_success")){
                              newmessage.command=result;
                              newmessage.document = message.document;
                              SendMessage(newmessage);
                         }
                    }
                    else if("enter".equals(commands[0])){
                         result=enterRoom(loginUser.id,commands[1]);
                         String[] results=result.split(" ");
                         Message newmessage=new Message();
                         if(result.equals("enter_room_fail")){
                              newmessage.command=result;
                              SendMessage(newmessage);
                         }
                         else if(results[0].equals("enter_room_success")) {
                              Room room = Room.roomlist.get(commands[1]);
                              newmessage = new Message();
                              newmessage.command = "enter_room_success " + room.operations.size() + " " + room.roomId;
                              newmessage.document = room.text;
                              SendMessage(newmessage);
                         }
                    }
                    else if("quit_room".equals(commands[0])) {
                         Room room=Room.roomlist.get(commands[1]);
                         room.quitRoom(loginUser.id);
                    }
                    else if("close_room".equals(commands[0])){
                         Room room=Room.roomlist.get(commands[1]);
                         Message newmessage=new Message();
                         newmessage.command="room_closed "+room.roomId;
                         for(String userid:room.roomUser.keySet()){
                              SendMessageByUserid(newmessage, userid);
                         }
                         Room.roomlist.remove(room.roomId);
                    }
                    else if("operation".equals(commands[0])){
                         synchronized (Room.roomlist.get(commands[1])) {
                              System.out.println("收到operation:来自："+message.operation.username+" 版本:"+message.operation.version);
                              System.out.println(message.operation.operation.toString());
                              Room room = Room.roomlist.get(commands[1]);
                              Operation operation = new Operation();
                              try {
                                   operation = room.receiveOperation(message.operation.version, message.operation.operation);
                                   StringBuilder newStr = new StringBuilder();
                                   int index = 0;
                                   for(AtomicOperation i:operation.getOperations())
                                   {
                                        if(i.isInsert())
                                        {
                                             newStr.append(i.getInsertString());
                                        }
                                        else if(i.isDelete())
                                        {
                                             index+=i.getDeleteLength();
                                        }
                                        else if(i.isRetain())
                                        {
                                             newStr.append(room.text, index, index+i.getRetainLength());
                                             index += i.getRetainLength();
                                        }
                                   }
                                   room.text=newStr.toString();
                              } catch (Exception e) {
                                   e.printStackTrace();
                              }

                              Message newmessage = new Message();
                              Text_Operation text_operation = new Text_Operation(0,username,operation);
                              text_operation.operation = operation;
                              System.out.println("广播operation:");
                              System.out.println(operation.toString());
                              text_operation.username = message.operation.username;
                              newmessage.operation = text_operation;
                              newmessage.command = "broadcast " + room.roomId;
                              for(String userid:room.roomUser.keySet()){
                                   SendMessageByUserid(newmessage, userid);
                              }
                         }
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

     /**
      * 对指定的用户发送信息.
      * @param newmessage 要发送的信息对象
      * @param userid 给这个ID的用户发送信息
      */
     private void SendMessageByUserid(Message newmessage, String userid) {
          try {
               streams.get(userid).writeObject(newmessage);
               streams.get(userid).flush();
               System.out.println("成功发送:"+newmessage.command);
          } catch (IOException e){
               System.out.println("发送失败:"+newmessage.command);
               e.printStackTrace();
          }
     }

     /**
      * 给当前线程对应的连接用户发送信息.
      * @param message 要发送的信息对象
      */
     public void SendMessage(Message message){
          try{
               objectOutputStream.writeObject(message);
               objectOutputStream.flush();
               System.out.println("成功发送:"+message.command);
          }
          catch (IOException e){
               System.out.println("发送失败:"+message.command);
               e.printStackTrace();
          }
     }
}