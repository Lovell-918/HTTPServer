package server;

import java.io.*;
import java.net.*;
import java.util.*;

public class Request {
    /**
     * 处理请求信息
     */

    final private String CRLF = "\r\n";

    private String requestInfo;     //请求信息
    private String requestMethod;   //请求方法
    private String uri;             //请求的文件地址
    private String query;           //请求参数
    private Map<String,List<String>> parameterMap;  //存储请求参数的map

    public String getRequestMethod() {
        return requestMethod;
    }

    public String getUri() {
        return uri;
    }

    public String getQuery() {
        return query;
    }

    public Request(Socket client) throws IOException {
        this(client.getInputStream());
    }

    public Request(InputStream inputStream){
        try {
            parameterMap = new HashMap<String, List<String>>();
            byte[] requestByte = new byte[1024*1024];
            int len = inputStream.read(requestByte);
            this.requestInfo = new String(requestByte,0,len);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        parseRequestInfo();
    }

    public String[] getParameterValues(String key){
        List<String> list = this.parameterMap.get(key);
        if(list == null||list.size()<1){
            return null;
        }
        return list.toArray(new String[0]);
    }

    private void parseRequestInfo(){
        this.requestMethod = this.requestInfo.substring(0,this.requestInfo.indexOf("/")).trim();
        this.uri = this.requestInfo.substring(this.requestInfo.indexOf("/")+1,this.requestInfo.indexOf("HTTP/")).trim();
        int queryIndex = this.uri.indexOf("?");
        if(queryIndex >= 0){        //存在请求参数
            this.uri = this.uri.split("\\?")[0];
            this.query = this.uri.split("\\?")[1];
        }

        if(requestMethod.equals("POST")){
            String qStr = this.requestInfo.substring(this.requestInfo.lastIndexOf(CRLF)).trim();
            if (query == null) {
                query = qStr;
            }else{
                query += "&" + qStr;
            }
        }
        query = query == null?"":query;
        convertMap();
    }

    private void convertMap(){
        String[] keyAndValues = this.query.split("&");
        for (String oneKeyAndValues:keyAndValues){
            String[] kv = oneKeyAndValues.split("=");
            kv = Arrays.copyOf(kv,2);
            String key = kv[0];
            String value = null;
            try {
                value = kv[1] == null? null:URLDecoder.decode(kv[1],"UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if(!parameterMap.containsKey(key)){
                parameterMap.put(key,new ArrayList<String>());
            }
            parameterMap.get(key).add(value);
        }
    }
}
