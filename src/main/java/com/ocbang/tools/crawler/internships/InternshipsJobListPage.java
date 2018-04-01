package com.ocbang.tools.crawler.internships;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public List<String> getJobDetailPageUrls(){

        List<String> urlList = new ArrayList<>();
        try {
            Element container = this.document.select("div[class=pnl]").first();
            Elements links = container.select("a[data-job-id]");
            links.stream().forEach(l -> {
                String url = l.absUrl("href");
                if (InternshipsJobDetailsPageUrlPatterns.isMatch(url)) {
                    urlList.add(url);
                }
            });
        }catch (Exception e){
            logger.error(e.getMessage());
        }
        return urlList;
    }
}
