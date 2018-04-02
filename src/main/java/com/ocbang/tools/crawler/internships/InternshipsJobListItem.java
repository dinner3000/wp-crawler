package com.ocbang.tools.crawler.internships;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class InternshipsJobListItem {
    private static Logger logger = LoggerFactory.getLogger(InternshipsJobListItem.class);

    private String url;
    private String company;
    private String location;
    private String posted;
    private String timeType;
    private String payType;

    public static List<InternshipsJobListItem> createFromEntity(HttpEntity entity, String baseUrl) throws IOException {
        List<InternshipsJobListItem> ret = new ArrayList<>();

        Document document = Jsoup.parse(EntityUtils.toString(entity), baseUrl);
        Elements jobListItems = document.select("div[class=pnl] div[class=internship-result-link-item");
        jobListItems.stream().forEach(jobListItem -> {
            Element url = jobListItem.select("a[data-job-id]").first();
            Element company = jobListItem.select("span[class=company-name]>span").first();
            Element location = jobListItem.select("span[class=internship-location]").first();
            Element posted = jobListItem.select("span[class=date-title]").first();
            Element timeType = jobListItem.select("span[class=job-type job-part-time]").first();
            Element payType = jobListItem.select("span[class=job-type job-paid]").first();
            if(url != null){
                InternshipsJobListItem item = new InternshipsJobListItem();
                item.setUrl(url.absUrl("href"));
                if(company != null) item.setCompany(company.text());
                if(location != null) item.setLocation(location.text());
                if(posted != null) item.setPosted(posted.text());
                if(timeType != null) item.setTimeType(timeType.text());
                if(payType != null) item.setPayType(payType.text());
                ret.add(item);
            }
        });
        return ret;
    }

    protected InternshipsJobListItem() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPosted() {
        return posted;
    }

    public void setPosted(String posted) {
        this.posted = posted;
    }

    public String getTimeType() {
        return timeType;
    }

    public void setTimeType(String timeType) {
        this.timeType = timeType;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

}
