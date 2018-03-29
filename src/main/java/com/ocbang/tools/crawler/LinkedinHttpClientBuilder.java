package com.ocbang.tools.crawler;

import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;

import javax.net.ssl.SSLContext;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class LinkedinHttpClientBuilder {

    public static CloseableHttpClient create() throws Exception {
        SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(new TrustStrategy() {
            public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                return true;
            }
        }).build();

        HttpHost proxy = new HttpHost("127.0.0.1", 8888, "http");
        RequestConfig requestConfig = RequestConfig.custom().setProxy(proxy).setCookieSpec(CookieSpecs.STANDARD_STRICT).build();
//        RequestConfig requestConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD_STRICT).build();
        return HttpClients.custom().setSSLContext(sslContext).setDefaultRequestConfig(requestConfig).build();
    }
}
