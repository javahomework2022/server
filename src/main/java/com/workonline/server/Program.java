package com.workonline.server;

import com.workonline.util.Message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;


/**
 *
 */
public class Program {
    static String flag="null";
    static Map<String,Thread> userThread=new HashMap<>();
    //一个用户名对于一个输出流,登录后添加
    public static HashMap<String, ObjectOutputStream > streams = new HashMap<>();
    public static void main(String[] args) throws IOException {
        //主线程接收tcp请求，对于每个tcp新建线程接收Message，在每一个线程里直接调用SendMessage就好了

            try {
                var serverSocket = new ServerSocket(10099);
                while (true)
                {
                    Socket socket = serverSocket.accept();
                    Thread newThread=new Thread(new MessageReceiver(socket));
                    newThread.start();
                    if(User.userlist.containsKey(flag)){
                        new Room(User.userlist.get(flag)).creatRoom();
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

    }

    public static final Object locker = new Object();

    public static void SendMessage(Message message, String username) throws IOException {
        synchronized (locker) {
            ObjectOutputStream objectOutputStream = Program.streams.get(username);
            objectOutputStream.writeObject(message);
            objectOutputStream.flush();
        }
    }
}