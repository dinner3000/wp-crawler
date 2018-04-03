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
    private String category;
    private String company;
    private String location;
    private String posted;
    private String timeType;
    private String payType;

    protected InternshipsJobListItem() {
    }

    @Override
    public String toString() {
        return "InternshipsJobListItem{" +
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
