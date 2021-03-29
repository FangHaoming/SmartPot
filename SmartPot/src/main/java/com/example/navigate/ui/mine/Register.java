package com.example.navigate.ui.mine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.navigate.R;

import java.io.IOException;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Register extends AppCompatActivity {
    private EditText mAccount;                        //用户名编辑
    private EditText mPwd;                            //密码编辑
    private EditText mPwdCheck;                       //密码编辑
    private Button mSureButton;                       //确定按钮
    private Button mCancelButton;                     //取消按钮
    String userAcc;
    String userPwd;
    private SharedPreferences login_sp;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        login_sp = getSharedPreferences("userInfo", 0);  //存在本地的数据
        editor =login_sp.edit();
        //连接服务器
        mAccount = (EditText) findViewById(R.id.resetpwd_edit_name);
        mPwd = (EditText) findViewById(R.id.resetpwd_edit_pwd_old);
        mPwdCheck = (EditText) findViewById(R.id.resetpwd_edit_pwd_new);

        mSureButton = (Button) findViewById(R.id.register_btn_sure);
        mCancelButton = (Button) findViewById(R.id.register_btn_cancel);

        mSureButton.setOnClickListener(m_register_Listener);      //注册界面两个按钮的监听事件
        mCancelButton.setOnClickListener(m_register_Listener);

    }
    View.OnClickListener m_register_Listener = new View.OnClickListener() {    //不同按钮按下的监听事件选择
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.register_btn_sure:                       //确认按钮的监听事件
                    register_check();
                    break;
                case R.id.register_btn_cancel:                     //取消按钮的监听事件,由注册界面返回登录界面
                    Intent intent_Register_to_Login = new Intent(Register.this, Login.class) ;    //切换User Activity至Login Activity
                    startActivity(intent_Register_to_Login);
                    finish();
                    break;
            }
        }
    };


    private void sendByPost(String txtUserID, String txtUserPwd) {
        //172.16.226.68
        //10.22.32.85
        String Url = "http://172.16.42.203:8090/_war_exploded/servlet.RegisterServlet";
        String path = Url ;
        OkHttpClient client = new OkHttpClient();
        final FormBody formBody = new FormBody.Builder()
                .add("USER_ACC", txtUserID)
                .add("PASSWORD", txtUserPwd)
                .build();
        Request request = new Request.Builder()
                .url(path)
                .post(formBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                Looper.prepare();
                Toast.makeText(Register.this, "服务器连接失败", Toast.LENGTH_SHORT).show();
                Looper.loop();
                e.printStackTrace();
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                //登录成功，跳转到MainActivity.class课程表页面

                String info=response.body().string();
                System.out.println("**********"+info);
                if(info.equals("success")){
                    Looper.prepare();
                    Toast.makeText(Register.this, "注册成功!", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                    Intent intent = new Intent(Register.this, MineFragment.class);
                    //intent.putExtras(send);
                    startActivity(intent);
                    finish();
                }
                else if(info.equals("fail")){
                    Looper.prepare();
                    Toast.makeText(Register.this, "注册失败!", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
                else if(info.equals("existed")) {
                    Looper.prepare();
                    Toast.makeText(Register.this, "该账号已存在!", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        });
    }

    public void register_check() {                                //确认按钮的监听事件
        if (isUserNameAndPwdValid()) {
            sendByPost(userAcc,userPwd);
        }
    }
    public boolean isUserNameAndPwdValid() {
        userAcc=mAccount.getText().toString().trim();
        userPwd=mPwd.getText().toString().trim();
        if (userAcc.equals("")) {
            Toast.makeText(this, getString(R.string.account_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        }else if (userPwd.equals("")) {
            Toast.makeText(this, getString(R.string.pwd_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        }else if(mPwdCheck.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.pwd_check_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        }else if(!userPwd.equals(mPwdCheck.getText().toString().trim())){     //两次密码输入不一样
            Toast.makeText(this, getString(R.string.pwd_not_the_same),Toast.LENGTH_SHORT).show();
            return false;
        }else if(userAcc.length()>=8&&userAcc.length()<=20&&userPwd.length()>=6&&userPwd.length()<=20){
            return true;
        }
        else{
            Toast.makeText(this, "请按要求输入！",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}
