package com.example.navigate;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class CrawlFlowersData {
    public static void main(String[] args) {
        try {
            getData("guihua");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static JSONObject getData(String s) throws IOException {
        JSONObject jsonObject = new JSONObject();
        Document doc1 = Jsoup.connect("http://www.aihuhua.com/tu/" + s + ".html").get();
        Elements elements = doc1.select("img.lazy");
        for (int i = 0; i < 4; i++) {
            String src = elements.get(i).attr("data-original");
            try {
                jsonObject.put("img" + i, src);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Document doc2 = Jsoup.connect("http://www.aihuhua.com/huahui/" + s + ".html").get();
        Elements elements1 = doc2.select("div.content dl");
        StringBuilder stringBuilder = new StringBuilder();
        for (Element e : elements1)
            stringBuilder.append(e.toString());
        try {
            jsonObject.put("content", stringBuilder.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //System.out.println(content);
                /*Elements elements1 = doc2.select("dl");
                for (Element e : elements1) {
                    if (e.select("dt h3").text().contains("介绍"))
                        introduce = e.select("dd").text();
                    if (e.select("dt h3").text().contains("形态特征"))
                        xttz = e.select("dd").text();
                    if (e.select("dt h3").text().contains("生态习性"))
                        stxx = e.select("dd").text();
                    if (e.select("dt h3").text().contains("养殖方法"))
                        yzff = e.select("dd").text();
                    if (e.select("dt h3").text().contains("栽培技术"))
                        zpjs = e.select("dd").text();
                }
                System.out.println("介绍\n" + introduce);
                System.out.println("形态特征\n" + xttz);
                System.out.println("生态习性\n" + stxx);
                System.out.println("养殖方法\n" + yzff);
                System.out.println("栽培技术\n" + zpjs);
                DbBean dbBean=new DbBean();
                if(!dbBean.addBook(name,img[0],img[1],img[2],img[3],introduce,xttz,stxx,yzff,zpjs))
                    return;*/
        System.out.println(jsonObject.toString());
        return jsonObject;
    }
}
