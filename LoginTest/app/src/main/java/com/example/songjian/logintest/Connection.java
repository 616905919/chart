//package com.example.songjian.logintest;
//
//
//import org.jivesoftware.smack.ConnectionConfiguration;
//import org.jivesoftware.smack.SmackException;
//import org.jivesoftware.smack.XMPPException;
//import org.jivesoftware.smack.tcp.XMPPTCPConnection;
//import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
//import org.jivesoftware.smackx.iqregister.AccountManager;
//
//import java.io.IOException;
//
///**
// * Created by songjian on 10/09/2016.
// * no use
// */
//public class Connection {
//    public String userName;
//    private String passWord;
//    private String host;
//
//    public Connection(String userName, String passWord, String host) {
//        this.userName = userName;
//        this.passWord = passWord;
//        this.host = host;
//    }
//
//    public XMPPTCPConnection connection() {
//        try {
//            XMPPTCPConnectionConfiguration connectionConfig = XMPPTCPConnectionConfiguration.builder()
//                    .setUsernameAndPassword(userName, passWord)
//                    .setServiceName("127.0.0.1")
//                    .setHost(host)
//                    .setConnectTimeout(3000)
//                    .setSendPresence(false)
//                    .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
//                    .build();
//            XMPPTCPConnection connection = new XMPPTCPConnection(connectionConfig);
//            connection.connect().login();
//            return connection;
//        } catch (XMPPException e) {
//            e.printStackTrace();
//        } catch (SmackException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//}