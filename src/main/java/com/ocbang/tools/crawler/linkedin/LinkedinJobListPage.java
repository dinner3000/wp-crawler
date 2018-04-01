package com.ocbang.tools.crawler.linkedin;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class LinkedinJobListPage {

    public static List<String> getJobDetailPageUrls(String html){
        Document doc = Jsoup.parse(html);
        Elements links = doc.select("a[href]");
        List<String> urlList = new ArrayList<>();
        links.stream().forEach(l -> {
            urlList.add(l.attr("abs:href"));
        });
        return urlList;
    }
}
