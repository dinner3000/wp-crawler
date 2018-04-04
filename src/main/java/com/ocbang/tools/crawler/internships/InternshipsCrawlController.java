package com.ocbang.tools.crawler.internships;

import com.ocbang.tools.crawler.wordpress.WPPostsDAO;
import com.ocbang.tools.crawler.wordpress.WPTermsDAO;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InternshipsCrawlController {

    private static final Logger logger = LoggerFactory.getLogger(InternshipsCrawlController.class);

    @Value("${internships.baseUrl}")
    private String baseUrl;
    @Value("${internships.homePageUrl}")
    private String homePageUrl;
    @Value("${internships.jobListPageUrl}")
    private String jobListPageUrl;

    @Autowired
    private WPPostsDAO wpPostsDAO;

    @Autowired
    private WPTermsDAO wpTermsDAO;

    @Autowired
    private InternshipsJobDetailEntityQualifier internshipsJobDetailEntityQualifier;

    public void run() throws Exception {
        CloseableHttpClient httpClient = InternshipsHttpClientBuilder.create();

        logger.info("Visit home page ... ");
        CloseableHttpResponse response;
        HttpGet get = new HttpGet(this.homePageUrl);
        response = httpClient.execute(get);
        response.close();

        Thread.sleep(1000);

        //Job list
        logger.info("Visit jobs list page ... ");
        get = new HttpGet(this.jobListPageUrl);
        get.setHeader("Referer", this.homePageUrl);
        response = httpClient.execute(get);
        InternshipsJobListPage internshipsJobListPage =
                InternshipsJobListPage.createFromEntity(response.getEntity(), this.baseUrl);
        response.close();

        Thread.sleep(1000);

        //Job details
        List<InternshipsJobSummaryEntity> jobSummaryEntities = internshipsJobListPage.extractJobListItems();
        for (InternshipsJobSummaryEntity jobSummaryEntity : jobSummaryEntities) {
            logger.info("Processing {}", jobSummaryEntity.toString());
            get = new HttpGet(jobSummaryEntity.getUrl());
            get.setHeader("Referer", this.jobListPageUrl);
            response = httpClient.execute(get);
            InternshipsJobDetailPage internshipsJobDetailPage = InternshipsJobDetailPage.createFromEntity(response.getEntity());
            InternshipsJobDetailEntity jobDetailEntity = internshipsJobDetailPage.produceJobDetailEntity();
            if (!this.internshipsJobDetailEntityQualifier.isQualified(jobDetailEntity)) {
                logger.info("Not qualified，drop it");
                logger.debug(jobDetailEntity.toString());
            } else {
                logger.info("Import into wordpress db");
                wpPostsDAO.init(jobSummaryEntity, jobDetailEntity);
                if (wpPostsDAO.save()) {
                    wpTermsDAO.init(wpPostsDAO.getId(), jobSummaryEntity);
                    wpTermsDAO.save();
                }
            }

            Thread.sleep(1000);
        }
    }

}
