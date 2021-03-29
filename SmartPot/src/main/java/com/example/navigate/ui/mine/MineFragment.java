package com.example.navigate.ui.mine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.navigate.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MineFragment extends Fragment {

    private Button mReturnButton;
    private Button Bind;
    private SharedPreferences receive;
    SharedPreferences.Editor editor;
    private EditText InputDevice;
    String userAcc;
    String userPwd;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_mine, container, false);
        InputDevice=root.findViewById(R.id.device);
        receive= Objects.requireNonNull(getActivity()).getSharedPreferences("userInfo", 0);
        editor=receive.edit();
        if(!receive.getBoolean("isLogin",false)){
            Intent intent = new Intent(getActivity(), Login.class);
            //intent.putExtras(send);
            startActivity(intent);
        }
        InputDevice.setText(receive.getString("Device",""));
        Bind=root.findViewById(R.id.bind);
        mReturnButton = (Button) root.findViewById(R.id.returnback);
        mReturnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putBoolean("isLogin",false);
                editor.putString("Device","");
                editor.apply();
                Intent intent3 = new Intent(getActivity(), Login.class);
                startActivity(intent3);
            }
        });
        Bind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString("Device",InputDevice.getText().toString().trim());
                editor.apply();
                Toast.makeText(getActivity(),"绑定成功",Toast.LENGTH_LONG).show();
            }
        });
        return root;
    }

    private void sendByPost(String userAcc) {
        //172.16.226.68
        //10.22.32.85
        String Url = "http://172.16.42.203:8090/_war_exploded/servlet.DataServlet";
        String path = Url ;
        OkHttpClient client = new OkHttpClient();
        final FormBody formBody = new FormBody.Builder()
                .add("USER_ACC", userAcc)
                .build();
        Request request = new Request.Builder()
                .url(path)
                .post(formBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                Looper.prepare();
                Toast.makeText(getActivity(), "服务器连接失败", Toast.LENGTH_SHORT).show();
                Looper.loop();
                e.printStackTrace();
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                String info=response.body().string();
                System.out.println("**********"+info);
                try {
                    JSONObject Json=new JSONObject(info);
                    for(int i=0;i<Json.getInt("len");i++){
                        JSONObject json=Json.getJSONObject("data"+i);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}