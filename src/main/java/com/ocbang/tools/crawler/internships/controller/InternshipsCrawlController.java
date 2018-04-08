package com.ocbang.tools.crawler.internships.controller;

import com.ocbang.tools.crawler.internships.entity.InternshipsJobDetailEntity;
import com.ocbang.tools.crawler.internships.entity.InternshipsJobSummaryEntity;
import com.ocbang.tools.crawler.internships.helper.InternshipsHttpClientBuilder;
import com.ocbang.tools.crawler.internships.helper.InternshipsJobDetailEntityQualifier;
import com.ocbang.tools.crawler.internships.page.InternshipsJobDetailPage;
import com.ocbang.tools.crawler.wordpress.helper.JobCrawlResultCollector;
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

import java.io.IOException;
import java.util.List;

@Component
public class InternshipsCrawlController {

    private static final Logger logger = LoggerFactory.getLogger(InternshipsCrawlController.class);

    @Value("${internships.home-page-url}")
    private String homePageUrl;
    @Value("${internships.crawl-interval}")
    private int crawlInterval;
    @Value("${internships.start-page}")
    private int startPage;
    @Value("${internships.end-page}")
    private int endPage;

    @Autowired
    InternshipsJobSearchController jobSearchController;

    @Autowired
    private JobService jobService;
    @Autowired
    private CompanyService companyService;

    @Autowired
    JobCrawlResultCollector resultCollector;

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
            for(int i = startPage; i<= endPage; i++) {
                List<InternshipsJobSummaryEntity> jobSummaryEntities = jobSearchController.searchByCompany(httpClient, company, i);
                resultCollector.AddJobListPage();
                logger.info("{} job items found", jobSummaryEntities.size());

                logger.info("Wait for {} sec ... ", crawlInterval);
                Thread.sleep(1000 * crawlInterval);

                //Job details
                for (InternshipsJobSummaryEntity jobSummaryEntity : jobSummaryEntities) {
                    crawlDetailPage(httpClient, jobSummaryEntity);
                    resultCollector.AddJobDetailPage();

                    logger.info("Wait for {} sec ... ", crawlInterval);
                    Thread.sleep(1000 * crawlInterval);
                }
            }

            logger.info("Wait for {} sec ... ", crawlInterval);
            Thread.sleep(1000 * crawlInterval);
        }

        resultCollector.report();
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


        for(int i = startPage; i <= endPage; i++){
            logger.info("Visit job page: {}", i);
            List<InternshipsJobSummaryEntity> jobSummaryEntities = jobSearchController.searchByDefault(httpClient,i);
            logger.info("{} job items found", jobSummaryEntities.size());
            resultCollector.AddJobListPage();

            logger.info("Wait for {} sec ... ", crawlInterval);
            Thread.sleep(1000 * crawlInterval);

            //Job details
            for (InternshipsJobSummaryEntity jobSummaryEntity : jobSummaryEntities) {
                crawlDetailPage(httpClient, jobSummaryEntity);
                resultCollector.AddJobDetailPage();

                logger.info("Wait for {} sec ... ", crawlInterval);
                Thread.sleep(1000 * crawlInterval);
            }

            logger.info("Wait for {} sec ... ", crawlInterval);
            Thread.sleep(1000 * crawlInterval);
        }

        resultCollector.report();
    }

    protected void crawlDetailPage(CloseableHttpClient httpClient, InternshipsJobSummaryEntity jobSummaryEntity) throws InterruptedException, IOException {
        logger.info("Processing: {}", jobSummaryEntity.toString());
        HttpGet get = new HttpGet(jobSummaryEntity.getUrl());
        get.setHeader("Referer", jobSearchController.getSearchUrl());
        CloseableHttpResponse response = httpClient.execute(get);

        InternshipsJobDetailPage internshipsJobDetailPage = InternshipsJobDetailPage.createFromEntity(response.getEntity());
        InternshipsJobDetailEntity jobDetailEntity = internshipsJobDetailPage.produceJobDetailEntity();
        logger.info("Parsed entity: {}", jobDetailEntity.toString());

        resultCollector.AddJob();
        if (!this.internshipsJobDetailEntityQualifier.isQualified(jobDetailEntity)) {
            logger.info("Job data not qualified，drop it");
            resultCollector.AddErrorJob();
        } else {
            logger.info("Job data qualified, Write into db ... ");
            if(jobService.addNewOne(jobSummaryEntity, jobDetailEntity)){
                resultCollector.AddNewJob();
            }else {
                resultCollector.AddDupJob();
            }
        }
    }
}
