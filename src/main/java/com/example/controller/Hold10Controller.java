package com.example.controller;


import com.example.model.auto.Hold10;
import com.example.model.auto.StockDayInfo;
import com.example.model.auto.StockPreNotice;
import com.example.service.IHold10Service;
import com.example.service.IStockPreNoticeService;
import com.example.util.RedisUtil;
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

import javax.annotation.Resource;
import javax.sound.midi.Soundbank;
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
    @Autowired
    private IStockPreNoticeService iStockPreNoticeService;
    @Resource
    RedisUtil redisUtil;

    //private static String url="http://basic.10jqka.com.cn/002139/holder.html";


    @GetMapping("/getUser")
    public Hold10 getUser(){
        return iHold10Service.getById(1);
    }

    @GetMapping("/genHold10Data")
    public int genHold10Data() throws Exception {
        //获取业绩公告的股票
        List<StockPreNotice> stockNotice = getStockNotice();

        iStockPreNoticeService.saveOrUpdateBatch(stockNotice);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        int k=0;
        for (StockPreNotice stockPreNotice : stockNotice) {
            String url="http://basic.10jqka.com.cn/"+stockPreNotice.getStockNum()+"/holder.html";
            Document html= null;
            try {
                html = getHtmlFromUrl(url, false);
                Elements years = html.select("#bd_1").select("li");
                List<String> yearList= new ArrayList<>();
                for (Element year : years) {
                    yearList.add(year.text());
                }

                Elements select = html.select("table.m_table.m_hl.ggintro");

                List<Hold10> dataList= new ArrayList<>();
                int i=0;
                for(Element el:select){//遍历i个以季度（日期）分组的table
                    String caption = el.select("caption").text();
                    if(caption.indexOf("前十大流通股东累计持有")>-1){

                        Elements trs = el.select("tbody").select("tr");

                        for (Element tr : trs) {
                            Hold10 hold10 = new Hold10();

                            hold10.setStockName(stockPreNotice.getStockName());
                            hold10.setStockNum(stockPreNotice.getStockNum());
                            hold10.setCreateTime(LocalDateTime.now());
                            hold10.setDeliveryDate(sdf.parse(yearList.get(i)));
                            Elements th2 = tr.select("th");
                            Elements th = tr.select("td");

                            hold10.setHoldName(th2.get(0).text());//设置十大流通股东姓名
                            if(th.size()==0){
                                continue;
                            }
                            hold10.setHoldNum(th.get(0).text());//设置持股数量
                            //设置持股变化数量
                            String text = th.get(1).text();
                            String text3 = th.get(4).text();
                            hold10.setHoldNumChange(text);
                            hold10.setHoldChangRate(text3);

                            hold10.setHoldRate(th.get(2).text());
                            hold10.setType(th.get(5).text());//设置股份类型
                            //hold10.setId(hold10.getStockNum()+"_"+yearList.get(i)+"_"+hold10.getHoldName());
                            hold10.setId(UUID.randomUUID().toString());
                            hold10.setIsFromNotice(1);
                            dataList.add(hold10);

                        }
                        i++;
                        k++;

                    }

                }
                if(dataList.size()>0){
                    iHold10Service.saveOrUpdateBatch(dataList);

                }
            } catch (Exception e) {
                System.out.println("出现异常");
                e.printStackTrace();
               continue;
            }
        }

        return k;

    }


    @GetMapping("/addStockHold10")
    public int addStockHold10() throws Exception {

       // Set<String> stocks = (Set<String>) redisUtil.get("stocks");
        Set<String> stocks = new HashSet<>();
        //if(stocks.size()==0){
            StockDayInfoController.genInfo().forEach(e->stocks.add(e.getStockNum()));
       // }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        int k=0;
        for (StockDayInfo stockDayInfo : StockDayInfoController.genInfo()) {
            String url="http://basic.10jqka.com.cn/"+stockDayInfo.getStockNum()+"/holder.html";
            Document html= null;
            try {
                html = getHtmlFromUrl(url, false);
                Elements years = html.select("#bd_1").select("li");
                List<String> yearList= new ArrayList<>();
                for (Element year : years) {
                    yearList.add(year.text());
                }

                Elements select = html.select("table.m_table.m_hl.ggintro");

                List<Hold10> dataList= new ArrayList<>();
                int i=0;
                for(Element el:select){//遍历i个以季度（日期）分组的table
                    String caption = el.select("caption").text();
                    if(caption.indexOf("前十大流通股东累计持有")>-1){

                        Elements trs = el.select("tbody").select("tr");

                        for (Element tr : trs) {
                            Hold10 hold10 = new Hold10();

                            hold10.setStockName(stockDayInfo.getStockName());
                            hold10.setStockNum(stockDayInfo.getStockNum());
                            hold10.setCreateTime(LocalDateTime.now());
                            hold10.setDeliveryDate(sdf.parse(yearList.get(i)));
                            Elements th2 = tr.select("th");
                            Elements th = tr.select("td");

                            hold10.setHoldName(th2.get(0).text());//设置十大流通股东姓名
                            if(th.size()==0){
                                continue;
                            }
                            hold10.setHoldNum(th.get(0).text());//设置持股数量
                            //设置持股变化数量
                            String text = th.get(1).text();
                            String text3 = th.get(4).text();
                            hold10.setHoldNumChange(text);
                            hold10.setHoldChangRate(text3);

                            hold10.setHoldRate(th.get(2).text());
                            hold10.setType(th.get(5).text());//设置股份类型
                            //hold10.setId(hold10.getStockNum()+"_"+yearList.get(i)+"_"+hold10.getHoldName());
                            hold10.setId(UUID.randomUUID().toString());
                            dataList.add(hold10);

                        }
                        i++;
                        k++;

                    }

                }
                if(dataList.size()>0){
                    iHold10Service.saveOrUpdateBatch(dataList);

                }
            } catch (Exception e) {
                System.out.println("出现异常");
                e.printStackTrace();
                continue;
            }
        }

        return k;

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

    public static void main(String[] args) throws Exception {
        String url="http://basic.10jqka.com.cn/300481/holder.html";
        Document htmlFromUrl = getHtmlFromUrl(url, true);
        Elements select = htmlFromUrl.select("table.m_table.m_hl.ggintro");
        System.out.println(select);
    }

    public  static List<StockPreNotice>
    getStockNotice() throws Exception {

        String url2="http://data.10jqka.com.cn/ajax/yjyg/date/2021-03-31/board/ALL/field/enddate/order/desc/page/1/ajax/1/free/1/";
        Document htmlFromUrl = getHtmlFromUrl(url2, true);
        String pageInfo = htmlFromUrl.select("span.page_info").text();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String page=pageInfo.split("/")[1];
        List<StockPreNotice> list= new ArrayList<>();
        for(int k=1;k<=Integer.valueOf(page);k++){
            String url="http://data.10jqka.com.cn/ajax/yjyg/date/2021-03-31/board/ALL/field/enddate/order/desc/page/"+k+"/ajax/1/free/1/";
            //System.out.println("分页：url"+url);
            Document pageInfo2 = getHtmlFromUrl(url, true);
            Elements select = pageInfo2.select("table").select("tbody").select("tr");
            for (Element element : select) {
                //System.out.println("一个tr 一组数据");
                //System.out.println(element.text());
                Elements td = element.select("td");
                String stockNum = td.get(1).text();
                String stockName = td.get(2).text();
                String noticeType=td.get(3).text();
                String noticeSummary=td.get(4).text();
                String changeRate=td.get(5).text();
                String noticeDateStr= td.get(7).text();
                StockPreNotice stockPreNotice = new StockPreNotice();

                stockPreNotice.setStockName(stockName);
                stockPreNotice.setStockNum(stockNum);
                stockPreNotice.setNoticeType(noticeType);
                stockPreNotice.setNoticeSummary(noticeSummary);
                stockPreNotice.setChangeRate(changeRate);
                stockPreNotice.setNoticeDate(sdf.parse(noticeDateStr));
                stockPreNotice.setId(stockNum+"_"+noticeDateStr);
                list.add(stockPreNotice);
            }

        }
        return list;

    }
}
