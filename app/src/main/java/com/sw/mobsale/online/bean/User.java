package com.sw.mobsale.online.bean;


public class User {
    private  int userId;
    private  String userName;
    private  String userPwd;

    public User() {

    }

    public User(String userName, String userPwd) {

        this.userName = userName;
        this.userPwd = userPwd;
    }
    public User(int userId,String userName, String userPwd) {
        this.userId = userId;
        this.userName = userName;
        this.userPwd = userPwd;
    }

    public int getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserPwd() {
        return userPwd;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserPwd(String userPwd) {
        this.userPwd = userPwd;
    }

    @Override
    public String toString() {
        return super.toString();
    }

}
