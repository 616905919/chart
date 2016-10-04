package com.example.songjian.logintest;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;


import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by songjian on 26/09/2016.
 */
public class ChatActivity extends AppCompatActivity {
    private static ConnectionManager mConnectionManager = ConnectionManager.shareInstance();

    private ListView msgListView;
    private EditText inputText;
    private Button send;
    private Button back;
    private MsgAdapter adapter;

    private String userJid;
    private Chat chat;

    public List<Msg> msgList = new ArrayList<>();
    private ChatObserver mChatObserver = new ChatObserver() {
        @Override
        public void onSuccess() {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    printMsg();
                    adapter.notifyDataSetChanged();
                }
            });
        }
    };

    /*
    private ChatManagerListener mChatManagerListener = new ChatManagerListener() {
        @Override
        public void chatCreated(Chat chat, boolean createdLocally) {
            chat.addMessageListener(new ChatMessageListener() {
                @Override
                public void processMessage(Chat chat, Message message) {
                    Log.d("RECEIVED MSG:", message.getBody());
                    //接收到消息Message之后进行消息展示处理，这个地方可以处理所有人的消息
                    String messageBody = message.getBody();
                    Msg msgReceived = new Msg(messageBody, Msg.TYPE_RECEIVED);
                    msgList.add(msgReceived);
                    adapter.notifyDataSetChanged();
                }
            });
        }
    };
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        setContentView(R.layout.activity_chat_main);

        Intent get = getIntent();
        userJid = get.getStringExtra("JID");

        //userJid = "admin@127.0.0.1";
        chat = createChat(userJid);
//        TextView tv = (TextView) findViewById(R.id.nameText);
        ((TextView) findViewById(R.id.nameText)).setText(userJid.substring(0, userJid.indexOf('@')));
        printMsg();
        //initMsgs();


        adapter = new MsgAdapter(ChatActivity.this, R.layout.msg_item, msgList);
//        adapter.notifyDataSetChanged();
        inputText = (EditText) findViewById(R.id.input_text);
        send = (Button) findViewById(R.id.send);
        back = (Button) findViewById(R.id.bBackFriendList);
        msgListView = (ListView) findViewById(R.id.msg_list_view);
        msgListView.setAdapter(adapter);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = inputText.getText().toString();
                if (!"".equals(content)) {
                    try {
                        //String self = mConnectionManager.connection.getUser(); //admin@127.0.0.1/smack 这个是发文件的JID
                        chat.sendMessage(content);
                    } catch (SmackException.NotConnectedException e) {
                        e.printStackTrace();
                    }
                    Msg msg = new Msg(content, Msg.TYPE_SEND);
                    msgList.add(msg);
                    //printMsg();
                    msgListView.setSelection(msgList.size());
                    inputText.setText("");
                    adapter.notifyDataSetChanged();
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent friendListIntent = new Intent(ChatActivity.this, FriendsList.class);
                startActivity(friendListIntent);
                finish();
            }
        });
        mConnectionManager.setChatObserver(mChatObserver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mConnectionManager.removeChatObserver();
    }

    private void initMsgs() {
        Msg msg1 = new Msg("Hello, how are you?", Msg.TYPE_RECEIVED);
        msgList.add(msg1);
        Msg msg2 = new Msg("Fine, thank you, and you?", Msg.TYPE_SEND);
        msgList.add(msg2);
        Msg msg3 = new Msg(userJid, Msg.TYPE_RECEIVED);
        msgList.add(msg3);
    }

    /*
    private Chat createChat(String recipient){
        ChatManager chatManager = getChatManager();
        Chat chat = chatManager.createChat(recipient, new ChatMessageListener() {
            @Override
            public void processMessage(Chat chat, Message message) {
                Log.d("Received: ", message.getBody());
            }
        });
    }
    */

    public Chat createChat(String jid) {
        if (mConnectionManager.isConnected()) {
            ChatManager chatManager = ChatManager.getInstanceFor(mConnectionManager.connection);
            return chatManager.createChat(jid);
        }
        throw new NullPointerException("服务器连接失败，请先连接服务器");
    }

    public void printMsg() {
        String userName = userJid.split("@")[0];
        List<String> incomingMsgList = mConnectionManager.incomingMsg.get(userName);
        if (incomingMsgList != null) {
            Iterator<String> iterator = incomingMsgList.iterator();
            while (iterator.hasNext()) {
                Msg incomingMsg = new Msg(iterator.next(), Msg.TYPE_RECEIVED);
                msgList.add(incomingMsg);
            }
            incomingMsgList.clear();
        }
    }

}
