package com.example.navigate.ui.mine;

import android.content.Intent;
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

public class Resetpwd extends AppCompatActivity {
    private EditText mAccount;                        //用户名编辑
    private EditText mPwd_old;                            //密码编辑
    private EditText mPwd_new;                            //密码编辑
    private EditText mPwdCheck;                       //密码编辑
    private Button mSureButton;                       //确定按钮
    private Button mCancelButton;                     //取消按钮
    private String userAcc;
    private String userPwd_OLD;
    private String userPwd_NEW;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resetpwd);
//        layout.setOrientation(RelativeLayout.VERTICAL).
        mAccount = (EditText) findViewById(R.id.resetpwd_edit_name);
        mPwd_old = (EditText) findViewById(R.id.resetpwd_edit_pwd_old);
        mPwd_new = (EditText) findViewById(R.id.resetpwd_edit_pwd_new);
        mPwdCheck = (EditText) findViewById(R.id.resetpwd_edit_pwd_check);

        mSureButton = (Button) findViewById(R.id.resetpwd_btn_sure);
        mCancelButton = (Button) findViewById(R.id.resetpwd_btn_cancel);

        mSureButton.setOnClickListener(m_resetpwd_Listener);      //注册界面两个按钮的监听事件
        mCancelButton.setOnClickListener(m_resetpwd_Listener);
        //mCancelButton.setOnClickListener(m_resetpwd_Listener);

    }
    View.OnClickListener m_resetpwd_Listener = new View.OnClickListener() {    //不同按钮按下的监听事件选择
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.resetpwd_btn_sure:                       //确认按钮的监听事件
                    resetpwd_check();
                    break;
                case R.id.resetpwd_btn_cancel:                     //取消按钮的监听事件,由注册界面返回登录界面
                    Intent intent_Resetpwd_to_Login = new Intent(Resetpwd.this, Login.class) ;    //切换Resetpwd Activity至Login Activity
                    startActivity(intent_Resetpwd_to_Login);
                    finish();
                    break;
            }
        }
    };

    private void sendByPost(String txtUserID, String password_old,String password_new) {
        //172.16.226.68
        //10.22.32.85
        String Url = "http://172.16.226.68:8080/servlet.loginServlet";
        String path = Url ;
        OkHttpClient client = new OkHttpClient();
        final FormBody formBody = new FormBody.Builder()
                .add("USER_ACC", txtUserID)
                .add("PASSWORD_OLD", password_old)
                .add("PASSWORD_NEW",password_new)
                .build();
        Request request = new Request.Builder()
                .url(path)
                .post(formBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                Looper.prepare();
                Toast.makeText(Resetpwd.this, "服务器连接失败", Toast.LENGTH_SHORT).show();
                Looper.loop();
                e.printStackTrace();
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                //登录成功，跳转到MainActivity.class课程表页面

                String info=response.body().string();
                if(info.equals("success\r\n")){
                    Looper.prepare();
                    Toast.makeText(Resetpwd.this, "注册成功!", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                    Intent intent = new Intent(Resetpwd.this, MineFragment.class);
                    //intent.putExtras(send);
                    startActivity(intent);
                    finish();
                }
                else if(info.equals("fail\r\n")){
                    Looper.prepare();
                    Toast.makeText(Resetpwd.this, "重置密码失败!", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
                else if(info.equals("wrongPwd\r\n")) {
                    Looper.prepare();
                    Toast.makeText(Resetpwd.this, "密码错误!", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
                else if(info.equals("miss\r\n")) {
                    Looper.prepare();
                    Toast.makeText(Resetpwd.this, "找不到该账号!", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        });
    }

    public void resetpwd_check() {                                //确认按钮的监听事件
        if (isUserNameAndPwdValid()) {
            sendByPost(userAcc,userPwd_OLD,userPwd_NEW);
        }
    }


    public boolean isUserNameAndPwdValid() {
        userAcc=mAccount.getText().toString().trim();
        userPwd_OLD=mPwd_new.getText().toString().trim();
        userPwd_NEW=mPwd_new.getText().toString().trim();
        if (mAccount.getText().toString().trim().equals("")) {
            Toast.makeText(this, "账号为空，请重新输入！",Toast.LENGTH_SHORT).show();
            return false;
        } else if (mPwd_old.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.pwd_empty),Toast.LENGTH_SHORT).show();
            return false;
        } else if (mPwd_new.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.pwd_new_empty),Toast.LENGTH_SHORT).show();
            return false;
        }else if(mPwdCheck.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.pwd_check_empty),Toast.LENGTH_SHORT).show();
            return false;
        }else if(!mPwd_new.getText().toString().trim().equals(mPwdCheck.getText().toString().trim())){     //两次密码输入不一样
            Toast.makeText(this, "两次密码输入不一样,请重新输入！",Toast.LENGTH_SHORT).show();
            return false;
        }else if(userAcc.length()>=8&&userAcc.length()<=20&&mPwd_new.getText().toString().trim().length()>=6&&mPwd_new.getText().toString().trim().length()<=20){
            return true;
        }
        else{
            Toast.makeText(this, "请按要求输入！",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
    }

}

