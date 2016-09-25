package com.example.songjian.logintest;

import android.app.Dialog;
import android.app.ExpandableListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.Toast;
import android.os.Handler;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterGroup;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.search.ReportedData;
import org.jivesoftware.smackx.search.UserSearchManager;
import org.jivesoftware.smackx.xdata.Form;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by songjian on 25/09/2016.
 */
public class FriendsList extends ExpandableListActivity {
    //private static UserAreaActivity userAreaActivityInstance = UserAreaActivity.UserAreaActivityInstance;
    private static LoginActivity instance = LoginActivity.instance;
    private Roster roster = UserAreaActivity.roster;

    /*
    * create level 1 container
    * */
    private List<Map<String, String>> groups = new ArrayList<Map<String, String>>();

    /*
    * store content, in order to show content in the list
    * */
    private List<List<Map<String, String>>> childs = new ArrayList<List<Map<String, String>>>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list_expandable);
        setListData();

        Button bBack = (Button) findViewById(R.id.bBack);
        bBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(FriendsList.this, UserAreaActivity.class);
                FriendsList.this.startActivity(registerIntent);
                FriendsList.this.finish();

                //setListData();
            }
        });

        //添加好友按钮
        Button bAddFriends = (Button) findViewById(R.id.bAddFriends);
        bAddFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                setListData();
                addFriendDialog();
            }
        });
    }

    static int a = 0;

    public void setListData() {
        groups.clear();
        childs.clear();
        try {
            roster.reload();
        } catch (SmackException.NotLoggedInException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
        String pattern = "\\[(.*)\\]";
        // Create a Pattern object
        Pattern r = Pattern.compile(pattern);

        //获得所有用户(userName, group)
        Map<String, String> users = new HashMap<String, String>(); //("stan", "friends" )
        Collection<RosterEntry> entries = roster.getEntries();
        for (RosterEntry entry : entries) {
            System.out.print(entry.toString() + '\n');
            // Create matcher object.
            Matcher m = r.matcher(entry.toString());
            String groupName = "";
            if (m.find())
                groupName = m.group(1);
            else
                groupName = "DefaultGroup";
            String userName = entry.toString().split(":")[0];

            users.put(userName, groupName);
            //msg += entry.toString().split(":")[0] + '\n';
        }

        //根据组信息,来创建一级条目
        Collection collectionGroups = users.values();
        Set setGroups = new HashSet(collectionGroups);
        Iterator iteratorGroups = setGroups.iterator();
        while (iteratorGroups.hasNext()) {
            String group = (String) iteratorGroups.next();
            //根据组信息,来创建一级条目
            Map<String, String> groupItem = new HashMap<String, String>();
            groupItem.put("group", group);
            groups.add(groupItem);

            //创建二级条目
            List<Map<String, String>> childItem = new ArrayList<Map<String, String>>();
            Iterator iterator = users.entrySet().iterator(); //users: ("stan", "friends" )
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                String groupName = (String) entry.getValue();
                String userName = (String) entry.getKey();
                if (group.equals(groupName)) {
                    Map<String, String> childItemContent = new HashMap<String, String>();
                    childItemContent.put("child", userName);
//                    childItemContent.put("child", String.valueOf(a++));
                    childItem.add(childItemContent);
                }
            }
            childs.add(childItem);
        }
//        if (getExpandableListAdapter() != null) {
//            ((SimpleExpandableListAdapter) getExpandableListAdapter()).notifyDataSetChanged();
//            return;
//        }
        SimpleExpandableListAdapter sela = new SimpleExpandableListAdapter(
                this, groups, R.layout.friend_list_group, new String[]{"group"},
                new int[]{R.id.textGroup}, childs, R.layout.friend_list_child,
                new String[]{"child"}, new int[]{R.id.textChild});
        Log.d("c_size_inFunc", String.valueOf(childs.size()));
        setListAdapter(sela);
    }


    @Override
    public boolean onChildClick(ExpandableListView parent, View v,
                                int groupPosition, int childPosition, long id) {
        Toast.makeText(
                FriendsList.this,
                "您选择了"
                        + groups.get(groupPosition).toString()
                        + "子编号"
                        + childs.get(groupPosition).get(childPosition)
                        .toString(), Toast.LENGTH_SHORT).show();
        return super.onChildClick(parent, v, groupPosition, childPosition, id);
    }

    @Override
    public boolean setSelectedChild(int groupPosition, int childPosition,
                                    boolean shouldExpandGroup) {
        return super.setSelectedChild(groupPosition, childPosition,
                shouldExpandGroup);
    }

    @Override
    public void setSelectedGroup(int groupPosition) {
        super.setSelectedGroup(groupPosition);
    }

    private void addFriendDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(FriendsList.this);
        final View viewAddFriend = layoutInflater.inflate(R.layout.add_friend, null);
        Dialog dialog = new AlertDialog.Builder(FriendsList.this)
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
                        EditText etAddFriendGroup = (EditText) viewAddFriend.findViewById(R.id.etAddFriendGroup);
                        String userName = etAddFriend.getText().toString();
                        String nickName = etAddFriend.getText().toString();
                        String groupName = etAddFriendGroup.getText().toString();
                        if ("".equals(groupName))
                            groupName = null;
                        if (addFriend(userName, nickName, groupName, instance.connection)) {
                            setListData();
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            setListData();
                                        }
                                    }, 10);
                                }
                            }).start();
                            Toast.makeText(FriendsList.this, "adding friend succeeded", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(FriendsList.this, "adding friend failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).create();
        dialog.show();
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

    public boolean addFriend(String user, String nickName, String groupName, XMPPTCPConnection connection) {
        if (isConnected(connection)) {
            try {
                if (isUserExist(user)) {
                    roster.createEntry(user, nickName, new String[]{groupName});
                    return true;
                } else {
                    Toast.makeText(FriendsList.this, "User doesn't exist", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(FriendsList.this, "User doesn't exist", Toast.LENGTH_SHORT).show();
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
    public static List<HashMap<String, String>> searchUsers(String userName) {
        if (instance.connection == null)
            return null;
        HashMap<String, String> user = null;
        List<HashMap<String, String>> results = new ArrayList<HashMap<String, String>>();
        System.out.println("Begin searching......" + instance.connection.getHost() + "  " + instance.connection.getServiceName());

        try {
            UserSearchManager usm = new UserSearchManager(instance.connection);
            Form searchForm = usm.getSearchForm("search." + instance.connection.getServiceName());
            Form answerForm = searchForm.createAnswerForm();
            answerForm.setAnswer("Username", true);
            answerForm.setAnswer("search", userName);
            ReportedData data = usm.getSearchResults(answerForm, "search." + instance.connection.getServiceName());

            Iterator<ReportedData.Row> it = data.getRows().iterator();
            ReportedData.Row row = null;

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

    public static boolean isUserExist(String user) {
        List<HashMap<String, String>> results = searchUsers(user);
        Iterator<HashMap<String, String>> iterator = results.iterator();
        if (iterator.hasNext()) {
            return true;
        }
        return false;
    }
}
