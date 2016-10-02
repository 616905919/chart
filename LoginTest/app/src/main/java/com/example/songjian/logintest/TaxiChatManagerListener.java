//package com.example.songjian.logintest;
//
//import android.util.Log;
//
//import org.jivesoftware.smack.chat.Chat;
//import org.jivesoftware.smack.chat.ChatManagerListener;
//import org.jivesoftware.smack.chat.ChatMessageListener;
//import org.jivesoftware.smack.packet.Message;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * Created by songjian on 30/09/2016.
// * nouse
// */
//public class TaxiChatManagerListener implements ChatManagerListener {
//    public static Map<String, List> incomingMsg = new HashMap<>();
//
//    @Override
//    public void chatCreated(Chat chat, boolean arg1) {
//        chat.addMessageListener(new ChatMessageListener() {
//            @Override
//            public void processMessage(Chat chat, Message message) {
//                String body = message.getBody();
//                String receiver = message.getFrom();
//                String sender = message.getTo();
//                if (sender != null && sender.contains("@"))
//                    sender = sender.split("@")[0];
//                else
//                    sender = "pimao";
//                Log.d("我收到了", body);
//                if (incomingMsg.containsKey(sender)) {
//                    incomingMsg.get(sender).add(body);
//                } else {
//                    ArrayList msgList = new ArrayList();
//                    msgList.add(body);
//                    incomingMsg.put(sender, msgList);
//                }
//            }
//        });
//    }
//
//    public static void clearMap() {
//        incomingMsg.clear();
//    }
//}
