package com.ocbang.tools.crawler.internships.controller;

import com.ocbang.tools.crawler.internships.entity.InternshipsJobDetailEntity;
import com.ocbang.tools.crawler.internships.entity.InternshipsJobSummaryEntity;
import com.ocbang.tools.crawler.internships.helper.InternshipsHttpClientBuilder;
import com.ocbang.tools.crawler.internships.helper.InternshipsJobDetailEntityQualifier;
import com.ocbang.tools.crawler.internships.page.InternshipsJobDetailPage;
import com.ocbang.tools.crawler.wordpress.service.CompanyService;
import com.ocbang.tools.crawler.wordpress.service.JobService;
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

    @Value("${internships.home-page-url}")
    private String homePageUrl;

    @Autowired
    InternshipsJobSearchController jobSearchController;

    @Autowired
    private JobService jobService;
    @Autowired
    private CompanyService companyService;

    @Autowired
    private InternshipsJobDetailEntityQualifier internshipsJobDetailEntityQualifier;

    public void crawlByCompany() throws Exception {
        CloseableHttpClient httpClient = InternshipsHttpClientBuilder.create();

        logger.info("Visit home page ... ");
        CloseableHttpResponse response;
        HttpGet get = new HttpGet(this.homePageUrl);
        response = httpClient.execute(get);
        response.close();

        logger.info("Wait for 1 sec ... ");
        Thread.sleep(1000);


        for(String company : companyService.getCompanyList()){
            logger.info("Search job of company: {}", company);
            List<InternshipsJobSummaryEntity> jobSummaryEntities = jobSearchController.searchByCompany(httpClient, company,1);
            logger.info("{} job items found", jobSummaryEntities.size());

            logger.info("Wait for 3 sec ... ");
            Thread.sleep(3000);

            //Job details
            for (InternshipsJobSummaryEntity jobSummaryEntity : jobSummaryEntities) {

                logger.info("Processing: {}", jobSummaryEntity.toString());
                get = new HttpGet(jobSummaryEntity.getUrl());
                get.setHeader("Referer", jobSearchController.getSearchUrl());
                response = httpClient.execute(get);

                InternshipsJobDetailPage internshipsJobDetailPage = InternshipsJobDetailPage.createFromEntity(response.getEntity());
                InternshipsJobDetailEntity jobDetailEntity = internshipsJobDetailPage.produceJobDetailEntity();
                logger.info("Parsed entity: {}", jobDetailEntity.toString());

                if (!this.internshipsJobDetailEntityQualifier.isQualified(jobDetailEntity)) {
                    logger.info("Not qualified，drop it");
                } else {
                    logger.info("Import into db ... ");
                    jobService.addNewOne(jobSummaryEntity, jobDetailEntity);
                }

                logger.info("Wait for 3 sec ... ");
                Thread.sleep(3000);
            }

            logger.info("Wait for 10 sec ... ");
            Thread.sleep(10000);
        }
    }

    public void crawlByDefault() throws Exception {
        CloseableHttpClient httpClient = InternshipsHttpClientBuilder.create();

        logger.info("Visit home page ... ");
        CloseableHttpResponse response;
        HttpGet get = new HttpGet(this.homePageUrl);
        response = httpClient.execute(get);
        response.close();

        logger.info("Wait for 1 sec ... ");
        Thread.sleep(1000);


        for(int i = 1; i <= 3; i++){
            logger.info("Visit job page: {}", i);
            List<InternshipsJobSummaryEntity> jobSummaryEntities = jobSearchController.searchByDefault(httpClient,i);
            logger.info("{} job items found", jobSummaryEntities.size());

            logger.info("Wait for 3 sec ... ");
            Thread.sleep(3000);

            //Job details
            for (InternshipsJobSummaryEntity jobSummaryEntity : jobSummaryEntities) {

                logger.info("Processing: {}", jobSummaryEntity.toString());
                get = new HttpGet(jobSummaryEntity.getUrl());
                get.setHeader("Referer", jobSearchController.getSearchUrl());
                response = httpClient.execute(get);

                InternshipsJobDetailPage internshipsJobDetailPage = InternshipsJobDetailPage.createFromEntity(response.getEntity());
                InternshipsJobDetailEntity jobDetailEntity = internshipsJobDetailPage.produceJobDetailEntity();
                logger.info("Parsed entity: {}", jobDetailEntity.toString());

                if (!this.internshipsJobDetailEntityQualifier.isQualified(jobDetailEntity)) {
                    logger.info("Not qualified，drop it");
                } else {
                    logger.info("Import into db ... ");
                    jobService.addNewOne(jobSummaryEntity, jobDetailEntity);
                }

                logger.info("Wait for 3 sec ... ");
                Thread.sleep(3000);
            }

            logger.info("Wait for 10 sec ... ");
            Thread.sleep(10000);
        }
    }
}
