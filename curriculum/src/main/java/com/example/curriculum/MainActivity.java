package com.example.curriculum;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private GridLayout gridLayout;
    //private Bundle receive;

    private Lesson[] lessons1; //解析好的课程信息
    private  int lessonCount;   //一个学期内总课程数
    private Map<String,Object>[] lesson1;
    private SharedPreferences receive;
    private SharedPreferences.Editor editor;
    private int k;
    private int[] a;

    //初始化视图
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        receive=getSharedPreferences("data", Context.MODE_PRIVATE);
        editor=receive.edit();

        //editor.putString("s","s");
        ActionBar actionBar = getSupportActionBar();
        /*
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

         */
        //receive=this.getIntent().getExtras();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setContentView(R.layout.activity_main);
        gridLayout=(GridLayout)findViewById(R.id.gridlayout);
        addWholeLesson();
    }

    //创建课程表
    public void addWholeLesson(){
        Calendar now=Calendar.getInstance();
        String xn;
        String xq;
        if(now.get(Calendar.MONTH)>=9) {
            xn = now.get(Calendar.YEAR) + "-" + (now.get(Calendar.YEAR) + 1)+"学年";
            xq="1";
        }
        else{
            xn = (now.get(Calendar.YEAR)-1) + "-" + now.get(Calendar.YEAR)+"学年";
            xq="2";
        }
        if(receive.getBoolean("isLoad",false)==false){
            sendByPost(receive.getString("txtUserID",""),receive.getString("txtUserPwd",""),xn,xq);
            while(lessonCount==0){}
        }
        if(receive.getString("info","")!=null){
            readResponse();
            k=0;
            a=new int[20];
            if(receive.getString("color","")==""){
                Map<String,Object>map=new HashMap<>();
                //创建随机颜色标号
                Random random=new Random();
                int count=0;
                while(count<20){
                    boolean flag=true;
                    int r=random.nextInt(27);
                    for(int m=0;m<20;m++){
                        if(r==a[m]){
                            flag=false;
                            break;
                        }
                    }
                    if(flag){
                        a[count]=r;
                        map.put("c"+count,String.valueOf(r));
                        count++;
                    }
                }
                String json=JSON.toJSONString(map);
                editor.putString("color",json);
                editor.apply();
            }

            String color1 =receive.getString("color","");
            Map<String,Object> objectMap= JSON.parseObject(color1,Map.class);
            for(int i=0;i<a.length;i++){
                a[i]=Integer.parseInt(String.valueOf(objectMap.get("c"+i)));
            }
            for(int i = 0; i<lessonCount; i++) {
                for(int j=0;j<7;j++) {
                    String time = (String) lesson1[i].get(String.valueOf(j));
                    if (!time.contains("null")&&lessons1[i].time.during[0]>=3) { //这里只显示第三周开始的课程
                        addLesson(lessons1[i], j);

                    }
                }
                k++;

            }
        }

    }

    //获取课程信息方法
    private void sendByPost(String txtUserID, String txtUserPwd, String xn, String xq) {
        //172.16.226.68
        String Url = "http://172.16.226.68:8080/_war_exploded/servlet.timeTableServlet";
        String path = Url ;
        OkHttpClient client = new OkHttpClient();

        final FormBody formBody = new FormBody.Builder()
                .add("txtUserID", txtUserID)
                .add("txtUserPwd", txtUserPwd)
                .add("xn",xn)
                .add("xq",xq)
                .build();
        /*
        final FormBody formBody = new FormBody.Builder()
                .add("name","乒乓球")
                .add("no","12221")
                .add("teacher","熊志")
                .add("classroom","E301")
                .add("duringb","3")
                .add("duringe","18")
                .add("time1","12")
                .add("time2","67")
                .add("credit","2")
                .add("note","明天就要交作业了")
                .add("txtUserID", txtUserID)
                .add("txtUserPwd", txtUserPwd)
                .add("xn",xn)
                .add("xq",xq)
                .build();
         */



        Request request = new Request.Builder()
                .url(path)
                .post(formBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                String info=response.body().string();
                editor.putString("info",info);
                editor.putBoolean("isLoad",true);
                editor.apply();
                lessonCount=1;
                //System.out.println("*************"+info);

            }
        });
    }

    //解析response返回的课程信息(Json格式)，使之用来生成课程表
    private void readResponse(){
        String info =receive.getString("info","");
        Map<String,Object> objectMap= JSON.parseObject(info,Map.class);
        Map<String,Object>[] lesson=new Map[Integer.parseInt((String)(objectMap.get("length")))];
        Lesson[] lessons=new Lesson[Integer.parseInt((String)(objectMap.get("length")))];
        //char[][] temp=new char[7][];
        for(int i = 0; i<Integer.parseInt((String)(objectMap.get("length"))); i++){
            lesson[i]=JSON.parseObject(String.valueOf(objectMap.get("lesson"+i)),Map.class);
            lessons[i]=new Lesson();
            lessons[i].name=String.valueOf(lesson[i].get("name"));
            lessons[i].no=String.valueOf(lesson[i].get("no"));
            lessons[i].teacher=String.valueOf(lesson[i].get("teacher"));
            lessons[i].classroom=String.valueOf(lesson[i].get("classroom"));
            lessons[i].credit=String.valueOf(lesson[i].get("credit"));
            String temp[]=String.valueOf(lesson[i].get("during")).split("-");
            lessons[i].time.during[0]=Integer.parseInt(temp[0]);
            lessons[i].time.during[1]=Integer.parseInt(temp[1]);
            lessons[i].time.count=0;
            for(int j=0;j<7;j++) {
                String time = (String) lesson[i].get(String.valueOf(j));
                String timeList[] = time.split("");
                if (!time.contains("null")) {
                    lessons[i].time.count++;
                    for (int k = 0; k < timeList.length; k++) {
                        if (timeList[k].equals("A")) {
                            timeList[k] = "11";
                        }
                        if (timeList[k].equals("B")) {
                            timeList[k] = "12";
                        }
                        if (timeList[k].equals("C")) {
                            timeList[k] = "13";
                        }
                        //System.out.print(timeList[k]+" ");
                    }
                    //System.out.println("\n");
                    if (time.contains("单")) {
                        lessons[i].time.isOddWeek = 1;

                        lessons[i].time.begin[j] = Integer.parseInt(timeList[2]);
                        lessons[i].time.last = timeList.length - 2;
                    } else if (time.contains("双")) {
                        lessons[i].time.isOddWeek = 0;
                        lessons[i].time.begin[j] = Integer.parseInt(timeList[2]);
                        lessons[i].time.last = timeList.length - 2;
                    } else {
                        lessons[i].time.isOddWeek = 2;
                        lessons[i].time.begin[j] = Integer.parseInt(timeList[1]);
                        lessons[i].time.last = timeList.length-1;
                    }
                    //System.out.println(lessons[i].time.begin[j]);
                }
            }
        }
        lessons1=lessons;
        lesson1=lesson;
        lessonCount=Integer.parseInt(String.valueOf(objectMap.get("length")));
    }

    //创建课程方法，j 是星期几
    private void addLesson(Lesson lessons,int j){

        for(int i=0;i<lessons.time.count;i++){
            //设置课程样式
            TextView lessonTag=new TextView(MainActivity.this);
            lessonTag.setTextAppearance(R.style.grid3);
            lessonTag.setGravity(Gravity.CENTER); //居中
            String[] color={"#E6862617","#E6C5708B","#E6D4C4B7","#E6EBB10D","#E6ED5126","#E6BACCD9","#E696C24E","#E6E3BD8D","#E6F4D3DC","#E6E69189","#E6F051E4","#E61CA3FF","#E60EE8BD","#E6B7AE8F","#E6F27635","#E6F8BC31","#E6EB8A3A","#E6815C94","#E68A6913","#E615559A","#E6D2D97A","#E6EA8958","#E6EEB8C3","#E6F7DE98","#E6EF475D","#E6C27C88","#E6C6DFC8"};
            lessonTag.setBackgroundColor(Color.parseColor(color[a[k]]));
            lessonTag.setText(lessons.name+"\n"+"@"+lessons.classroom);

            //lessonTag.setOnClickListener(); //给每个课程添加点击事件，打开编辑ddl
            GridLayout.Spec rowSpec = GridLayout.spec(lessons.time.begin[j],lessons.time.last);
            GridLayout.Spec columnSpec=GridLayout.spec(j+1);
            GridLayout.LayoutParams params=new GridLayout.LayoutParams(rowSpec,columnSpec);//设置添加的课程行与列
            params.setGravity(Gravity.FILL);
            params.width=0;
            params.height=0;
            params.setMargins(1,1,1,1);
            gridLayout.addView(lessonTag,params);
        }


    }

    //注销登录，返回登录界面
    private void logout(){
        Intent intent=new Intent(MainActivity.this,Login.class);
        startActivity(intent);
        finish();
    }

    //创建菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_top_right,menu);
        return super.onCreateOptionsMenu(menu);
    }
    //菜单事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.add:
                showAddDialog();
                break;
            case android.R.id.home:
                this.finish(); // back button
            case R.id.delete:
                logout(); //退出登录，仅作调试用
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    //显示添加课程对话框
    public void showAddDialog(){
        LayoutInflater factory= LayoutInflater.from(this);
        final View view=factory.inflate(R.layout.add_dialog,null);
        final EditText Ename=(EditText)findViewById(R.id.Ename);
        final EditText Eroom=(EditText)findViewById(R.id.Eroom);
        AlertDialog.Builder add=new AlertDialog.Builder(MainActivity.this);
        add.setTitle("添加课程");
        add.setView(view);
        add.setPositiveButton("确定",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this,"确定", Toast.LENGTH_SHORT).show();
                //getsource();
            }
        });
        add.setNegativeButton("取消",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        add.show();
    }





}