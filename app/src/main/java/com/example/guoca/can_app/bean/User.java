package com.example.guoca.can_app.bean;

/**
 * Created by Guoca on 2019/7/31.
 */

public class User {
    private long id;
    private String tel=null;

    private String pass=null;

    //-1为女性，0为未设置，1为男性
    private int sex=0;

    private String name=null;
    public User() {

    }
    public User(String tel,String pass,int sex,String name) {
        this.tel=tel;
        this.pass=pass;
        this.sex=sex;
        this.name=name;
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getTel() {
        return tel;
    }
    public void setTel(String tel) {
        this.tel = tel;
    }
    public String getPass() {
        return pass;
    }
    public void setPass(String pass) {this.pass = pass;}
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }
    @Override
    public String toString() {
        return "{tel:'" + this.tel +
                "', pass:'" + this.pass +
                "',sex:'" + this.sex +
                "', name:'" + this.name+"'}";
    }

}
