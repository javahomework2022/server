/**
 * 这个包是客户端需要运行的程序包.
 * @author 不要爆零小组
 * @version JDK17
 */
package com.workonline.server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

/**
 * 该类为程序主类，主线程在此运行，从中分出其他线程.
 */
public class Program {
    /**
     * 用户名与输出流对应的哈希表.
     */
    public static HashMap<String, ObjectOutputStream> streams = new HashMap<>();

    /**
     * 程序主线程，主入口.
     * @param args main方法的参数
     * @throws IOException 在得到输入输出流方法中抛出的异常
     */
    public static void main(String[] args) throws IOException {
        //主线程接收tcp请求，对于每个tcp新建线程接收Message，在每一个线程里直接调用SendMessage就好了

            try {
                var serverSocket = new ServerSocket(10099);
                while (true)
                {
                    Socket socket = serverSocket.accept();
                    Thread newThread=new Thread(new MessageReceiver(socket));
                    newThread.start();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

    }
}