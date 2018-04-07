package com.ocbang.tools.crawler.internships.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InternshipsJobSummaryEntity {
    private static Logger logger = LoggerFactory.getLogger(InternshipsJobSummaryEntity.class);

    private String url;
    private String category;
    private String company;
    private String location;
    private String posted;
    private String timeType;
    private String payType;

    @Override
    public String toString() {
        return "InternshipsJobSummaryEntity{" +
                "url='" + url + '\'' +
                ", company='" + company + '\'' +
                ", location='" + location + '\'' +
                ", posted='" + posted + '\'' +
                ", timeType='" + timeType + '\'' +
                ", payType='" + payType + '\'' +
                '}';
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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
