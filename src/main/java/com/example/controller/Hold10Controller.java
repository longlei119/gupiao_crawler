package com.example.controller;


import com.example.model.auto.Hold10;
import com.example.service.IHold10Service;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

/**
 * <p>
 * 十大流通股东 前端控制器
 * </p>
 *
 * @author astupidcoder
 * @since 2021-04-08
 */
@RestController
@RequestMapping("/hold10")
@Slf4j
public class Hold10Controller {
    @Autowired
    private IHold10Service iHold10Service;

    private static String url="http://basic.10jqka.com.cn/002139/holder.html";


    @GetMapping("/getUser")
    public Hold10 getUser(){
        return iHold10Service.getById(1);
    }
    @GetMapping("/genHold10Data")
    public int genHold10Data(){

        Document html= null;
        try {
            html = getHtmlFromUrl(url, true);
            Elements years = html.select("#bd_1").select("li");
            List<String> yearList= new ArrayList<>();
            for (Element year : years) {
                yearList.add(year.text());
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Elements select = html.select("table.m_table.m_hl.ggintro");
            int k=0;
            List<Hold10> dataList= new ArrayList<>();
            for(Element el:select){
                String caption = el.select("caption").text();
                if(caption.indexOf("前十大流通股东累计持有")>-1){
                    Hold10 hold10 = new Hold10();
                    hold10.setId(UUID.randomUUID().toString());
                    hold10.setCreateTime(LocalDateTime.now());
                    hold10.setDeliveryDate(sdf.parse(yearList.get(k)));
                    Elements body = el.select("tbody");
                    Elements th2 = body.select("th");
                    Elements th = body.select("td");
                    hold10.setHoldName(th2.get(0).text());//设置十大流通股东姓名
                    hold10.setHoldNum(th.get(0).text());//设置持股数量
                    //设置持股变化数量
                    String text = th.get(1).text();
                    String text3 = th.get(4).text();
                    if("新进".equals(text) || "不变".equals(text)){
                        hold10.setHoldNumChange(text);
                        hold10.setHoldChangRate(text3);
                    }else{
                        String s = th.select("s").html();
                        if(s.indexOf("up")>-1){
                            hold10.setHoldNumChange("+"+text);
                            hold10.setHoldChangRate("+"+text3);
                        }else{
                            hold10.setHoldNumChange("-"+text);
                            hold10.setHoldChangRate("-"+text3);
                        }

                    }

                    hold10.setHoldRate(th.get(2).text());
                    hold10.setType(th.get(5).text());//设置股份类型
                    dataList.add(hold10);
                    k++;

                }

            }
            iHold10Service.saveBatch(dataList);
            System.out.println("table length:"+dataList);
            return k;
        } catch (Exception e) {
            log.error("==============爬取失败: {}=============", url);
            e.printStackTrace();
            return 0;
        }
    }




    public static Document getHtmlFromUrl(String url, boolean useHtmlUnit) throws Exception {
        if (!useHtmlUnit) {
            return Jsoup.connect(url)
                    //模拟火狐浏览器
                    .userAgent("Mozilla/4.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)")
                    .get();
        } else {
            WebClient webClient = new WebClient(BrowserVersion.CHROME);
            webClient.getOptions().setJavaScriptEnabled(true);
            webClient.getOptions().setCssEnabled(false);
            webClient.getOptions().setActiveXNative(false);
            webClient.getOptions().setCssEnabled(false);
            webClient.getOptions().setThrowExceptionOnScriptError(false);
            webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
            webClient.getOptions().setTimeout(10000);
            HtmlPage rootPage = null;
            try {
                rootPage = webClient.getPage(url);
                webClient.waitForBackgroundJavaScript(10000);
                String htmlString = rootPage.asXml();
                return Jsoup.parse(htmlString);
            } catch (Exception e) {
                throw e;
            } finally {
                webClient.close();
            }
        }
    }
}
