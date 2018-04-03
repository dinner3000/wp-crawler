package com.ocbang.tools.crawler;

import com.ocbang.tools.crawler.internships.*;
import com.ocbang.tools.crawler.wordpress.WPPostsDAO;
import com.ocbang.tools.crawler.wordpress.WPTermsDAO;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

/**
 * @author Yasser Ganjisaffar
 */
@SpringBootApplication(scanBasePackages = {"com.ocbang.tools.crawler"})
public class MainCrawlController implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(MainCrawlController.class);

    @Autowired
    private WPPostsDAO wpPostsDAO;

    @Autowired
    private WPTermsDAO wpTermsDAO;

    @Autowired
    private InternshipsJobEntityQualifier internshipsJobEntityQualifier;

    public MainCrawlController() {
    }

    @Override
    public void run(String... args) throws Exception {

//        CloseableHttpClient httpClient = LinkedinHttpClientBuilder.create();
//        LinkedinLoginController loginController = new LinkedinLoginController(httpClient, "garyfake@mail.com", "1234abcd!@#$");
//        loginController.login();
        CloseableHttpClient httpClient = InternshipsHttpClientBuilder.create();

        logger.info("Visit home page ... ");
        CloseableHttpResponse response;
        HttpGet get = new HttpGet("http://www.internships.com/student");
        response = httpClient.execute(get);
        response.close();

        Thread.sleep(1000);

        //Job list
        logger.info("Visit jobs list page ... ");
        get = new HttpGet("http://www.internships.com/search/posts?Keywords=&Location=");
        get.setHeader("Referer", "http://www.internships.com/student");
        response = httpClient.execute(get);
        InternshipsJobListPage internshipsJobListPage =
                InternshipsJobListPage.createFromEntity(response.getEntity(),
                        "http://www.internships.com/");
        response.close();

        Thread.sleep(1000);

        //Job details
        List<InternshipsJobListItem> jobListItems = internshipsJobListPage.extractJobListItems();
        for (InternshipsJobListItem jobListItem:jobListItems) {
            logger.info("Processing {}", jobListItem.toString());
            get = new HttpGet(jobListItem.getUrl());
            get.setHeader("Referer", "http://www.internships.com/search/posts?Keywords=&Location=");
            response = httpClient.execute(get);
            InternshipsJobDetailPage internshipsJobDetailPage = InternshipsJobDetailPage.createFromEntity(response.getEntity());
            InternshipsJobEntity internshipsJobEntity = internshipsJobDetailPage.produceNoojobEntity();
            if(!this.internshipsJobEntityQualifier.isQualified(internshipsJobEntity)) {
                logger.info("Not qualified，drop it");
                logger.debug(internshipsJobEntity.toString());
            }else {
                logger.info("Import into wordpress db");
                wpPostsDAO.init(internshipsJobEntity);
                if(wpPostsDAO.save()) {
                    wpTermsDAO.init(wpPostsDAO.getId(), jobListItem);
                    wpTermsDAO.save();
                }
            }

            Thread.sleep(1000);
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(MainCrawlController.class, args);
    }

}