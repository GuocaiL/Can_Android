package com.example.guoca.can_app.bean;

/**
 * Created by Guoca on 2019/7/7.
 */

public class RegitsterBean {

    private String password;
    private String tel;
    private String sex;
    private String nicheng;
    public RegitsterBean(String nicheng,String password,String tel,String sex){
        this.nicheng=nicheng;
        this.password=password;
        this.tel=tel;
        this.sex=sex;
    }

    public String getNicheng() {
        return nicheng;
    }

    public String getPassword() {
        return password;
    }

    public String getTel() {
        return tel;
    }

    public String getSex() {
        return sex;
    }


    public void setPassword(String password) {
        this.password = password;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public void setNicheng(String nicheng) {
        this.nicheng = nicheng;
    }
}
