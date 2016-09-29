package com.example.songjian.logintest;

import android.util.Log;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

import java.io.IOException;

/**
 * Created by jiayao on 2016/9/29.
 */
public class ConnectionManager {
    private static ConnectionManager instance = null;
    public XMPPTCPConnection connection = null;
    private ChatManager mChatManager = null;

    public Roster getRoster() {
        return roster;
    }

    private Roster roster = null;

    private static void createInstance() {
        instance = new ConnectionManager();
    }


    private ConnectionManager() {
        //init
    }

    public ChatManager getChatManager() {
        return mChatManager;
    }

    public static ConnectionManager shareInstance() {
        if (instance == null) {
            synchronized (ConnectionManager.class) {
                if (instance == null) {
                    createInstance();
                }
            }
        }
        return instance;
    }

    public boolean logout() {
        if (!isConnected()) {
            return false;
        }
        try {
            connection.instantShutdown();
            connection = null;
            roster = null;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isConnected() {
        Log.d("tag", connection.getUser());
        if (connection == null) {
            return false;
        }
        if (!connection.isConnected()) {
            try {
                connection.connect();
                return true;
            } catch (SmackException | IOException | XMPPException e) {
                return false;
            }
        }
        return true;
    }

    public void connect(String userName, String passWord, String host) {
        try {
            XMPPTCPConnectionConfiguration connectionConfig = XMPPTCPConnectionConfiguration.builder()
                    .setUsernameAndPassword(userName, passWord)
                    .setServiceName("127.0.0.1")
                    .setHost(host)
                    .setConnectTimeout(3000)
                    .setSendPresence(false)
                    .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                    .build();
            connection = new XMPPTCPConnection(connectionConfig);
            connection.connect().login();
            // 初始化chat manager
            if (isConnected()) {
                mChatManager = ChatManager.getInstanceFor(connection);
            }
            roster = Roster.getInstanceFor(connection);
        } catch (XMPPException e) {
            e.printStackTrace();
        } catch (SmackException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
