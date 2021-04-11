package com.example.util;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.util.StringUtils;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * http、https 请求工具类， 微信为https的请求
 * @author yehx
 *
 */
public class HttpUtil {

    private static final String DEFAULT_CHARSET = "UTF-8";

    private static final String _GET = "GET"; // GET
    private static final String _POST = "POST";// POST
    public static final int DEF_CONN_TIMEOUT = 30000;
    public static final int DEF_READ_TIMEOUT = 30000;

    /**
     * 初始化http请求参数
     *
     * @param url
     * @param method
     * @param headers
     * @return
     * @throws Exception
     */
    private static HttpURLConnection initHttp(String url, String method,
                                              Map<String, String> headers) throws Exception {
        URL _url = new URL(url);
        HttpURLConnection http = (HttpURLConnection) _url.openConnection();
        // 连接超时
        http.setConnectTimeout(DEF_CONN_TIMEOUT);
        // 读取超时 --服务器响应比较慢，增大时间
        http.setReadTimeout(DEF_READ_TIMEOUT);
        http.setUseCaches(false);
        http.setRequestMethod(method);
        http.setRequestProperty("Content-Type",
                "application/x-www-form-urlencoded");
        http.setRequestProperty(
                "User-Agent",
                "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.146 Safari/537.36");
        if (null != headers && !headers.isEmpty()) {
            for (Entry<String, String> entry : headers.entrySet()) {
                http.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }
        http.setDoOutput(true);
        http.setDoInput(true);
        http.connect();
        return http;
    }

    /**
     * 初始化http请求参数
     *
     * @param url
     * @param method
     * @return
     * @throws Exception
     */
    private static HttpsURLConnection initHttps(String url, String method,
                                                Map<String, String> headers) throws Exception {
        TrustManager[] tm = { new MyX509TrustManager() };
        System.setProperty("https.protocols", "TLSv1");
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tm, new java.security.SecureRandom());
        // 从上述SSLContext对象中得到SSLSocketFactory对象
        SSLSocketFactory ssf = sslContext.getSocketFactory();
        URL _url = new URL(url);
        HttpsURLConnection http = (HttpsURLConnection) _url.openConnection();
        // 设置域名校验
        http.setHostnameVerifier(new HttpUtil().new TrustAnyHostnameVerifier());
        // 连接超时
        http.setConnectTimeout(DEF_CONN_TIMEOUT);
        // 读取超时 --服务器响应比较慢，增大时间
        http.setReadTimeout(DEF_READ_TIMEOUT);
        http.setUseCaches(false);
        http.setRequestMethod(method);
        http.setRequestProperty("Content-Type",
                "application/x-www-form-urlencoded");
        http.setRequestProperty(
                "User-Agent",
                "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.146 Safari/537.36");
        if (null != headers && !headers.isEmpty()) {
            for (Entry<String, String> entry : headers.entrySet()) {
                http.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }
        http.setSSLSocketFactory(ssf);
        http.setDoOutput(true);
        http.setDoInput(true);
        http.connect();
        return http;
    }

    /**
     *
     * @description 功能描述: get 请求
     * @return 返回类型:
     * @throws Exception
     */
    public static String get(String url, Map<String, String> params,
                             Map<String, String> headers) throws Exception {
        HttpURLConnection http = null;
        if (isHttps(url)) {
            http = initHttps(initParams(url, params), _GET, headers);
        } else {
            http = initHttp(initParams(url, params), _GET, headers);
        }
        InputStream in = http.getInputStream();
        BufferedReader read = new BufferedReader(new InputStreamReader(in,
                DEFAULT_CHARSET));
        String valueString = null;
        StringBuffer bufferRes = new StringBuffer();
        while ((valueString = read.readLine()) != null) {
            bufferRes.append(valueString);
        }
        in.close();
        if (http != null) {
            http.disconnect();// 关闭连接
        }
        return bufferRes.toString();
    }

    public static String get(String url) throws Exception {
        return get(url, null);
    }

    public static String get(String url, Map<String, String> params)
            throws Exception {
        return get(url, params, null);
    }

    public static String post(String url, String params)
            throws Exception {
        HttpURLConnection http = null;
        if (isHttps(url)) {
            http = initHttps(url, _POST, null);
        } else {
            http = initHttp(url, _POST, null);
        }
        OutputStream out = http.getOutputStream();
        out.write(params.getBytes(DEFAULT_CHARSET));
        out.flush();
        out.close();

        InputStream in = http.getInputStream();
        BufferedReader read = new BufferedReader(new InputStreamReader(in,
                DEFAULT_CHARSET));
        String valueString = null;
        StringBuffer bufferRes = new StringBuffer();
        while ((valueString = read.readLine()) != null) {
            bufferRes.append(valueString);
        }
        in.close();
        if (http != null) {
            http.disconnect();// 关闭连接
        }
        return bufferRes.toString();
    }

    /**
     * 功能描述: 构造请求参数
     *
     * @return 返回类型:
     * @throws Exception
     */
    public static String initParams(String url, Map<String, String> params)
            throws Exception {
        if (null == params || params.isEmpty()) {
            return url;
        }
        StringBuilder sb = new StringBuilder(url);
        if (url.indexOf("?") == -1) {
            sb.append("?");
        }
        sb.append(map2Url(params));
        return sb.toString();
    }

    /**
     * map构造url
     *
     * @return 返回类型:
     * @throws Exception
     */
    public static String map2Url(Map<String, String> paramToMap)
            throws Exception {
        if (null == paramToMap || paramToMap.isEmpty()) {
            return null;
        }
        StringBuffer url = new StringBuffer();
        boolean isfist = true;
        for (Entry<String, String> entry : paramToMap.entrySet()) {
            if (isfist) {
                isfist = false;
            } else {
                url.append("&");
            }
            url.append(entry.getKey()).append("=");
            String value = entry.getValue();
            if (!StringUtils.isEmpty(value)) {
                url.append(URLEncoder.encode(value, DEFAULT_CHARSET));
            }
        }
        return url.toString();
    }

    /**
     * 检测是否https
     *
     * @param url
     */
    private static boolean isHttps(String url) {
        return url.startsWith("https");
    }

    /**
     * https 域名校验
     * @return
     */
    public class TrustAnyHostnameVerifier implements HostnameVerifier {
        public boolean verify(String hostname, SSLSession session) {
            return true;// 直接返回true
        }
    }

    public static  String getAccessToken(){
        String post = null;
        try {
            post = HttpUtil.post("https://open.ys7.com/api/lapp/token/get",
                    "appKey=ca60c005fe454770ac6fd81fd03a6164&appSecret=e081d6cf08c0607b8cc9ce438dc7d896");
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject jsonObject = JSON.parseObject(post);
        Object accessToken = jsonObject.get("data");
        JSONObject data = JSON.parseObject(accessToken.toString());
        return data.get("accessToken").toString();
    }
    public static void main(String[] args) throws Exception {
        Integer pages= 120;
        String url="http://www.szse.cn/api/report/ShowReport/data?SHOWTYPE=JSON&CATALOGID=1110&TABKEY=tab1&PAGENO=4";
        String url2="http://http://33.push2.eastmoney.com/api/qt/clist/get?pn=1&pz=5000&po=1&np=1&fltt=2&invt=2&fid=f3&fs=m:0+t:6,m:0+t:80,m:1+t:2,m:1+t:23" +
                "&fields=f1,f2,f3,f4,f5,f6,f7,f8,f9,f10,f12,f13,f14,f15,f16,f17,f18,f20,f21,f23,f24,f25,f22,f11,f62,f128,f136,f115,f152" +
                "&_=1618045723746";
        String url3="http://33.push2.eastmoney.com/api/qt/clist/get?pn=1&pz=5000&po=1&np=1&fltt=2&invt=2&fid=f3&fs=m:0+t:6,m:0+t:80,m:1+t:2,m:1+t:23" +
                "&fields=f12,f14,f3,f6,f10,f8,f9,f23" +
                "&_=1618045723746";
        String post = HttpUtil.post(url3, "");
        JSONObject jsonObject = JSONObject.parseObject(post);

        Object data = jsonObject.get("data");
        JSONObject data2 = JSONObject.parseObject(data.toString());
        Object diff = data2.get("diff");
        List<StockEntity> stockEntities = JSONArray.parseArray(diff.toString(), StockEntity.class);
        //JSONArray objects = JSONArray.parseArray(post);
        for (StockEntity stockEntity : stockEntities) {
            System.out.println(stockEntity);
        }
        //System.out.println(stockEntities);
    }
}
