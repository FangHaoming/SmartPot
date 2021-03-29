package com.example.navigate.ui.monitor;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.navigate.R;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MonitorFragment extends Fragment{

    private MonitorViewModel monitorViewModel;
    private SharedPreferences receive;
    private String userAcc;
    private String userPwd;
    private LineGraphSeries<DataPoint> series_light;
    private LineGraphSeries<DataPoint> series_wet;
    Button bt;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        monitorViewModel =
                ViewModelProviders.of(this).get(MonitorViewModel.class);
        View root = inflater.inflate(R.layout.fragment_monitor, container, false);
        receive= Objects.requireNonNull(getActivity()).getSharedPreferences("userInfo", 0);
        bt=(Button)root.findViewById(R.id.button);
        CalendarView calendarview = (CalendarView) root.findViewById(R.id.calendarview);
        calendarview.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {

            }
        });
        GraphView graph = root.findViewById(R.id.graph_wet);
        GraphView graph2=root.findViewById(R.id.graph_light);
        series_light=new LineGraphSeries<DataPoint>(new DataPoint[]{
                /*
                new DataPoint(30, 10),
                new DataPoint(100,20),
                new DataPoint(130,35),
                new DataPoint(200, 40),
                new DataPoint(230,30),
                new DataPoint(300,25),
                new DataPoint(500, 55),
                new DataPoint(1000,40),
                new DataPoint(1030,35),
                new DataPoint(1530, 65),
                new DataPoint(2000,60),
                new DataPoint(2230,70),
                new DataPoint(2300, 80),
                new DataPoint(2330,75),
                new DataPoint(2400,30)

                 */
        });
        series_wet = new LineGraphSeries<DataPoint>(new DataPoint[] {
                /*
                new DataPoint(30, 10),
                new DataPoint(100,20),
                new DataPoint(130,35),
                new DataPoint(200, 40),
                new DataPoint(230,30),
                new DataPoint(300,25),
                new DataPoint(500, 55),
                new DataPoint(1000,40),
                new DataPoint(1030,35),
                new DataPoint(1530, 65),
                new DataPoint(2000,60),
                new DataPoint(2230,70),
                new DataPoint(2300, 80),
                new DataPoint(2330,75),
                new DataPoint(2400,30)

                 */
        });
        //土壤湿度
        series_wet.setBackgroundColor(Color.argb(50,100,200,200));
        series_wet.setDrawBackground(true);
        series_wet.setColor(Color.argb(255,3,169,244));
        graph.addSeries(series_wet);
        graph.getViewport().setScalable(true);
        graph.setTitle("土壤湿度实时监测数据");
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(2400);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(100);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(){
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if(isValueX){
                    switch(String.valueOf((int)value).length()){
                        case 4:
                            return String.valueOf(value).substring(0,2)+":"+String.valueOf(value).substring(2,4);
                        case 3:
                            return String.valueOf(value).substring(0,1)+":"+String.valueOf(value).substring(1,3);
                        case 2:
                            return "00"+":"+String.valueOf(value).substring(0,2);
                        default:
                            return "0";
                    }
                }
                else{
                    return super.formatLabel(value,isValueX);
                }
            }
        });
        //光照强度
        series_light.setColor(Color.argb(255,255,235,59));
        series_light.setBackgroundColor(Color.argb(50,255,235,59));
        series_light.setDrawBackground(true);
        graph2.addSeries(series_light);
        graph2.getViewport().setScalable(true);
        graph2.setTitle("光照强度实时监测数据");
        graph2.getViewport().setMinX(0);
        graph2.getViewport().setMaxX(2400);
        graph2.getViewport().setMinY(0);
        graph2.getViewport().setMaxY(4000);
        graph2.getViewport().setXAxisBoundsManual(true);
        graph2.getViewport().setYAxisBoundsManual(true);
        /*
        NumberFormat nf = NumberFormat.getInstance();

        nf.setMinimumFractionDigits(2);
        nf.setMinimumIntegerDigits(2);
        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(nf, nf));

         */
        graph2.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(){
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if(isValueX){
                    switch(String.valueOf((int)value).length()){
                        case 4:
                            return String.valueOf(value).substring(0,2)+":"+String.valueOf(value).substring(2,4);
                        case 3:
                            return String.valueOf(value).substring(0,1)+":"+String.valueOf(value).substring(1,3);
                        case 2:
                            return "00"+":"+String.valueOf(value).substring(0,2);
                        default:
                            return "0";
                    }
                }
                else{
                    return super.formatLabel(value,isValueX);
                }
            }
        });

        /*
        final Handler mHandler = new Handler() {
            int x=6;
            double y=0;
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                DataPoint[] values = new DataPoint[1];
                values[0] = new DataPoint(x,y);
                x++;
                y=Math.sin(x);
                series.appendData(values[0], true, 1000);
                System.out.println("done  "+x);

            }
        };

        Timer timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                while(true){
                    try {
                        Thread.sleep(50);
                        Message msg=mHandler.obtainMessage();
                        msg.what=1;
                        mHandler.sendMessage(msg);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        },500);

         */
        bt.setOnClickListener(new View.OnClickListener() {
            int x=0;
            double y=0;
            @Override
            public void onClick(View v) {
                /*
                while(x<2000) {
                    DataPoint[] values = new DataPoint[1];
                    values[0] = new DataPoint(x, y);
                    x++;
                    y = Math.log10(x*x);
                    series_light.appendData(values[0], true, 2500);
                    System.out.println("Done: " + x);

                }

                 */
            }
        });
        if(!receive.getBoolean("isLogin",false)){
            Toast.makeText(getActivity(),"请登录！",Toast.LENGTH_LONG).show();
        }
        else if(!receive.getString("Device","").equals("")){
            sendByPost(2021,3,28,"12345");
        }
        return root;
    }

    private void sendByPost(int y,int m,int d,String device) {
        //172.16.226.68
        //10.22.32.85
        String Url = "http://172.16.42.203:8090/_war_exploded/servlet.DataServlet";
        String path = Url ;
        OkHttpClient client = new OkHttpClient();
        final FormBody formBody = new FormBody.Builder()
                .add("y",String.valueOf(y))
                .add("m",String.valueOf(m))
                .add("d",String.valueOf(d))
                .add("device", device)
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
                        DataPoint[] value_light = new DataPoint[1];
                        DataPoint[] value_wet = new DataPoint[1];
                        System.out.println("&&&&&x"+i+": "+json.getString("time"));
                        value_light[0] = new DataPoint(Integer.parseInt(json.getString("time")),json.getInt("gzqd"));
                        value_wet[0] = new DataPoint(Integer.parseInt(json.getString("time")),json.getInt("shidu"));
                        series_light.appendData(value_light[0], true, 2400);
                        series_wet.appendData(value_wet[0], true, 2400);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }



}