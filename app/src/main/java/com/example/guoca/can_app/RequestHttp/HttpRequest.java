package com.example.guoca.can_app.RequestHttp;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class HttpRequest implements Runnable{
    private String uri ="https://169.254.74.29";
    public String re = null;
    private String typeMethod=null;
    private String relative=null;
    private String s=null;

    public String getTypeMethod() {
        return typeMethod;
    }

    public void setTypeMethod(String typeMethod) {
        this.typeMethod = typeMethod;
    }

    public String getRelative() {
        return relative;
    }

    public void setRelative(String relative) {
        this.relative = relative;
    }

    public String getS() {
        return s;
    }

    public void setS(String s) {
        this.s = s;
    }

    /**
     * Get方法
     */
    public String sentGet() {
        try {
            URL url = new URL(uri + relative);
//            URL url=new URL("https://169.254.74.29");
            System.out.println(url.toString());
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setDoOutput(true); // 设置该连接是可以输出的
            connection.setRequestMethod("GET"); // 设置请求方式
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");

            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
            String line = null;
            StringBuilder result = new StringBuilder();
            while ((line = br.readLine()) != null) { // 读取数据
                result.append(line + "\n");
            }
            connection.disconnect();
            re=result.toString();
            System.out.println(re);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }finally {
            return re;
        }
    }

    /**
     * Post方法发送form表单
     */
    public void sentPostForm() {
        try {
            URL url = new URL(this.uri + this.relative);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setDoInput(true); // 设置可输入
            connection.setDoOutput(true); // 设置该连接是可以输出的
            connection.setRequestMethod("POST"); // 设置请求方式
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");

            PrintWriter pw = new PrintWriter(new BufferedOutputStream(connection.getOutputStream()));
            pw.write("code=001&name=测试");
            pw.flush();
            pw.close();

            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
            String line = null;
            StringBuilder result = new StringBuilder();
            while ((line = br.readLine()) != null) { // 读取数据
                result.append(line + "\n");
            }
            connection.disconnect();
            this.re=result.toString();
            System.out.println(this.re);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
        }
    }

    /**
     * Post方法发送json数据
     */
    public void sentPostJson() {
        HttpURLConnection urlConnection=null;

                try {
                    URL url = new URL(this.uri+this.relative);
                    System.out.println(url.toString());
                    urlConnection = (HttpURLConnection) url.openConnection();//打开http连接
                    urlConnection.setRequestMethod("POST");//设置请求的方式
                    urlConnection.setConnectTimeout(8000);//连接的超时时间
                    urlConnection.setReadTimeout(8000);//响应的超时时间
                    urlConnection.setUseCaches(false);//不使用缓存
                    urlConnection.setInstanceFollowRedirects(true);//是成员函数，仅作用于当前函数,设置这个连接是否可以被重定向
                    urlConnection.setDoInput(true);//设置这个连接是否可以写入数据
                    urlConnection.setDoOutput(true);//设置这个连接是否可以输出数据

                    urlConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");//设置消息的类型
                    urlConnection.connect();// 连接，从上述至此的配置必须要在connect之前完成，实际上它只是建立了一个与服务器的TCP连接

                    OutputStream out = urlConnection.getOutputStream();//输出流，用来发送请求，http请求实际上直到这个函数里面才正式发送出去
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));//创建字符流对象并用高效缓冲流包装它，便获得最高的效率,发送的是字符串推荐用字符流，其它数据就用字节流
                    bw.write(this.s);//把json字符串写入缓冲区中
                    bw.flush();//刷新缓冲区，把数据发送出去，这步很重要
                    out.close();
                    bw.close();//使用完关闭
                    if(urlConnection.getResponseCode()==HttpURLConnection.HTTP_OK){//得到服务端的返回码是否连接成功

                        //------------字符流读取服务端返回的数据------------
                        InputStream in = urlConnection.getInputStream();
                        BufferedReader br = new BufferedReader(new InputStreamReader(in));
                        String str = null;
                        StringBuffer buffer = new StringBuffer();
                        while((str = br.readLine())!=null){//BufferedReader特有功能，一次读取一行数据
                            buffer.append(str);
                        }
                        in.close();
                        br.close();
                        this.re=buffer.toString();
                        System.out.println(this.re);
                    }else{
                        this.re="{\"type\":false}";
                    }
                } catch (Exception e) {
                    this.re="{\"type\":false}";
                    e.printStackTrace();
                }finally{
                    urlConnection.disconnect();//使用完关闭TCP连接，释放资源
                }
            }
    /**
     * Put方法发送json数据
     */
    public void sentPutJson() {
        HttpURLConnection urlConnection=null;
        try {
            URL url = new URL(this.uri+this.relative);
            System.out.println(url.toString());
            urlConnection = (HttpURLConnection) url.openConnection();//打开http连接
            urlConnection.setRequestMethod("PUT");//设置请求的方式
            urlConnection.setConnectTimeout(8000);//连接的超U时时间
            urlConnection.setReadTimeout(8000);//响应的超时时间
            urlConnection.setUseCaches(false);//不使用缓存
            urlConnection.setInstanceFollowRedirects(true);//是成员函数，仅作用于当前函数,设置这个连接是否可以被重定向
            urlConnection.setDoInput(true);//设置这个连接是否可以写入数据
            urlConnection.setDoOutput(true);//设置这个连接是否可以输出数据

            urlConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");//设置消息的类型
            urlConnection.connect();// 连接，从上述至此的配置必须要在connect之前完成，实际上它只是建立了一个与服务器的TCP连接

            OutputStream out = urlConnection.getOutputStream();//输出流，用来发送请求，http请求实际上直到这个函数里面才正式发送出去
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));//创建字符流对象并用高效缓冲流包装它，便获得最高的效率,发送的是字符串推荐用字符流，其它数据就用字节流
            bw.write(this.s);//把json字符串写入缓冲区中
            bw.flush();//刷新缓冲区，把数据发送出去，这步很重要
            out.close();
            bw.close();//使用完关闭
            if(urlConnection.getResponseCode()==HttpURLConnection.HTTP_OK){//得到服务端的返回码是否连接成功

                //------------字符流读取服务端返回的数据------------
                InputStream in = urlConnection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String str = null;
                StringBuffer buffer = new StringBuffer();
                while((str = br.readLine())!=null){//BufferedReader特有功能，一次读取一行数据
                    buffer.append(str);
                }
                in.close();
                br.close();
                this.re=buffer.toString();
                System.out.println(this.re);
            }else{
                this.re="{\"type\":false}";
            }
        } catch (Exception e) {
            this.re="{\"type\":false}";
            e.printStackTrace();
        }finally{
            urlConnection.disconnect();//使用完关闭TCP连接，释放资源
        }
    }
    @Override
    public void run() {
        switch (this.typeMethod){
            case "sentGet":{
                sentGet();
                break;
            }
            case "sentPostForm":{
                sentPostForm();
                break;
            }
            case "sentPostJson":{
                this.sentPostJson();
                break;
            }
            case "sentPutJson":{
                this.sentPutJson();
                break;
            }
            default:{

            }
        }

    }
}