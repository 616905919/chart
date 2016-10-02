package com.example.songjian.logintest;

/**
 * Created by jiayao on 2016/10/2.
 */
public class LoginResultCallback {
    public void setOnConnectSuccess(onConnectSuccess mOnConnectSuccess) {
        this.mOnConnectSuccess = mOnConnectSuccess;
    }

    public void setOnConnectFail(onConnectFail mOnConnectFail) {
        this.mOnConnectFail = mOnConnectFail;
    }

    public void handleSuccess() {
        mOnConnectSuccess.run();
    }

    public void handleFail() {
        mOnConnectFail.run();
    }

    public interface onConnectSuccess {
        void run();
    }

    public interface onConnectFail {
        void run();
    }

    private onConnectSuccess mOnConnectSuccess;
    private onConnectFail mOnConnectFail;
}
