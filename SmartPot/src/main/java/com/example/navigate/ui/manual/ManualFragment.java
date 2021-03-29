package com.example.navigate.ui.manual;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.navigate.CrawlFlowersData;
import com.example.navigate.ListAdapter;
import com.example.navigate.R;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.loader.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ManualFragment extends Fragment{

    View root;
    private ImageView img;
    private TextView content;
    private JSONObject json;
    private Banner mBanner;
    private MyImageLoader mImageLoader;
    private ArrayList<String> imagePath;
    private ArrayList<String> imageTitle;
    private ListView listView;
    private ListAdapter adapter;
    private ArrayList<String[]> list;
    private SharedPreferences receive;
    private SharedPreferences.Editor editor;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.draw_layout, container, false);
        content=root.findViewById(R.id.content);
        mBanner = root.findViewById(R.id.banner);
        listView=root.findViewById(R.id.listview);
        imagePath = new ArrayList<>();
        imageTitle = new ArrayList<>();
        mImageLoader = new MyImageLoader();
        receive= Objects.requireNonNull(getActivity()).getSharedPreferences("userInfo", 0);
        editor =receive.edit();

        final DrawerLayout drawerLayout=root.findViewById(R.id.drawer_layout);
        final Toolbar toolbar=root.findViewById(R.id.toolbar);
        final String contextID[]={"菊花","梅花","桃花"};
        adapter=new ListAdapter(getContext());
        new Thread(new Runnable() {
            @Override
            public void run() {
                list=getNames(100);
                adapter.setList(list);
                System.out.println("&*&**2"+list.get(0)[0]);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                        listView.setAdapter(adapter);
                        System.out.println("&*&**"+list.get(0)[0]);
                    }
                });
                //listView.setBackgroundColor(Color.argb(255,255,255,255));
            }
        }).start();
        show();
        //ArrayAdapter<String> adapter=new ArrayAdapter<String>(Objects.requireNonNull(getContext()),android.R.layout.simple_list_item_single_choice,str);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                editor.putString("Plant",list.get(position)[1]);
                editor.commit();
                imagePath = new ArrayList<>();
                show();
                drawerLayout.closeDrawer(Gravity.START);
            }
        });
        toolbar.setNavigationIcon(R.drawable.ic_dehaze_black_24dp);
        toolbar.setTitle("图鉴");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.START);
            }
        });



        return root;
    }

    private void show(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {

                    json=CrawlFlowersData.getData(receive.getString("Plant",""));
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                content.setText(Html.fromHtml(json.getString("content")));
                                imagePath.add(json.getString("img0"));
                                imagePath.add(json.getString("img1"));
                                imagePath.add(json.getString("img2"));
                                imagePath.add(json.getString("img3"));
                                imageTitle.add("");
                                imageTitle.add("");
                                imageTitle.add("");
                                imageTitle.add("");
                                initView();
                                mBanner.start();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }


    private void initView() {
        //样式
        mBanner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        //加载器
        mBanner.setImageLoader(mImageLoader);
        //动画效果
        mBanner.setBannerAnimation(Transformer.ZoomOutSlide);
        //间隔时间
        mBanner.setDelayTime(3000);
        //是否为自动轮播
        mBanner.isAutoPlay(true);
        //图片小点显示所在位置
        mBanner.setIndicatorGravity(BannerConfig.CENTER);
        //图片加载地址
        mBanner.setImages(imagePath);
        //图片标题
        mBanner.setBannerTitles(imageTitle);

    }

    private class MyImageLoader extends ImageLoader {
        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            Glide.with(context.getApplicationContext())
                    .load((String) path)
                    .into(imageView);
        }
    }

    public static ArrayList<String[]> getNames(int len){
        ArrayList<String[]> names=new ArrayList<String[]>();
        int page=1;
        try {
            Document doc= Jsoup.connect("http://www.aihuhua.com/hua/").get();
            Elements elements=doc.select("ul li a.title");
            int i=0;
            while (i<len) {
                for (Element e : elements) {
                    String title = e.attr("title");
                    String str = e.attr("href");
                    String address = str.substring(str.lastIndexOf("/") + 1, str.length() - 5);
                    //System.out.println(title + "  " + address);
                    if(i>=len) break;
                    names.add(new String[]{title,address});
                    //names[i][0] = title;
                    //names[i][1] = address;
                    i++;
                }
                if(i>=len) break;
                page++;
                doc=Jsoup.connect("http://www.aihuhua.com/hua/page-"+page+".html").get();
                elements=doc.select("ul li a.title");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        names.sort(new Comparator<String[]>() {
            @Override
            public int compare(String[] o1, String[] o2) {
                if (o1[1].charAt(0)>o2[1].charAt(0))
                    return 1;
                else return -1;
            }
        });
        for (String[] strings:names){
            System.out.println(strings[0]+" "+strings[1]);
        }

        return names;
    }


}