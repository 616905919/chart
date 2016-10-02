package com.example.songjian.logintest;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterGroup;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.search.ReportedData;
import org.jivesoftware.smackx.search.UserSearchManager;
import org.jivesoftware.smackx.xdata.Form;
import org.jivesoftware.smackx.search.ReportedData.Row;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class UserAreaActivity extends AppCompatActivity {
    private Context mContext;
    private ConnectionManager mConnectionManager = ConnectionManager.shareInstance();
    private Roster roster = mConnectionManager.getRoster();
    //public static UserAreaActivity UserAreaActivityInstance = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_area);

        //UserAreaActivityInstance = this;
        mContext = UserAreaActivity.this;
        final EditText etInputName = (EditText) findViewById(R.id.etInputName);

        final EditText etFriends = (EditText) findViewById(R.id.etFriends);

        //登出按钮
        final Button bSignout = (Button) findViewById(R.id.bSignout);
        bSignout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showExitDialog();
            }
        });

        //测试用
        final Button bTest = (Button) findViewById(R.id.bTest);
        bTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(UserAreaActivity.this, FriendsList.class);
                UserAreaActivity.this.startActivity(registerIntent);
                UserAreaActivity.this.finish();
            }
        });


        //添加好友按钮
        final Button bAddFriends = (Button) findViewById(R.id.bAddFriends);
        bAddFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFriendDialog();
            }
        });

        //删除好友按钮
        final Button bRemoveFriends = (Button) findViewById(R.id.bRemoveFriends);
        bRemoveFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = etInputName.getText().toString();
                if (!"".equals(userName)) {
                    if (removeFriend(userName)) {
                        Toast.makeText(mContext, "删除好友成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mContext, "删除好友失败", Toast.LENGTH_SHORT).show();
                    }
                } else
                    Toast.makeText(mContext, "输入不能为空", Toast.LENGTH_SHORT).show();

            }
        });

        final Button bFriendList = (Button) findViewById(R.id.bFriendsList);
        bFriendList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    roster.reload();
                } catch (SmackException.NotLoggedInException e) {
                    e.printStackTrace();
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                }
                Intent friendListIntent = new Intent(UserAreaActivity.this, FriendsList.class);
                UserAreaActivity.this.startActivity(friendListIntent);
                UserAreaActivity.this.finish();
                /*
                try {
                    roster.reload();
                } catch (SmackException.NotLoggedInException e) {
                    e.printStackTrace();
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                }
                etFriends.getText().clear();
                String msg = "";
                Collection<RosterEntry> entries = roster.getEntries();
                for (RosterEntry entry : entries) {
                    msg += entry.toString().split(":")[0] + '\n';
                }

                etFriends.setText(msg);
                */
            }
        });
    }

    private void showExitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        AlertDialog alert = builder.setIcon(R.mipmap.ic_launcher)
                .setTitle("T_T!")
                .setMessage("Are you sure you want to sign out?")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("sure", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(mContext, "You have signed out!", Toast.LENGTH_SHORT).show();
                        final boolean logout = mConnectionManager.logout();
                        if (logout) {
                            dialog.dismiss();
                            Intent backIntent = new Intent(UserAreaActivity.this, LoginActivity.class);
                            UserAreaActivity.this.startActivity(backIntent);
                            UserAreaActivity.this.finish();
                        } else {
                            Toast.makeText(mContext, "something wrong with the connection, it can't be cut", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).create();
        alert.show();
    }

    private void addFriendDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        final View viewAddFriend = layoutInflater.inflate(R.layout.add_friend, null);
        Dialog dialog = new AlertDialog.Builder(mContext)
                .setTitle("Add friend")
                .setView(viewAddFriend)
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText etAddFriend = (EditText) viewAddFriend.findViewById(R.id.etAddFriend);
                        String userName = etAddFriend.getText().toString();
                        String nickName = etAddFriend.getText().toString();
                        if (addFriend(userName, nickName, mConnectionManager.connection)) {
                            Toast.makeText(mContext, "adding friend succeeded", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mContext, "adding friend failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).create();
        dialog.show();
    }

    public boolean addFriend(String user, String nickName, XMPPTCPConnection connection) {
        if (mConnectionManager.isConnected()) {
            try {
                if (isUserExist(user)) {
                    roster.createEntry(user, nickName, null);
                    return true;
                } else {
                    Toast.makeText(mContext, "User doesn't exist", Toast.LENGTH_SHORT).show();
                    return false;
                }
            } catch (SmackException.NotLoggedInException e) {
                e.printStackTrace();
            } catch (SmackException.NoResponseException e) {
                e.printStackTrace();
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            } catch (XMPPException.XMPPErrorException e) {
                e.printStackTrace();
            }
        }
        throw new NullPointerException("failed to connect to the server!");
    }


    public boolean removeFriend(String user) {
        try {
            if (user.contains("@")) {
                user = user.split("@")[0];
            }
            if (isUserExist(user)) {
                RosterEntry entry = roster.getEntry(user);
                if (entry != null) {
                    roster.removeEntry(entry);
                    return true;
                } else
                    return false;
            } else {
                Toast.makeText(mContext, "User doesn't exist", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (SmackException.NotLoggedInException e) {
            e.printStackTrace();
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        }
        throw new NullPointerException("failed to connect to the server!");
    }

    /*
        public void setPresence(int code) {
            Presence presence;
            switch (code) {
                case 0:
                    presence = new Presence(Presence.Type.available);
                    try {
                        mConnectionManager.connection.sendPacket(presence);
                    } catch (SmackException.NotConnectedException e) {
                        e.printStackTrace();
                    }
                    Log.v("state", "设置在线");
                    break;
                case 1:
                    presence = new Presence(Presence.Type.available);
                    presence.setMode(Presence.Mode.chat);
                    try {
                        mConnectionManager.connection.sendPacket(presence);
                    } catch (SmackException.NotConnectedException e) {
                        e.printStackTrace();
                    }
                    Log.v("state", "设置Q我吧");
                    System.out.println(presence.toXML());
                    break;
                case 2:
                    presence = new Presence(Presence.Type.available);
                    presence.setMode(Presence.Mode.dnd);
                    try {
                        mConnectionManager.connection.sendPacket(presence);
                    } catch (SmackException.NotConnectedException e) {
                        e.printStackTrace();
                    }
                    Log.v("state", "设置忙碌");
                    System.out.println(presence.toXML());
                    break;
                case 3:
                    presence = new Presence(Presence.Type.available);
                    presence.setMode(Presence.Mode.away);
                    try {
                        mConnectionManager.connection.sendPacket(presence);
                    } catch (SmackException.NotConnectedException e) {
                        e.printStackTrace();
                    }
                    Log.v("state", "设置离开");
                    System.out.println(presence.toXML());
                    break;
                case 4:
                    Collection<RosterEntry> entries = roster.getEntries();
                    for (RosterEntry entry : entries) {
                        presence = new Presence(Presence.Type.unavailable);
                        presence.setPacketID(Packet.ID);
                    }
            }

        }
    */
    //查询所有分组
    public static List<RosterGroup> getGroups(Roster roster) {
        List<RosterGroup> grouplist = new ArrayList<RosterGroup>();
        Collection<RosterGroup> rosterGroup = roster.getGroups();
        Iterator<RosterGroup> i = rosterGroup.iterator();
        while (i.hasNext()) {
            grouplist.add(i.next());
        }
        return grouplist;
    }

    //添加分组
    public static boolean addGroup(Roster roster, String groupName) {
        try {
            roster.createGroup(groupName);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //按组查询好友
    public static List<RosterEntry> getEntriesByGroup(Roster roster,
                                                      String groupName) {
        List<RosterEntry> Entrieslist = new ArrayList<RosterEntry>();
        RosterGroup rosterGroup = roster.getGroup(groupName);
        Collection<RosterEntry> rosterEntry = rosterGroup.getEntries();
        Iterator<RosterEntry> i = rosterEntry.iterator();
        while (i.hasNext()) {
            Entrieslist.add(i.next());
        }
        return Entrieslist;
    }

    //查询所有好友
    public List<RosterEntry> getAllEntries(Roster roster) {
        List<RosterEntry> Entrieslist = new ArrayList<RosterEntry>();
        Collection<RosterEntry> rosterEntry = roster.getEntries();
        Iterator<RosterEntry> i = rosterEntry.iterator();
        while (i.hasNext()) {
            Entrieslist.add(i.next());
        }
        return Entrieslist;
    }

    //查询用户
    public List<HashMap<String, String>> searchUsers(String userName) {
        if (mConnectionManager.connection == null)
            return null;
        HashMap<String, String> user = null;
        List<HashMap<String, String>> results = new ArrayList<HashMap<String, String>>();
        System.out.println("Begin searching......" + mConnectionManager.connection.getHost() + "  " + mConnectionManager.connection.getServiceName());

        try {
            UserSearchManager usm = new UserSearchManager(mConnectionManager.connection);
            Form searchForm = usm.getSearchForm("search." + mConnectionManager.connection.getServiceName());
            Form answerForm = searchForm.createAnswerForm();
            answerForm.setAnswer("Username", true);
            answerForm.setAnswer("search", userName);
            ReportedData data = usm.getSearchResults(answerForm, "search." + mConnectionManager.connection.getServiceName());

            Iterator<Row> it = data.getRows().iterator();
            Row row = null;

            while (it.hasNext()) {
                user = new HashMap<String, String>();
                row = it.next();
                user.put("userAccount", row.getValues("jid").toString());
                //user.put("userPhote", row.getValues("userPhote").toString());
                results.add(user);
            }

        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        }
        return results;
    }

    public boolean isUserExist(String user) {
        List<HashMap<String, String>> results = searchUsers(user);
        Iterator<HashMap<String, String>> iterator = results.iterator();
        if (iterator.hasNext()) {
            return true;
        }
        return false;
    }
}
