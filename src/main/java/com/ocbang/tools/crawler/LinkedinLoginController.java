package com.ocbang.tools.crawler;

import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Author: SundayDX
 * Date: 2017/3/5
 * <p>
 * 很潦草的写了一下登录的示范
 * Linkedin 的登录流程需要获取对应的csrf的参数，同时需要注意header的完整程度
 * 登录成功了，返回的是302到nhome路径上，但是也会到verity的页面，进行二步验证
 * 200状态则是登录失败，这个很反直觉
 */
@Component
public class LinkedinLoginController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private Map<String, String> cookieMap = new HashMap<>(64);
    private String formUrl = null;
    private Map<String, String> inputForm = new HashMap<>();

    @Value("${linkedin.rootUrl}")
    private String rootUrl;
    @Value("${linkedin.loginUrl}")
    private String loginUrl;
    @Value("${linkedin.logoutUrl}")
    private String logoutUrl;
    @Value("${linkedin.homeUrl}")
    private String homeUrl;
    @Value("${linkedin.username}")
    private String username;
    @Value("${linkedin.password}")
    private String password;

    private CloseableHttpClient httpClient = null;

    public LinkedinLoginController() {
        logger.info("LinkedinLoginController initial with {}/{}", username, password);
        RequestConfig requestConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD_STRICT).build();
        this.httpClient = HttpClients.custom().setDefaultRequestConfig(requestConfig).build();
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
        post.setHeader("Pragma", "no-cache");
        post.setHeader("Referer", "https://www.linkedin.com/");
        post.setHeader("Upgrade-Insecure-Requests", "1");
        post.setHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");
        response = this.httpClient.execute(post);
        Header[] headers = response.getHeaders("location");
        response.close();

        ret = headers.length > 0;

        return ret;
    }

    public void visitUserHome() throws IOException {
        CloseableHttpResponse response = null;
        HttpGet get = new HttpGet(this.rootUrl);
        response = httpClient.execute(get);
        logger.info(EntityUtils.toString(response.getEntity()));
        response.close();
    }

    public void login() throws Exception {

        UrlEncodedFormEntity entity = this.visitSiteRoot();
        if(!this.submitLoginForm(entity)){
            throw new Exception("Login failed");
        }
        this.visitUserHome();

    }

    private UrlEncodedFormEntity buildLoginEntity(String html) {
        Document doc = Jsoup.parse(html);
        Element form = doc.select("form").first();
        this.formUrl = form.attr("action");

        List<NameValuePair> valuePairs = new LinkedList<>();

        Elements input = form.select("input");
        for (Element ele : input) {
            String name = ele.attr("name");
            String value = ele.attr("value");

            if (name != null && value != null) {
                valuePairs.add(new BasicNameValuePair(name, value));
            }
        }
        this.inputForm.put("session_key", this.username);
        this.inputForm.put("session_password", this.password);

        for (String name : this.inputForm.keySet()) {
            valuePairs.add(new BasicNameValuePair(name, this.inputForm.get(name)));
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
