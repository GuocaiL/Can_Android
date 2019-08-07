package com.example.guoca.can_app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.example.guoca.can_app.RequestHttp.HttpRequest;
import com.example.guoca.can_app.bean.User;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;


public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    User userRegister=new User();

    //注册昵称
    private EditText registerName;

    // 手机号输入框
    private EditText inputPhoneEt;

    //密码
    private EditText pass;

    //确认密码
    private EditText veri_pass;

    // 验证码输入框
    private EditText inputCodeEt;

    // 获取验证码按钮
    private Button requestCodeBtn;

    // 注册按钮
    private Button commitBtn;

    //倒计时显示   可以手动更改。
    int i = 30;

    //注册提示信息
    private String inputMessge="";

    //http请求对象
    private HttpRequest req;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        req=new HttpRequest();
        inputPhoneEt = (EditText) findViewById(R.id.login_input_phone_et);
        pass= (EditText) findViewById(R.id.pass);
        veri_pass= (EditText) findViewById(R.id.veri_pass);
        inputCodeEt = (EditText) findViewById(R.id.login_input_code_et);
        requestCodeBtn = (Button) findViewById(R.id.login_request_code_btn);
        commitBtn = (Button) findViewById(R.id.login_commit_btn);
        requestCodeBtn.setOnClickListener(this);
        commitBtn.setOnClickListener(this);
        registerName=(EditText) findViewById(R.id.register_name);


        EventHandler eventHandler = new EventHandler(){
            @Override
            public void afterEvent(int event, int result, Object data) {
                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                msg.what=2;
                handler.sendMessage(msg);
            }
        };
        //注册回调监听接口
        SMSSDK.registerEventHandler(eventHandler);
    }

    @Override
    public void onClick(View v){
//        userRegister=new User(inputPhoneEt.getText().toString(),pass.getText().toString(),0, registerName.getText().toString());
        userRegister.setTel(inputPhoneEt.getText().toString());
        userRegister.setName(registerName.getText().toString());
        userRegister.setSex(0);
        userRegister.setPass(pass.getText().toString());
        switch (v.getId()) {
            //发送验证码事件
            case R.id.login_request_code_btn:
                // 1. 判断手机号是不是11位并且看格式是否合理
                if (!judgePhoneNums(userRegister.getTel())) {
                    return;
                }
                 // 1. 通过sdk发送短信验证
                //System.out.println("正在发送验证码:"+inputPhoneEt.getText().toString()+":");
                SMSSDK.getVerificationCode("86", inputPhoneEt.getText().toString());
              //  System.out.println("sdf");

                // 2. 把按钮变成不可点击，并且显示倒计时（正在获取）
                requestCodeBtn.setClickable(false);
                requestCodeBtn.setText("发送" + i);
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
                break;

            //注册事件
            case R.id.login_commit_btn:

                // 1. 判断手机号是不是11位并且看格式是否合理
                if (!judgePhoneNums(userRegister.getTel())) {
                    return;
                }else if (!userRegister.getPass().toString().equals(veri_pass.getText().toString()))//判断两次密码是否相同
                {
                    Toast.makeText(getApplicationContext(), "密码不一致",
                            Toast.LENGTH_SHORT).show();
                }
                else if(userRegister.getPass()==null){
                    Toast.makeText(getApplicationContext(), "密码输入为空",
                            Toast.LENGTH_SHORT).show();
                }
                else if(inputCodeEt.getText().toString().length()<=0){
                    Toast.makeText(getApplicationContext(), "验证码输入格式不正确",
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    try{
                        //查询手机号和昵称有没有被注册
                        Boolean nicheng=true;
                        Boolean te=true;
                        //此处为http请求语句，得到两个标识符，昵称注册成功与否标识符，已注册得到false，否则为true,手机号同理
                        String res=null;
                        JSONObject jsonObject=null;
                        req.setRelative("/chaUser");
                        req.setS(JSONObject.toJSONString(userRegister));
                        req.setTypeMethod("sentPostJson");
                        Thread thread1=new Thread(req);
                        thread1.join();
                        thread1.start();
                        int timeout=0;
                        while (req.re==null){
                            timeout++;
                            Thread.sleep(1);
                            if(timeout>=5000){
                                Toast.makeText(getApplicationContext(), "网络错误1",
                                        Toast.LENGTH_SHORT).show();
                                break;
                            }
                        }
                        res=req.re;
                        //System.out.println("测试："+res);
                        jsonObject=JSONObject.parseObject(res);
                        nicheng=jsonObject.getBoolean("nicheng");
                        te=jsonObject.getBoolean("te");
                        if (jsonObject.getBoolean("type")){
                            //用户名和昵称都被注册，则nicheng为true、te为ture
                            if (nicheng){
                                Toast.makeText(getApplicationContext(), "用户名已被注册",
                                        Toast.LENGTH_SHORT).show();
                            }else if (te){
                                Toast.makeText(getApplicationContext(), "手机号已被注册",
                                        Toast.LENGTH_SHORT).show();
                            }else {
                                //将收到的验证码和手机号提交再次核对
                                System.out.println("//将收到的验证码和手机号提交再次核对");
                                SMSSDK.submitVerificationCode("86", userRegister.getTel(), inputCodeEt
                                        .getText().toString());
                            }
                        }else {
                            Toast.makeText(getApplicationContext(), "网络错误2",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
        }
    }
    /**
     *
     */
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == -9) {
                requestCodeBtn.setText("发送" + i);
            } else if (msg.what == -8) {
                requestCodeBtn.setText("获取验证码");
                requestCodeBtn.setClickable(true);
                i = 30;
            } else if(msg.what==2){
                int event = msg.arg1;
                int result = msg.arg2;
                Object data = msg.obj;
                Log.e("event", "event=" + event);
                if (result == SMSSDK.RESULT_COMPLETE) {
                    // 短信注册成功后，返回MainActivity,然后提示
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {//码成功
                        try {
                            req.setRelative("/userIn");
                            req.setS(JSONObject.toJSONString(userRegister));
                            req.setTypeMethod("sentPostJson");
                            Thread thread=new Thread(req);
                            thread.join();
                            thread.start();
                            int timeout=0;
                            while (req.re==null){
                                Thread.sleep(1);
                                if(timeout>=5000){
                                    Toast.makeText(getApplicationContext(), "网络错误3",
                                            Toast.LENGTH_SHORT).show();
                                    break;
                                }
                            }
                            String res=req.re;
                            JSONObject jsonObject=JSONObject.parseObject(res);
                            Boolean type=jsonObject.getBoolean("type");
                            if(type){
                                Toast.makeText(getApplicationContext(), "注册成功",
                                        Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RegisterActivity.this,
                                        MainActivity.class);
                                /*Bundle bundle = new Bundle();
                                bundle.putString("userName",inputPhoneEt.getText().toString().trim());
                                intent.putExtras(bundle);*/
                                startActivity(intent);
                            }else {
                                Toast.makeText(getApplicationContext(), "未知错误，请重新注册",
                                        Toast.LENGTH_SHORT).show();
                            }

                        }catch (Exception e){

                        }

                    } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        Toast.makeText(getApplicationContext(), "正在获取验证码",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(RegisterActivity.this,"验证码不正确",Toast.LENGTH_SHORT).show();
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