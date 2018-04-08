package com.ocbang.tools.crawler;

import com.ocbang.tools.crawler.internships.controller.InternshipsCrawlController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Yasser Ganjisaffar
 */
@SpringBootApplication(scanBasePackages = {"com.ocbang.tools.crawler"})
public class MainCrawlController implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(MainCrawlController.class);

    @Value("${internships.crawl-mode}")
    private String crawlMode;

    @Autowired
    private InternshipsCrawlController internshipsCrawlController;

    @Override
    public void run(String... args) throws Exception {

        switch (crawlMode) {
            case "default":
                internshipsCrawlController.crawlByDefault();
                break;
            case "company":
                internshipsCrawlController.crawlByCompany();
                break;
            default:
                logger.error("Invalid crawl mode");
                break;
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(MainCrawlController.class, args);
    }

}