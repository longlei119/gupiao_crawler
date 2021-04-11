package com.example.stock;

import com.example.controller.Hold10Controller;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

/**
 * @Description: TODO
 * @author: scott
 * @date: 2021年04月09日 22:43
 */
public class Testkk {

    public static void main(String[] args) throws Exception {
        String url="http://basic.10jqka.com.cn/002139/holder.html";
        long l = System.currentTimeMillis();
        Document document = Jsoup.connect(url).get();
        System.out.println("用时："+(System.currentTimeMillis()-l));
        //System.out.println(document);
        System.out.println(document.toString().length());
        long l1 = System.currentTimeMillis();
        Document htmlFromUrl = Hold10Controller.getHtmlFromUrl(url, false);

        System.out.println("用时："+(System.currentTimeMillis()-l1));
        System.out.println(htmlFromUrl.toString().length());



    }
}
