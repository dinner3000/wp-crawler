package com.ocbang.tools.crawler.linkedin;

import org.apache.http.*;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class LinkedinLoginController {

    private static Logger logger = LoggerFactory.getLogger(LinkedinLoginController.class);

    private Map<String, String> cookieMap = new HashMap<String, String>(64);
    private String formUrl = null;

    private String rootUrl = "https://www.linkedin.com/";
    private String loginUrl = "https://www.linkedin.com/uas/login-submit";
    private String logoutUrl = "";
    private String homeUrl = "https://www.linkedin.com/nhome/";
    private String username;
    private String password;

    private CloseableHttpClient httpClient = null;

    public LinkedinLoginController(CloseableHttpClient httpClient, String username, String password) throws Exception {
        this.username = username;
        this.password = password;

        this.httpClient = httpClient;
    }

    private UrlEncodedFormEntity visitSiteRoot() throws IOException {
        UrlEncodedFormEntity entity = null;
        CloseableHttpResponse response = null;

        HttpGet get = new HttpGet(this.rootUrl);
        response = httpClient.execute(get);
        entity = this.buildLoginEntity(EntityUtils.toString(response.getEntity()));
        response.close();

        return entity;
    }

    private boolean submitLoginForm(UrlEncodedFormEntity entity) throws IOException {

        boolean ret = false;

        CloseableHttpResponse response = null;

        HttpPost post = new HttpPost(this.loginUrl);
        post.setEntity(entity);
        post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:58.0) Gecko/20100101 Firefox/58.0");
        post.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        post.setHeader("Accept-Language", "en-US,en;q=0.5");
        post.setHeader("Accept-Encoding", "gzip, deflate, br");
        post.setHeader("Referer", "https://www.linkedin.com/");
        post.setHeader("Connection", "keep-alive");
        post.setHeader("Upgrade-Insecure-Requests", "1");
        post.setHeader("Pragma", "no-cache");
        post.setHeader("Cache-Control", "no-cache");
        response = this.httpClient.execute(post);
        Header[] headers = response.getHeaders("location");
        response.close();

        ret = headers.length > 0;

        return ret;
    }

    public void visitUserHome() throws IOException {
        CloseableHttpResponse response = null;
        HttpGet get = new HttpGet(this.homeUrl);
        response = httpClient.execute(get);
        logger.info(EntityUtils.toString(response.getEntity()));
        response.close();
    }

    public void login() throws Exception {

        UrlEncodedFormEntity entity = this.visitSiteRoot();
        if(!this.submitLoginForm(entity)){
            throw new Exception("Login failed");
        }else {
            logger.info("Login success");
        }
        this.visitUserHome();

    }

    private UrlEncodedFormEntity buildLoginEntity(String html) {
        Document doc = Jsoup.parse(html);
        Element form = doc.select("form").first();
        this.formUrl = form.attr("action");

//        HashMap<String, String> valuePairs = new HashMap<>();
        List<NameValuePair> valuePairs = new LinkedList<NameValuePair>();

        Elements input = form.select("input");
        for (Element ele : input) {
            String name = ele.attr("name");
            String value = ele.attr("value");

            if (name != null && name != "" && value != null) {
                if (name.equals("session_key")) {
                    valuePairs.add(new BasicNameValuePair(name, this.username));
                } else if (name.equals("session_password")) {
                    valuePairs.add(new BasicNameValuePair(name, this.password));
                } else {
                    valuePairs.add(new BasicNameValuePair(name, value));
                }
            }
        }
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(valuePairs, Consts.UTF_8);

        return entity;
    }

    private String getCookie() {
        String cookiesTmp = "";
        for (String key : this.cookieMap.keySet()) {
            cookiesTmp += key + "=" + this.cookieMap.get(key) + ";";
        }

        return cookiesTmp.substring(0, cookiesTmp.length() - 2);
    }

    private void setCookie(HttpResponse httpResponse) {
        Header headers[] = httpResponse.getHeaders("Set-Cookie");
        if (headers == null || headers.length == 0) {
            return;
        }
        String cookie = "";
        for (int i = 0; i < headers.length; i++) {
            cookie += headers[i].getValue();
            if (i != headers.length - 1) {
                cookie += ";";
            }
        }

        String cookies[] = cookie.split(";");
        for (String c : cookies) {
            c = c.trim();
            if (this.cookieMap.containsKey(c.split("=")[0])) {
                this.cookieMap.remove(c.split("=")[0]);
            }
            this.cookieMap.put(c.split("=")[0], c.split("=").length == 1 ? "" :
                    (c.split("=").length == 2 ? c.split("=")[1] : c.split("=", 2)[1]));
        }
    }

}
