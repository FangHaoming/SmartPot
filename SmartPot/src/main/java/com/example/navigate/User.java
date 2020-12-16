package com.example.navigate;

public class User {
    private String name;            //用户名
    private String account;       //账号
    private String passerword;   //密码
    private String[] device;    //绑定的设备
    private int water_amount;    //浇水量
    private int wet_percentage; //土壤湿度
    private Boolean ato_lamp;  //勾选自动调节亮度
    private Boolean ato_water;  //勾选自动浇水
    private int lamp_progress; //拖动条的位置
    //private String image;     //当前栽种植物的图片文件名
    private String[] image_set; //用户收藏的图鉴集
}
