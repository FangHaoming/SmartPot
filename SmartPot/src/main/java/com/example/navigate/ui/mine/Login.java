package com.example.navigate.ui.mine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.navigate.MainActivity;
import com.example.navigate.R;

import java.io.IOException;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Login extends AppCompatActivity {                 //登录界面活动

    private EditText mAccount;                        //用户名编辑
    private EditText mPwd;                            //密码编辑
    private Button mRegisterButton;                   //注册按钮
    private Button mLoginButton;                      //登录按钮
    String userAcc;
    String userPwd;
    private CheckBox mRememberCheck;
    private TextView mChangepwdText;

    private SharedPreferences login_sp;
    SharedPreferences.Editor editor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        //通过id找到相应的控件
        mAccount = (EditText) findViewById(R.id.login_edit_account); //账号
        mPwd = (EditText) findViewById(R.id.login_edit_pwd);       //密码
        mRegisterButton = (Button) findViewById(R.id.login_btn_register);
        mLoginButton = (Button) findViewById(R.id.login_btn_login);

        mChangepwdText = (TextView) findViewById(R.id.login_text_change_pwd);

        mRememberCheck = (CheckBox) findViewById(R.id.Login_Remember);
        login_sp = getSharedPreferences("userInfo", 0);  //存在本地的数据
        editor =login_sp.edit();
        //连接服务器


        String account=login_sp.getString("USER_ACC", "");
        String pwd =login_sp.getString("PASSWORD", "");
        boolean choseRemember =login_sp.getBoolean("mRememberCheck", false); //记住密码


        mRegisterButton.setOnClickListener(mListener);                      //采用OnClickListener方法设置不同按钮按下之后的监听事件
        mLoginButton.setOnClickListener(mListener);
        mChangepwdText.setOnClickListener(mListener);
    }

    View.OnClickListener mListener = new View.OnClickListener() {                  //不同按钮按下的监听事件选择
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.login_btn_register:                            //登录界面的注册按钮
                    Intent intent_Login_to_Register = new Intent(Login.this,Register.class) ;
                    startActivity(intent_Login_to_Register);
                    break;
                case R.id.login_btn_login:                              //登录界面的登录按钮
                    login();
                    break;
                case R.id.login_text_change_pwd:                             //登录界面的修改密码
                    Intent intent_Login_to_reset = new Intent(Login.this,Resetpwd.class) ;
                    startActivity(intent_Login_to_reset);
                    break;
            }
        }
    };


    private void sendByPost(String txtUserID, String txtUserPwd) {
        //172.16.226.68
        //10.22.32.85
        String Url = "http://172.16.42.203:8090/_war_exploded/servlet.LoginServlet";
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
                Toast.makeText(Login.this, "服务器连接失败", Toast.LENGTH_SHORT).show();
                Looper.loop();
                e.printStackTrace();
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                //登录成功，跳转到MainActivity.class课程表页面

                String info=response.body().string();
                System.out.println("**********"+info);
                editor.putBoolean("isLogin",true);
                editor.apply();
                if(info.equals("match")){
                    Intent intent = new Intent(Login.this, MainActivity.class);
                    //intent.putExtras(send);
                    startActivity(intent);
                }
                else if(info.equals("wrongPwd")){
                    Looper.prepare();
                    Toast.makeText(Login.this, "密码错误!", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
                else if(info.equals("miss")) {
                    Looper.prepare();
                    Toast.makeText(Login.this, "找不到该账号!", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        });
    }

    public void login() {                                              //登录按钮监听事件
        if (isUserNameAndPwdValid()) {  //检查输入合法性
            sendByPost(userAcc,userPwd);
        }
    }

    public boolean isUserNameAndPwdValid() {
        userAcc=mAccount.getText().toString().trim();
        userPwd=mPwd.getText().toString().trim();
        if (userAcc.equals("")) {
            Toast.makeText(Login.this, getString(R.string.account_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        } else if (userPwd.equals("")) {
            Toast.makeText(Login.this, getString(R.string.pwd_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(userAcc.length()>=8&&userAcc.length()<=20&&userPwd.length()>=6&&userPwd.length()<=20){
            return true;
        }
        else{
            Toast.makeText(Login.this, "请按要求输入！",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
    }


}
