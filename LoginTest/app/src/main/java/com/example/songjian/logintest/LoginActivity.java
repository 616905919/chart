package com.example.songjian.logintest;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    private ConnectionManager mConnectionManager = null;
    private LoginResultCallback mConnectResultCallback = new LoginResultCallback();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);
        mConnectionManager = ConnectionManager.shareInstance();
        final EditText etUsername = (EditText) findViewById(R.id.etUsername);
        final EditText etPassword = (EditText) findViewById(R.id.etPassword);
        final EditText etHost = (EditText) findViewById(R.id.etHost);

        Button bLogin = (Button) findViewById(R.id.bLogin);
        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //解决主进程不能进行网络连接的问题
                StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
                StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());
                String loginName = etUsername.getText().toString();
                String loginPassword = etPassword.getText().toString();
                String loginHost = etHost.getText().toString();
                initCallback();
                try {
                    mConnectionManager.connect(loginName, loginPassword, loginHost, mConnectResultCallback);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Check login details", Toast.LENGTH_LONG).show();
                }
            }
        });

        TextView tvRegisterhere = (TextView) findViewById(R.id.tvRegisterhere);
        tvRegisterhere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                LoginActivity.this.startActivity(registerIntent);
                LoginActivity.this.finish();

            }
        });
    }

    private void initCallback() {
        mConnectResultCallback.setOnConnectSuccess(new LoginResultCallback.onConnectSuccess() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "Connection complete!", Toast.LENGTH_LONG).show();
                Intent userIntent = new Intent(LoginActivity.this, UserAreaActivity.class);
                LoginActivity.this.startActivity(userIntent);
                LoginActivity.this.finish();
            }
        });
        mConnectResultCallback.setOnConnectFail(new LoginResultCallback.onConnectFail() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "Connection failed!", Toast.LENGTH_LONG).show();
            }
        });
    }
}
