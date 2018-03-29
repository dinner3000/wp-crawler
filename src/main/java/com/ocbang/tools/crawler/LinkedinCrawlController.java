package com.ocbang.tools.crawler;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

import static com.ocbang.tools.crawler.LinkedinJobListPage.getJobDetailPageUrls;

/**
 * @author Yasser Ganjisaffar
 */
@SpringBootApplication
public class LinkedinCrawlController implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(LinkedinCrawlController.class);

    public LinkedinCrawlController() {
    }

    @Override
    public void run(String... args) throws Exception {

        CloseableHttpClient httpClient = LinkedinHttpClientBuilder.create();
        LinkedinLoginController loginController = new LinkedinLoginController(httpClient, "garyfake@mail.com", "1234abcd!@#$");
        loginController.login();

        CloseableHttpResponse response = null;
        HttpGet get = new HttpGet("http://www.linkedin.com/company/bmw-brilliance-automotive/jobs/");
        response = httpClient.execute(get);

        List<String> jobDetailUrls = LinkedinJobListPage.getJobDetailPageUrls(EntityUtils.toString(response.getEntity()));
    }

    public static void main(String[] args) {
        SpringApplication.run(LinkedinCrawlController.class, args);
    }

}