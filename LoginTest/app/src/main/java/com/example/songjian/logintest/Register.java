package com.example.songjian.logintest;

import android.nfc.Tag;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import org.jivesoftware.smack.ExceptionCallback;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import java.util.HashMap;
import java.util.Map;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;

/**
 * Created by songjian on 12/09/2016.
 */
public class Register {
    private String username;
    private String password;
    private Map<String, String> attributes = new HashMap<String, String>();
    private XMPPTCPConnection connection;

    public Register(String username, String password, Map<String, String> attributes) {
        this.username = username;
        this.password = password;
        attributes.put(username, password);
        this.attributes = attributes;
        //this.connection = connection;
    }

    private XMPPTCPConnection connect() {
        try {
            //解决主进程不能进行网络连接的问题
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());
            XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
                    .setHost("10.0.2.2")
                    .setPort(5222)
                    .setServiceName("127.0.0.1")
                    .setSecurityMode(XMPPTCPConnectionConfiguration.SecurityMode.disabled)
                    .setCompressionEnabled(false)
                    .setDebuggerEnabled(true).build();

            XMPPTCPConnection connection = new XMPPTCPConnection(config);
            connection.connect();
            return connection;
        } catch (Exception e) {
            return null;
        }
    }


    public boolean registerUser() throws SmackException.NoResponseException, XMPPException.XMPPErrorException, NotConnectedException {
        try {
            AccountManager.getInstance(connect()).createAccount(username, password, attributes);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


}
