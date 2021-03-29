package com.example.navigate.ui.control;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.navigate.R;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Objects;

public class ControlFragment extends Fragment{
    private ConnectThread connectThread;
    private EditText Set_wet;
    private EditText Set_water;
    private TextView Now_temp;
    private TextView Now_light;
    private TextView Now_wet;
    private SeekBar SeekBar;
    private Switch Auto_light;
    private Switch Auto_water;
    private Button Water;
    private Button Save;
    private String action;
    private String user;
    private String device;
    private JSONObject description;
    private SharedPreferences receive;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_control, container, false);
        Water = root.findViewById(R.id.waterBtn);
        Now_light = root.findViewById(R.id.now_light);
        Now_wet = root.findViewById(R.id.now_wet);
        SeekBar = root.findViewById(R.id.seekBar);
        Auto_light = root.findViewById(R.id.auto_light);
        Auto_water = root.findViewById(R.id.auto_water);
        Set_water = root.findViewById(R.id.set_water);
        Set_wet = root.findViewById(R.id.set_wet);
        Save=root.findViewById(R.id.save);
        receive= Objects.requireNonNull(getActivity()).getSharedPreferences("userInfo", 0);
        if(!receive.getBoolean("isLogin",false)){
            Toast.makeText(getActivity(),"请登录",Toast.LENGTH_LONG).show();
        }
        else{
            //连接服务器
            connectThread = new ConnectThread();
            connectThread.start();
            //发送连接信息
            JSONObject json_appRegister=new JSONObject();
            try {
                json_appRegister.put("action","appRegister");
                json_appRegister.put("user","12345");
                //json_setLight.put("device","74-70-FD-70-4A-90");
                //json_setLight.put("description",progress);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            final String content_appRegister=json_appRegister.toString();
            //发送数据

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (connectThread.socket == null) { }
                        connectThread.socket.getOutputStream().write(content_appRegister.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            //为“浇水”按钮添加点击事件
            Water.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    JSONObject json_setWater=new JSONObject();
                    try {
                        json_setWater.put("action","setWater");
                        json_setWater.put("user","12345");
                        json_setWater.put("device","74-70-FD-70-4A-90");
                        json_setWater.put("description",Set_water.getText());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    final String content_water=json_setWater.toString();
                    //发送数据
                    if (connectThread.socket != null) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    connectThread.outputStream.write(content_water.getBytes());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                }
            });
            //为“保存”按钮添加点击事件
            Save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    JSONObject json_setWet=new JSONObject();
                    try {
                        json_setWet.put("action","setWet");
                        json_setWet.put("user","12345");
                        json_setWet.put("device","74-70-FD-70-4A-90");
                        json_setWet.put("description",Set_wet.getText());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    final String content_wet=json_setWet.toString();
                    //发送数据
                    if (connectThread.socket != null) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    connectThread.outputStream.write(content_wet.getBytes());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                }
            });
            //灯自动开关
            Auto_light.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked)
                    {
                        JSONObject json_auto_light_on=new JSONObject();
                        try {
                            json_auto_light_on.put("action","auto_light_on");
                            json_auto_light_on.put("user","12345");
                            json_auto_light_on.put("device","74-70-FD-70-4A-90");
                            json_auto_light_on.put("description","on");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        final String content_auto_light_on=json_auto_light_on.toString();
                        //发送数据
                        if (connectThread.socket != null) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        connectThread.outputStream.write(content_auto_light_on.getBytes());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();
                        }
                    }
                    else
                    {
                        JSONObject json_auto_light_off=new JSONObject();
                        try {
                            json_auto_light_off.put("action","auto_light_off");
                            json_auto_light_off.put("user","12345");
                            json_auto_light_off.put("device","74-70-FD-70-4A-90");
                            json_auto_light_off.put("description","off");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        final String content_auto_light_off=json_auto_light_off.toString();
                        //发送数据
                        if (connectThread.socket != null) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        connectThread.outputStream.write(content_auto_light_off.getBytes());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();
                        }
                    }
                }
            });
            //自动浇水开关
            Auto_water.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked)
                    {
                        JSONObject json_auto_water_on=new JSONObject();
                        try {
                            json_auto_water_on.put("action","auto_water_on");
                            json_auto_water_on.put("user","12345");
                            json_auto_water_on.put("device","74-70-FD-70-4A-90");
                            json_auto_water_on.put("description","on");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        final String content_auto_water_on=json_auto_water_on.toString();
                        //发送数据
                        if (connectThread.socket != null) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        connectThread.outputStream.write(content_auto_water_on.getBytes());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();
                        }
                    }
                    else
                    {
                        JSONObject json_auto_water_off=new JSONObject();
                        try {
                            json_auto_water_off.put("action","auto_water_off");
                            json_auto_water_off.put("user","12345");
                            json_auto_water_off.put("device","74-70-FD-70-4A-90");
                            json_auto_water_off.put("description","off");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        final String content_auto_water_off=json_auto_water_off.toString();
                        //发送数据
                        if (connectThread.socket != null) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        connectThread.outputStream.write(content_auto_water_off.getBytes());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();
                        }
                    }
                }
            });


            //为拖动条添加监听事件
            SeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    JSONObject json_setLight=new JSONObject();
                    try {
                        json_setLight.put("action","setLight");
                        json_setLight.put("user","12345");
                        json_setLight.put("device","74-70-FD-70-4A-90");
                        json_setLight.put("description",progress);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    final String content_light=json_setLight.toString();
                    //发送数据
                    if (connectThread.socket != null) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    connectThread.outputStream.write(content_light.getBytes());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                    //Toast.makeText(getActivity(), "触碰SeekBar", Toast.LENGTH_SHORT).show();
                }
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                    //Toast.makeText(getActivity(), "放开SeekBar", Toast.LENGTH_SHORT).show();
                }
            });
        }
        return root;
    }


    //创建socket线程类
    public class ConnectThread extends Thread {
        Socket socket = null;        //定义socket
        OutputStream outputStream = null;    //定义输出流（发送）
        InputStream inputStream = null;    //定义输入流（接收）

        public void run() {
            try {
                //用InetAddress方法获取ip地址
                //InetAddress ipAddress = InetAddress.getByName(et_ip.getText().toString());
                //int port = Integer.valueOf(et_port.getText().toString());        //获取端口号
                socket = new Socket("192.168.43.172", 12345);
                //192.168.43.172
                //172.16.226.68
                //连接失败
                //获取输出流
                outputStream = socket.getOutputStream();
                //接收数据
                while (true) {
                    final byte[] buffer = new byte[1024];//创建接收缓冲区
                    inputStream = socket.getInputStream();
                    //BufferedReader br=new BufferedReader(new InputStreamReader(inputStream));
                     int len = inputStream.read(buffer);//数据读出来，并且返回数据的长度
                    byte[] bu=new byte[len];
                    System.arraycopy(buffer,0,bu,0,len);
                    String content=new String(bu);
                    try {
                        JSONTokener json = new JSONTokener(content);
                        JSONObject one = (JSONObject) json.nextValue();
                        action=one.getString("action");
                        user=one.getString("user");
                        device=one.getString("device");
                        description = one.getJSONObject("description");
                        Now_wet.setText(description.getString("wet"));
                        Now_light.setText(description.getString("light"));
                        Now_temp.setText(description.getString("temp"));
                        SeekBar.setProgress(Integer.parseInt(description.getString("lamp")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    //断开连接
    @Override
    public void onPause() {
        super.onPause();
        if(receive.getBoolean("isLogin",false)){
            JSONObject json_close = new JSONObject();
            try {
                json_close.put("action", "close");
                json_close.put("actor", "users");
                //json_setLight.put("device","74-70-FD-70-4A-90");
                //json_setLight.put("description",progress);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            final String content_close = json_close.toString();
            //发送数据
            if (connectThread.socket != null) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            while(connectThread.socket.getOutputStream()==null){}
                            connectThread.socket.getOutputStream().write(content_close.getBytes());
                            connectThread.socket.getOutputStream().flush();
                            connectThread.socket.close();
                            connectThread.socket = null;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        }

    }

}