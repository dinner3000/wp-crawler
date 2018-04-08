package com.ocbang.tools.crawler.wordpress.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Service
public class JobCrawlResultCollector {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private int jobListPage;
    private int jobDetailPage;
    private int totalJobs;
    private int newJobs;
    private int dupJobs;
    private int errorJobs;

    private Instant startTime;

    public JobCrawlResultCollector() {
        startTime = Instant.now();
    }

    public void AddJobListPage() {
        jobListPage++;
    }

    public void AddJobDetailPage() {
        jobDetailPage++;
    }

    public void AddJob() {
        totalJobs++;
    }

    public void AddNewJob() {
        newJobs++;
    }

    public void AddDupJob() {
        dupJobs++;
    }

    public void AddErrorJob() {
        errorJobs++;
    }

    @Override
    public String toString() {
        return "JobCrawlResultCollector{" +
                "jobListPage=" + jobListPage +
                ", jobDetailPage=" + jobDetailPage +
                ", totalJobs=" + totalJobs +
                ", newJobs=" + newJobs +
                ", dupJobs=" + dupJobs +
                ", errorJobs=" + errorJobs +
                '}';
    }

    public void report() {
        Duration duration = Duration.between(startTime, Instant.now());

        logger.info("time cost: {}", duration.toMinutes());
        logger.info("jobListPage: {}", jobListPage);
        logger.info("jobDetailPage: {}", jobDetailPage);
        logger.info("totalJobs: {}", totalJobs);
        logger.info("newJobs: {}", newJobs);
        logger.info("dupJobs: {}", dupJobs);
        logger.info("errorJobs: {}", errorJobs);
    }
}
