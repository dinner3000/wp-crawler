package com.ocbang.tools.crawler.internships;

import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.ssl.SSLContexts;

import javax.net.ssl.SSLContext;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

public class InternshipsHttpClientBuilder {

    public static CloseableHttpClient create() throws Exception {
        SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(new TrustStrategy() {
            public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                return true;
            }
        }).build();

//        HttpHost proxy = new HttpHost("127.0.0.1", 8888, "http");
//        RequestConfig requestConfig = RequestConfig.custom().setProxy(proxy).setCookieSpec(CookieSpecs.STANDARD_STRICT).build();
        RequestConfig requestConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD_STRICT).build();

        ArrayList<Header> headers = new ArrayList<Header>();
        Header header = new BasicHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:58.0) Gecko/20100101 Firefox/58.0");
        headers.add(header);

        return HttpClients.custom().setSSLContext(sslContext).setDefaultRequestConfig(requestConfig).setDefaultHeaders(headers).build();
    }
}
