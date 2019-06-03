package server;

import java.io.*;
import java.net.*;


public class myRun implements Runnable{
    private Socket client;
    private Request request;
    private Response response;
    private FileInputStream fileInputStream;

    public myRun(Socket client) throws IOException {
        this.client = client;
        try {
            this.request = new Request(this.client);
            this.response = new Response(this.client);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("客户端错误");
            this.client.close();
        }

    }
    @Override
    public void run() {

        String requestMethod = request.getRequestMethod();
        if(requestMethod.equals("GET")){
            runGet();
        }else if(requestMethod.equals("POST")){
            runPost();
        }else {
            response.pushToClient(405);
        }

        }

    /**
     * 请求方法为get时运行
     */
    public void runGet(){
        String filePath = request.getUri();
        int len = 1024;
        boolean fileExit = true;
        try {
            File file = new File(filePath);
            fileExit = file.exists();
            if (fileExit) {
                fileInputStream = new FileInputStream(file);
                len = (int) file.length();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if (fileExit) {
            byte[] sendByte = new byte[len];
            try {
                fileInputStream.read(sendByte);
            } catch (IOException e) {
                e.printStackTrace();
            }
            response.addContent(sendByte);
            response.pushToClient(200);
        } else {
            statusCodeProcessing(filePath);
         }
    }

    /**
     * 请求方法为post时运行
     */
    public void runPost(){
        //暂时采用get方法
        runGet();
     }

    /**
     * 当找不到文件时对路径进行相应的操作并返回对应的状态码
     * @param filePath
     */
    public void statusCodeProcessing(String filePath){
        boolean isContainFolder = filePath.contains("/");
        boolean isFileExistsInOtherPlaces = false;
        File file;
        String newFilePath = "";
        if(isContainFolder){
            newFilePath = filePath.split("/")[1];
        }else{
           newFilePath = "resource/"+filePath;
        }
        file = new File(newFilePath);
        isFileExistsInOtherPlaces = file.exists();
        if(isFileExistsInOtherPlaces){
            if(newFilePath.contains("/")) {
                response.addContent(newFilePath.split("/")[0].getBytes());
            }else{
                response.addContent("".getBytes());
            }
            response.pushToClient(301);
        }else {
            response.pushToClient(404);
        }
    }
}
