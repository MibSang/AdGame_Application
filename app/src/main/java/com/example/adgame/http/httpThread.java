package com.example.adgame.http;

import android.content.ContentValues;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class httpThread extends Thread {
    final private String _url = "http://ec2-13-124-160-65.ap-northeast-2.compute.amazonaws.com:8080";
    private String res = "";
    private String uri;
    private ContentValues params;
    private String token;
    private String method;

    public void setParams(String uri, ContentValues params, String token, String method) {
        this.uri = uri;
        this.params = params;
        this.token = token;
        this.method = method;
    }

    public void run() {
        String str, receiveMsg = "";
        StringBuffer send_params = new StringBuffer();
        HttpURLConnection conn;
        URL url;

        if (params == null)
            send_params.append("");
        else {
            boolean isAnd = false;
            String key;
            String value;
            for (Map.Entry<String, Object> parameter : params.valueSet()) {
                key = parameter.getKey();
                value = parameter.getValue().toString();
                if (isAnd)
                    send_params.append("&");
                send_params.append(key).append("=").append(value);
                if (!isAnd)
                    if (params.size() >= 2)
                        isAnd = true;
            }
        }

        try {
            String strParams = send_params.toString();
            if (method.equals("GET"))
                url = new URL(_url + uri + "?" + strParams);
            else
                url = new URL(_url + uri);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method);
            conn.setRequestProperty("Accept-Charset", "UTF-8");
            if (!token.equals(""))
                conn.setRequestProperty("Authorization", "Bearer " + token);

            if (!method.equals("GET")) {
                OutputStream os = conn.getOutputStream();
                os.write(strParams.getBytes("UTF-8"));
                os.flush();
                os.close();
            }

            if (conn.getResponseCode() == conn.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                StringBuffer buffer = new StringBuffer();
                while ((str = reader.readLine()) != null) {
                    buffer.append(str);
                }
                receiveMsg = buffer.toString();
                System.out.println(receiveMsg);

                reader.close();
            } else {
                System.out.println(conn.getResponseCode());
            }
            conn.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        res = receiveMsg;
    }

    public String getRes() {
        return res;
    }
}
