package com.ocbang.tools.crawler.internships;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InternshipsJobListPage {

    private static Logger logger = LoggerFactory.getLogger(InternshipsJobListPage.class);

    public static InternshipsJobListPage createFromEntity(HttpEntity entity, String baseUrl) throws IOException {
        return new InternshipsJobListPage(entity, baseUrl);
    }

    private Document document;

    protected InternshipsJobListPage(HttpEntity entity, String baseUrl) throws IOException {
        this.document = Jsoup.parse(EntityUtils.toString(entity), baseUrl);
    }

    public List<InternshipsJobSummaryEntity> extractJobListItems() throws IOException {
        List<InternshipsJobSummaryEntity> jobList = new ArrayList<>();

        Elements listItems = this.document.select("div[class=pnl] div[class=internship-result-link-item");
        listItems.stream().forEach(listItem -> {
            Element url = listItem.select("a[data-job-id]").first();
            Element company = listItem.select("span[class=company-name]>span").first();
            Element location = listItem.select("span[class=internship-location]").first();
            Element posted = listItem.select("span[class=date-title]").first();
            Element timeType = listItem.select("span[class=job-type job-part-time]").first();
            Element payType = listItem.select("span[class=job-type job-paid]").first();

            String absUrl = null;
            String category = null;
            if(url != null) {
                absUrl = url.absUrl("href");
                String[] buffer = url.attr("href").split("/");
                if(buffer.length > 1) category = buffer[1];
            }

            if(!StringUtils.isEmpty(absUrl) && InternshipsJobDetailPageUrlQualifier.isQualified(absUrl)){

                InternshipsJobSummaryEntity jobItem = new InternshipsJobSummaryEntity();
                jobItem.setUrl(absUrl);
                if(category != null) jobItem.setCategory(category);
                if(company != null) jobItem.setCompany(company.text());
                if(location != null) jobItem.setLocation(location.text());
                if(posted != null) jobItem.setPosted(posted.text());
                if(timeType != null) jobItem.setTimeType(timeType.text());
                if(payType != null) jobItem.setPayType(payType.text());
                jobList.add(jobItem);
            }
        });
        return jobList;
    }
}
