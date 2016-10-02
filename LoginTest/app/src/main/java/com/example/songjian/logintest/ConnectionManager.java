package com.example.songjian.logintest;

import android.util.Log;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jiayao on 2016/9/29.
 */
public class ConnectionManager {
    private static ConnectionManager instance = null;
    public XMPPTCPConnection connection = null;
    private ChatManager mChatManager = null;
    public Map<String, List<String>> incomingMsg = new HashMap<>();
    private LoginResultCallback mLoginResultCallback = null;
    private ChatObserver mChatObserver = null;

    public Roster getRoster() {
        return roster;
    }

    private ConnectResultCallback mConnectResult = new ConnectResultCallback() {
        @Override
        public void onSuccess() {
            mChatManager = ChatManager.getInstanceFor(connection);
            addListener();
            roster = Roster.getInstanceFor(connection);
            mLoginResultCallback.handleSuccess();
        }

        @Override
        public void onFail() {
            //链接失败log
            mLoginResultCallback.handleFail();
        }
    };
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

    public void connect(String userName, String passWord, String host, LoginResultCallback mLoginResultCallback) {
        this.mLoginResultCallback = mLoginResultCallback;
        boolean connectSuccess = false;
        try {
            XMPPTCPConnectionConfiguration connectionConfig = XMPPTCPConnectionConfiguration.builder()
                    .setUsernameAndPassword(userName, passWord)
                    .setServiceName("127.0.0.1")
                    .setHost(host)
                    .setPort(5222)
                    .setConnectTimeout(3000)
                    .setSendPresence(true)
                    .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                    .build();
            connection = new XMPPTCPConnection(connectionConfig);
            connection.connect().login();
            if (isConnected())
                connectSuccess = true;
            // 初始化chat manager
        } catch (XMPPException e) {
            e.printStackTrace();
        } catch (SmackException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connectSuccess) mConnectResult.onSuccess();
            else mConnectResult.onFail();
        }
    }

    private void addListener() {
        mChatManager.addChatListener(new ChatManagerListener() {
            @Override
            public void chatCreated(Chat chat, boolean createdLocally) {
                chat.addMessageListener(new ChatMessageListener() {
                    @Override
                    public void processMessage(Chat chat, Message message) {
                        String body = message.getBody();
                        String receiver = message.getFrom();
                        String sender = message.getTo();
                        if (sender != null && sender.contains("@"))
                            sender = sender.split("@")[0];
                        else
                            sender = "pimao";
                        Log.d("我收到了", body);
                        if (incomingMsg.containsKey(sender)) {
                            incomingMsg.get(sender).add(body);
                        } else {
                            ArrayList msgList = new ArrayList();
                            msgList.add(body);
                            incomingMsg.put(sender, msgList);
                        }
                        mChatObserver.onSuccess();
                    }
                });
            }
        });
    }

    public void setChatObserver(ChatObserver mChatObserver) {
        this.mChatObserver = mChatObserver;
    }

    public void removeChatObserver() {
        this.mChatObserver = null;
    }
}
