package com.example.controller;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.model.auto.Hold10;
import com.example.model.auto.StockDayInfo;
import com.example.model.auto.StockPreNotice;
import com.example.service.IStockDayInfoService;
import com.example.util.HttpUtil;
import com.example.util.RedisUtil;
import com.example.util.SpringContextUtil;
import com.example.util.StockEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;

/**
 * <p>
 * 股票每日信息 前端控制器
 * </p>
 *
 * @author astupidcoder
 * @since 2021-04-10
 */
@RestController
@RequestMapping("/stock-day-info")
public class StockDayInfoController {
    //@Resource
    //RedisUtil redisUtil;
    @Autowired
    private  IStockDayInfoService iStockDayInfoService;
    @GetMapping("/add")
    public Integer add() throws Exception {
        List<StockDayInfo> dataList = genInfo();
        if(dataList.size()>0){
                iStockDayInfoService.saveOrUpdateBatch(dataList);
            }

        return dataList.size();
    }
    public static   List<StockDayInfo> genInfo() throws Exception {

        String url3="http://33.push2.eastmoney.com/api/qt/clist/get?pn=1&pz=5000&po=1&np=1&fltt=2&invt=2&fid=f3&fs=m:0+t:6,m:0+t:80,m:1+t:2,m:1+t:23" +
                "&fields=f12,f14,f3,f6,f10,f8,f9,f23" +
                "&_=1618045723746";
        String post = HttpUtil.post(url3, "");
        JSONObject jsonObject = JSONObject.parseObject(post);

        Object data = jsonObject.get("data");
        JSONObject data2 = JSONObject.parseObject(data.toString());
        Object diff = data2.get("diff");
        List<StockEntity> stockEntities = JSONArray.parseArray(diff.toString(), StockEntity.class);
        List<StockDayInfo> dataList = new ArrayList<>();
        Set<String>  stocks= new HashSet<>();
        for (StockEntity stockEntity : stockEntities) {
            StockDayInfo stockDayInfo = new StockDayInfo();
            stockDayInfo.setStockName(stockEntity.getF14());
            stockDayInfo.setStockNum(stockEntity.getF12());
            stockDayInfo.setChangeRate(stockEntity.getF8());
            stockDayInfo.setNumRate(stockEntity.getF10());
            stockDayInfo.setDealMoney("-".equals(stockEntity.getF6())?-1:Float.valueOf(stockEntity.getF6())/1000);
            stockDayInfo.setUpDownRate(stockEntity.getF3());
            stockDayInfo.setSyl(stockEntity.getF9());
            stockDayInfo.setSjl(stockEntity.getF23());
            stockDayInfo.setId(UUID.randomUUID().toString());
            stockDayInfo.setCreateTime(LocalDateTime.now());
            dataList.add(stockDayInfo);
            stocks.add(stockEntity.getF12());
        }


        RedisUtil bean = (RedisUtil) SpringContextUtil.getBean("redisUtil");
        bean.set("stocks",stocks);
        //bean.set()
        return dataList;
    }
}
