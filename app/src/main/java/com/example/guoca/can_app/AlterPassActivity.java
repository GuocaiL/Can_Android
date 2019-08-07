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

public class AlterPassActivity extends AppCompatActivity implements View.OnClickListener{

    //手机号输入框
    private EditText forget_pass_edit;

    //验证码输入框
    private EditText edit;

    //发送验证码按钮
    private Button get_veri_pass;

    //重置密码按钮
    private Button new_pass_btn;

    //重置密码输入框
    private EditText new_pass_edit;

    //发送倒计时
    int i=30;





    //组件初始化
    private void initView(){
        //手机号输入框
        forget_pass_edit= (EditText) findViewById(R.id.forget_pass_edit);

        //验证码输入框
        edit= (EditText) findViewById(R.id.edit);

        //发送验证码按钮
        get_veri_pass= (Button) findViewById(R.id.get_veri_pass);
        get_veri_pass.setOnClickListener(this);

        //重置密码按钮
        new_pass_btn= (Button) findViewById(R.id.new_pass_btn);
        new_pass_btn.setOnClickListener(this);

        //重置密码输入框
        new_pass_edit= (EditText) findViewById(R.id.new_pass_edit);

        EventHandler eventHandler = new EventHandler(){
            @Override
            public void afterEvent(int event, int result, Object data) {
                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                msg.what=1;
                handler.sendMessage(msg);

            }
        };
        //注册回调监听接口
        SMSSDK.registerEventHandler(eventHandler);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forget_pass);
        //初始化组件
        initView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.get_veri_pass:{
                // 1. 判断手机号是不是11位并且看格式是否合理
                if (!judgePhoneNums(forget_pass_edit.getText().toString())) {
                    return;
                }
                // 1. 通过sdk发送短信验证
                SMSSDK.getVerificationCode("86", forget_pass_edit.getText().toString());

                // 2. 把按钮变成不可点击，并且显示倒计时（正在获取）
                get_veri_pass.setClickable(false);
                get_veri_pass.setText("发送" + i);
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
            }
            case R.id.new_pass_btn:{
                if(edit.getText().toString().length()<=0){
                    Toast.makeText(getApplicationContext(), "验证码输入格式不正确",
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    try{
                        //http请求
                        HttpRequest req=new HttpRequest();
                        //用户bean
                        User user=new User();
                        user.setTel(forget_pass_edit.getText().toString());
                        //查询手机号有没有被注册
                        Boolean te=true;
                        String res=null;
                        JSONObject jsonObject=null;
                        req.setRelative("/chaUser");
                        req.setS(JSONObject.toJSONString(user));
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
                        te=jsonObject.getBoolean("te");
                        if (jsonObject.getBoolean("type")){
                            //用户名和昵称都被注册，则nicheng为true、te为ture
                            if (!te){
                                Toast.makeText(getApplicationContext(), "手机号未注册，请注册",
                                        Toast.LENGTH_SHORT).show();
                            }else {
                                //将收到的验证码和手机号提交再次核对
                                System.out.println("//将收到的验证码和手机号提交再次核对");
                                SMSSDK.submitVerificationCode("86", forget_pass_edit.getText().toString(), edit
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
                break;
            }

        }

    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == -9) {
                get_veri_pass.setText("发送" + i);
            }
            else if (msg.what == -8) {
                get_veri_pass.setText("获取验证码");
                get_veri_pass.setClickable(true);
                i = 30;
            }
            else if(msg.what==1){
                int event = msg.arg1;
                int result = msg.arg2;
                Object data = msg.obj;
                Log.e("event", "event=" + event);
                if (result == SMSSDK.RESULT_COMPLETE) {
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {//码成功
                        try {
                            HttpRequest req=new HttpRequest();
                            User user=new User();
                            user.setTel(forget_pass_edit.getText().toString());
                            user.setPass( new_pass_edit.getText().toString());
                            Log.e("修改时的user",JSONObject.toJSONString(user));
                            req.setRelative("/");
                            req.setS(JSONObject.toJSONString(user));
                            req.setTypeMethod("sentPutJson");
                            Thread thread2=new Thread(req);
                            thread2.join();
                            thread2.start();
                            int timeout=0;
                            while (req.re==null){
                                Thread.sleep(1);
                                if(timeout>=5000){
                                    Toast.makeText(getApplicationContext(), "网络错误3",
                                            Toast.LENGTH_SHORT).show();
                                    break;
                                }
                            }
                            JSONObject jsonObject=JSONObject.parseObject(req.re);
                            Boolean type=jsonObject.getBoolean("type");
                            if(type){
                                Toast.makeText(getApplicationContext(), "修改成功",
                                        Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(AlterPassActivity.this,
                                        MainActivity.class);
                                /*Bundle bundle = new Bundle();
                                bundle.putString("userName",inputPhoneEt.getText().toString().trim());
                                intent.putExtras(bundle);*/
                                startActivity(intent);
                            }else {
                                Toast.makeText(getApplicationContext(), "未知错误，请重新操作",
                                        Toast.LENGTH_SHORT).show();
                            }

                        }catch (Exception e){
                            e.printStackTrace();

                        }

                    } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        Toast.makeText(getApplicationContext(), "正在获取验证码",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AlterPassActivity.this,"验证码不正确",Toast.LENGTH_SHORT).show();
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
