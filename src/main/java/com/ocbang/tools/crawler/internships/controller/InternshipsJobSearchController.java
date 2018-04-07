package com.ocbang.tools.crawler.internships.controller;

import com.ocbang.tools.crawler.internships.entity.InternshipsJobSummaryEntity;
import com.ocbang.tools.crawler.internships.page.InternshipsJobListPage;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.List;

@Controller
public class InternshipsJobSearchController {

    private static final Logger logger = LoggerFactory.getLogger(InternshipsJobSearchController.class);

    @Value("${internships.base-url}")
    private String baseUrl;
    @Value("${internships.home-page-url}")
    private String homePageUrl;

    //http://www.internships.com/search/posts?Keywords=&Location=&Radius=Twenty
    // &Company=Ford&ListingType=Internship&Sort=MostRecent&FilterBy=&Page=1
    @Value("${internships.base-url}/searchByCompany/posts")
    private String searchUrlTemplate;
    private String searchUrl;

    public String getSearchUrl() {
        return searchUrl;
    }

    private URIBuilder uriBuilder;

    @PostConstruct
    protected void initUriBuilder() throws URISyntaxException {
        uriBuilder = new URIBuilder(this.searchUrlTemplate);
        uriBuilder.addParameter("Keywords", "");
        uriBuilder.addParameter("Location", "");
        uriBuilder.addParameter("Radius", "Twenty");
        uriBuilder.addParameter("ListingType", "Internship");
        uriBuilder.addParameter("Sort", "MostRecent");
        uriBuilder.addParameter("FilterBy", "");
        uriBuilder.addParameter("Company", "");
        uriBuilder.addParameter("Page", "1");
    }

    public List<InternshipsJobSummaryEntity> searchByCompany(CloseableHttpClient httpClient, String company, int pageIndex) throws IOException, URISyntaxException {
        uriBuilder.setParameter("Company", company);
        uriBuilder.setParameter("Page", String.valueOf(pageIndex));

        URI uri = uriBuilder.build();
        searchUrl = String.format("http://www.internships.com/search/posts?" +
                "Keywords=&Location=&Radius=Twenty&Company=%s&" +
                "ListingType=Internship&Sort=MostRecent&FilterBy=&Page=%d", URLEncoder.encode(company, "UTF-8"), pageIndex);
//        searchUrl = uri.toString();
        logger.info("Visit jobs list page: {}", searchUrl);

        HttpGet get = new HttpGet(searchUrl);
//        HttpGet get = new HttpGet(uri);
        get.setHeader("Referer", "http://www.internships.com/search/posts?Keywords=&Location=");
        CloseableHttpResponse response = httpClient.execute(get);
        InternshipsJobListPage internshipsJobListPage =
                InternshipsJobListPage.createFromEntity(response.getEntity(), this.baseUrl);
        response.close();

        return internshipsJobListPage.extractJobListItems();
    }
}
