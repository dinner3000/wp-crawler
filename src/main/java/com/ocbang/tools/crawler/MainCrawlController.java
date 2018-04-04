package com.ocbang.tools.crawler;

import com.ocbang.tools.crawler.internships.InternshipsCrawlController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Yasser Ganjisaffar
 */
@SpringBootApplication(scanBasePackages = {"com.ocbang.tools.crawler"})
public class MainCrawlController implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(MainCrawlController.class);

    @Autowired
    private InternshipsCrawlController internshipsCrawlController;

    @Override
    public void run(String... args) throws Exception {

//        CloseableHttpClient httpClient = LinkedinHttpClientBuilder.create();
//        LinkedinLoginController loginController = new LinkedinLoginController(httpClient, "garyfake@mail.com", "1234abcd!@#$");
//        loginController.login();
        internshipsCrawlController.run();
    }

    public static void main(String[] args) {
        SpringApplication.run(MainCrawlController.class, args);
    }

}