package com.example.guoca.can_app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.example.guoca.can_app.CalendarActivity.yearCalendar;
import com.example.guoca.can_app.RequestHttp.HttpRequest;
import com.example.guoca.can_app.adapter.GuideAdapter;
import com.example.guoca.can_app.bean.User;
import com.example.guoca.can_app.view.ClearEditText;

import java.util.ArrayList;
import java.util.List;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener,View.OnClickListener{

    private TextView zhuce,forget_pass;
    private ViewPager login_viewpager;
    private ImageButton methods[];

    //登录按钮
    private Button login_button;
    private List<View> views;
    private GuideAdapter guideAdapter;


    //手机验证码登录方式下的组件
    //手机号输入框
    private ClearEditText tel_method;
    //发送验证码按钮
    private Button sent_veri_button;
    //验证码输入框
    private EditText veri_code;

    //手机密码登录方式下的手机号和密码
    private ClearEditText login_pass,tel_clearEdittext;


    //按钮倒计时
    int i =30;

    private void initView(){
        login_viewpager=(ViewPager) findViewById(R.id.login_viewpager);
        views =new ArrayList<View>();
        views.add(LayoutInflater.from(this).inflate(R.layout.login_guide,null));
        views.add(LayoutInflater.from(this).inflate(R.layout.tel_login_guide,null));
        guideAdapter=new GuideAdapter(this,views);
        login_viewpager.setAdapter(guideAdapter);
        methods=new ImageButton[]{
                (ImageButton) findViewById(R.id.count_pass_method_white),
                (ImageButton) findViewById(R.id.count_pass_method_blue),
                (ImageButton) findViewById(R.id.veri_login_btn_white),
                (ImageButton) findViewById(R.id.veri_login_btn_blue)
        };
        zhuce= (TextView)views.get(0).findViewById(R.id.zhuce);
        forget_pass= (TextView) views.get(0).findViewById(R.id.forget_pass);
        login_pass=(ClearEditText)views.get(0).findViewById(R.id.login_pass);
        tel_clearEdittext=(ClearEditText) views.get(0).findViewById(R.id.tel_clearEdittext);
        login_button=(Button)findViewById(R.id.login_button);

        //手机验证码登录方式下的组件
        //手机号输入框
        tel_method= (ClearEditText) views.get(1).findViewById(R.id.tel_method);
        //发送验证码按钮
        sent_veri_button=(Button)views.get(1).findViewById(R.id.sent_veri_button);
        //验证码输入框
        veri_code=(EditText)views.get(1).findViewById(R.id.veri_code);

        EventHandler eventHandler = new EventHandler(){
            @Override
            public void afterEvent(int event, int result, Object data) {
                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                msg.what=0;
                handler.sendMessage(msg);
            }
        };
        //注册回调监听接口
        SMSSDK.registerEventHandler(eventHandler);
    }

    private void setLisenter(){
        //选择不同的登录方式，接口监听
        login_viewpager.setOnPageChangeListener(this);
        zhuce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent start_zhuce = new Intent(MainActivity.this,
                        RegisterActivity.class);
                startActivity(start_zhuce);
            }
        });
        forget_pass.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent start_alter_pass=new Intent(MainActivity.this,AlterPassActivity.class);
                startActivity(start_alter_pass);
            }
        });

        //注册登录事件监听接口
        login_button.setOnClickListener(this);
        //发送验证码注册监听事件
        sent_veri_button.setOnClickListener(this);
    }
    //事件处理
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //处理登录事件
            case R.id.login_button:{
                //手机号密码登录方式
                if(login_viewpager.getCurrentItem()==0){
                    JSONObject jsonObject=new JSONObject();
                    HttpRequest httpRequest=new HttpRequest();
                    User user=new User();
                    user.setTel(tel_clearEdittext.getText().toString());
                    user.setPass(login_pass.getText().toString());
                    httpRequest.setTypeMethod("sentPostJson");
                    httpRequest.setS(JSONObject.toJSONString(user));
                    httpRequest.setRelative("/userLogin");
                    try{
                        Thread thread=new Thread(httpRequest);
                        thread.join();
                        thread.start();
                        int timeout=0;
                        while (httpRequest.re==null){
                            timeout++;
                            Thread.sleep(1);
                            if(timeout>=5000){
                                Toast.makeText(getApplicationContext(), "网络错误1",
                                        Toast.LENGTH_SHORT).show();
                                break;
                            }
                        }
                        jsonObject=JSONObject.parseObject(httpRequest.re);
                    }catch (Exception e){
                        jsonObject.put("type",false);
                        e.printStackTrace();
                    }
                    if(jsonObject.getBoolean("type")){
                        if(jsonObject.getString("tel")==null){
                            Toast.makeText(getApplicationContext(), "手机号未注册，请注册",
                                    Toast.LENGTH_SHORT).show();
                        }else{
                            if(jsonObject.getString("pass").equals(login_pass.getText().toString())){
                                Intent intent=new Intent(MainActivity.this, yearCalendar.class);
                                startActivity(intent);
                            }
                            else {
                                Toast.makeText(getApplicationContext(), "密码错误",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "网络错误",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                //手机验证码登录方式
                else{
                    if(veri_code.getText().toString().length()<=0){
                        Toast.makeText(getApplicationContext(), "验证码输入格式不正确",
                                Toast.LENGTH_SHORT).show();
                    }
                    else {
                       //将收到的验证码和手机号提交再次核对
                        //System.out.println("//将收到的验证码和手机号提交再次核对");
                        SMSSDK.submitVerificationCode("86", tel_method.getText().toString(), veri_code
                                            .getText().toString());
                    }

                }
                break;

            }
            //发送验证码处理流程
            case R.id.sent_veri_button:{
                // 1. 判断手机号是不是11位并且看格式是否合理
                if (!judgePhoneNums(tel_method.getText().toString())) {
                    return;
                }
                User user=new User();
                user.setTel(tel_method.getText().toString());
//               user.setPass(login_pass.getText().toString());
//                ?\System.out.println("验证码登录方式："+user.getTel().toString());
                JSONObject jsonObject=new JSONObject();
                HttpRequest httpRequest=new HttpRequest();
                httpRequest.setTypeMethod("sentPostJson");
                httpRequest.setS(JSONObject.toJSONString(user));
                httpRequest.setRelative("/userLogin");
                try{
                    Thread thread=new Thread(httpRequest);
                    thread.join();
                    thread.start();
                    int timeout=0;
                    while (httpRequest.re==null){
                        timeout++;
                        Thread.sleep(1);
                        if(timeout>=5000){
                            Toast.makeText(getApplicationContext(), "网络错误1",
                                    Toast.LENGTH_SHORT).show();
                            break;
                        }
                    }
                    jsonObject=JSONObject.parseObject(httpRequest.re);
                }catch (Exception e){
                    jsonObject.put("type",false);
                    e.printStackTrace();
                }
                if(jsonObject.getBoolean("type")){
                    if(jsonObject.getString("tel")==null){
                        Toast.makeText(getApplicationContext(), "手机号未注册，请注册",
                                Toast.LENGTH_SHORT).show();
                    }else{
                        // 1. 通过sdk发送短信验证
                        //System.out.println("正在发送验证码:"+inputPhoneEt.getText().toString()+":");
                        SMSSDK.getVerificationCode("86", tel_method.getText().toString());
                        //  System.out.println("sdf");
                        // 2. 把按钮变成不可点击，并且显示倒计时（正在获取）
                        sent_veri_button.setClickable(false);
                        sent_veri_button.setText("发送" + i);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                for (; i > 0; i--) {
                                    handler.sendEmptyMessage(-9);
                                    if (i <= 0) {
                                        break;
                                    }
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                                handler.sendEmptyMessage(-8);
                            }
                        }).start();
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(), "网络错误",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            }
     }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        setLisenter();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if(position==0){
            methods[0].setVisibility(View.VISIBLE);
            methods[1].setVisibility(View.GONE);
            methods[2].setVisibility(View.GONE);
            methods[3].setVisibility(View.VISIBLE);
        }
        else {
            methods[0].setVisibility(View.GONE);
            methods[1].setVisibility(View.VISIBLE);
            methods[2].setVisibility(View.VISIBLE);
            methods[3].setVisibility(View.GONE);
        }

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    /**
     *
     */
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == -9) {
                sent_veri_button.setText("发送" + i);
            }
            else if (msg.what == -8) {
                sent_veri_button.setText("获取验证码");
                sent_veri_button.setClickable(true);
                i = 30;
            }
            else if(msg.what==0){
                int event = msg.arg1;
                int result = msg.arg2;
                Object data = msg.obj;
                Log.e("event", "event=" + event);
                if (result == SMSSDK.RESULT_COMPLETE) {
                    // 短信注册成功后，进入日历主页面,然后提示
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {//码成功
                        Intent intent=new Intent(MainActivity.this, yearCalendar.class);
                        startActivity(intent);
                    }
                    else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        Toast.makeText(getApplicationContext(), "正在获取验证码",
                                Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(MainActivity.this,"验证码不正确",Toast.LENGTH_SHORT).show();
                        ((Throwable) data).printStackTrace();
                    }
                }
            }
        }
    };


    /**
     * 判断手机号码是否合理
     *
     * @param phoneNums
     */
    private boolean judgePhoneNums(String phoneNums) {
        if (isMatchLength(phoneNums, 11)
                && isMobileNO(phoneNums)) {
            return true;
        }
        Toast.makeText(this, "手机号码输入有误！",Toast.LENGTH_SHORT).show();
        return false;
    }

    /**
     * 判断一个字符串的位数
     * @param str
     * @param length
     * @return
     */
    public static boolean isMatchLength(String str, int length) {
        if (str.isEmpty()) {
            return false;
        } else {
            return str.length() == length ? true : false;
        }
    }

    /**
     * 验证手机格式
     */
    public static boolean isMobileNO(String mobileNums) {
        /*
         * 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
         * 联通：130、131、132、152、155、156、185、186 电信：133、153、180、189、（1349卫通）
         * 总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
         */
        String telRegex = "[1][358]\\d{9}";// "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(mobileNums))
            return false;
        else
            return mobileNums.matches(telRegex);
    }

    @Override
    protected void onDestroy() {
        //反注册回调监听接口
        SMSSDK.unregisterAllEventHandler();
        super.onDestroy();
    }
}


