package com.example.songjian.logintest;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterListener;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class UserAreaActivity extends AppCompatActivity {
    private Context mContext;
    private LoginActivity instance = LoginActivity.instance;
    private Roster roster = Roster.getInstanceFor(instance.connection);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_area);
        /*
        roster.addRosterListener(new RosterListener() {
            @Override
            public void entriesAdded(Collection<String> addresses) {}

            @Override
            public void entriesUpdated(Collection<String> addresses) {}

            @Override
            public void entriesDeleted(Collection<String> addresses) {}

            @Override
            public void presenceChanged(Presence presence) {
                System.out.println("Presence changed: " + presence.getFrom() + " " + presence);
            }
        });
        */
        mContext = UserAreaActivity.this;
        final EditText etInputName = (EditText) findViewById(R.id.etInputName);

        final EditText etFriends = (EditText) findViewById(R.id.etFriends);

        final Button bSignout = (Button) findViewById(R.id.bSignout);
        bSignout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showExitDialog();
            }
        });

        final Button bAddFriends = (Button) findViewById(R.id.bAddFriends);
        bAddFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = etInputName.getText().toString();
                String nickName = etInputName.getText().toString();

                if (addFriend(userName, nickName, instance.connection)) {
                    Toast.makeText(mContext, "添加好友成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, "添加好友失败", Toast.LENGTH_SHORT).show();
                }
            }
        });

        final Button bRemoveFriends = (Button) findViewById(R.id.bRemoveFriends);
        bRemoveFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = etInputName.getText().toString();
                if (!"".equals(userName)) {
                    if (removeFriend(userName, instance.connection)) {
                        Toast.makeText(mContext, "删除好友成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mContext, "删除好友失败", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                    Toast.makeText(mContext, "输入不能为空", Toast.LENGTH_SHORT).show();

            }
        });

        final Button bFriendList = (Button) findViewById(R.id.bFriendsList);
        bFriendList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Roster roster = Roster.getInstanceFor(instance.connection);//放这里
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
                        final boolean logout = instance.logout();
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

    private boolean isConnected(XMPPTCPConnection connection) {
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


    public boolean addFriend(String user, String nickName, XMPPTCPConnection connection) {
        if (isConnected(connection)) {
            try {
                roster.createEntry(user, nickName, null);
                return true;
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


    public boolean removeFriend(String user, XMPPTCPConnection connection) {
        try {
            if (user.contains("@")) {
                user = user.split("@")[0];
            }
            RosterEntry entry = roster.getEntry(user);
            if(entry!=null){
                roster.removeEntry(entry);
                return true;
            }
            else
                return false;
        } catch (SmackException.NotLoggedInException e) {
            e.printStackTrace();
            return false;
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
            return false;
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
            return false;
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
            return false;
        }
        //throw new NullPointerException("failed to connect to the server!");
    }


    public List<RosterEntry> getAllEntries(Roster roster) {
        List<RosterEntry> Entrieslist = new ArrayList<RosterEntry>();
        Collection<RosterEntry> rosterEntry = roster.getEntries();
        Iterator<RosterEntry> i = rosterEntry.iterator();
        while (i.hasNext()) {
            Entrieslist.add(i.next());
        }
        return Entrieslist;
    }
}
