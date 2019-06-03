package server;

import java.io.*;
import java.net.*;
import java.util.*;

public class Response {

    final private String BLANK = " ";
    final private String CRLF = "\r\n";

    private byte[] content;     //文件内容
    private StringBuilder headInfo;//头信息
    private DataOutputStream dataOutputStream;
    private int len;

    private Response(){
        headInfo = new StringBuilder();
        len = 0;
    }

    public Response(Socket client){
        this();
        try {
            dataOutputStream = new DataOutputStream(client.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
            headInfo = null;
        }
    }

    public Response(OutputStream os){
        this();
        dataOutputStream = new DataOutputStream(os);

    }

    /**
     * 添加响应内容
     * @param info
     * @return
     */
    public Response addContent(byte[] info){

        content = info;
        len = info.length;
        return this;
    }


    /**
     * 返回响应信息给客户端
     * @param code
     */
    public void pushToClient(int code){
        if (headInfo == null){
            code = 500;
        }
        try {
            creatHeadInfo(code);
            byte[] headByte = headInfo.toString().getBytes();
            int len = headByte.length;
            if(content != null){
                len = len+content.length;
            }
            byte[] all = new byte[len];
            for(int i = 0; i < len; i++){
                if(i < headByte.length){
                    all[i] = headByte[i];
                }else{
                    all[i] = content[i - headByte.length];
                }
            }
            dataOutputStream.write(all);
            dataOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 构建头信息
     * @param statusCode
     */
    private void creatHeadInfo(int statusCode){
        headInfo.append("HTTP/1.1").append(BLANK);
        headInfo.append(statusCode).append(BLANK);
        String status = "";
        switch (statusCode){
            case 200:
                status = "OK";
                break;
            case 301:
                status = "Moved Permanently";
                break;
            case 302:
                status = "Found";
                break;
            case 304:
                status = "Not Modified";
                break;
            case 404:
                status = "Not Found";
                break;
            case 405:
                status = "Method Not Allowed";
                break;
            case 500:
                status = "Internal Server Error";
                break;
            default:
                break;
        }
     headInfo.append(status).append(CRLF);
     headInfo.append( "Date:").append(new Date()).append(CRLF);
     headInfo.append("Server:").append("HttpServer/0.0.1;charset=UTF-8" ).append(CRLF);
     headInfo.append("Content-type:text/html").append(CRLF);
     headInfo.append("Content-length:").append(len).append(CRLF);
     headInfo.append(CRLF);
    }

}
