package server;

import java.io.*;
import java.net.*;


public class HttpServer {

    private ServerSocket serverSocket;


    public static void main(String[] args) throws IOException {

        HttpServer server = new HttpServer();
        server.start();

    }

    public void start(){
        try {
            serverSocket = new ServerSocket(8080);
            serverSocket.setSoTimeout(50000);
            receive();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("服务器启动失败");
        }finally {
            if (serverSocket != null){
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public void receive(){

        try {
            System.out.println("等待客户端进行连接。。。");
            while (true) {
                Socket client = serverSocket.accept();
                System.out.println("一个客户端进行了连接");
                new Thread(new myRun(client)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}
