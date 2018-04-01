package com.ocbang.tools.crawler.internships;

import com.ocbang.tools.crawler.wordpress.NoojobJobEntity;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class InternshipsJobDetailPage {
    private static Logger logger = LoggerFactory.getLogger(InternshipsJobDetailPage.class);

    public static InternshipsJobDetailPage createFromEntity(HttpEntity entity) throws IOException {
        return new InternshipsJobDetailPage(entity);
    }

    private Document document;

    protected InternshipsJobDetailPage(HttpEntity entity) throws IOException {
        this.document = Jsoup.parse(EntityUtils.toString(entity));
    }

    public NoojobJobEntity produceNoojobEntity() {
        NoojobJobEntity noojobJobEntity = new NoojobJobEntity();

        Element title = this.document.select("div[class=internship-detail-header] h1[class]").first();
        Element company = this.document.select("div[class=company-info] td[class=company-detail] div[class=company-name]>span").first();
        Element location = this.document.select("div[class=company-info] td[class=company-detail] div[class=location]").first();
        Element posted = this.document.select("div[class=company-info] td[class=company-detail] div[class=location]+div").first();
        Element internInfo = this.document.select("div[class=i-info]").first();
        Element description = this.document.select("div[class=section description]").first();
        Element responsibilities = this.document.select("div[class=section responsibilities]").first();
        Element requirements = this.document.select("div[class=section requirements]").first();

        if (title != null) {
            noojobJobEntity.setTitle(title.text());
        }

        if (company != null) {
            noojobJobEntity.setCompany(company.text());
        }

        if (location != null) {
            noojobJobEntity.setLocation(location.text());
        }

        if (posted != null) {
            noojobJobEntity.setPosted(posted.text());
        }

        if (internInfo != null) {
            noojobJobEntity.setInternInfo(internInfo.html());
        }

        if (description != null) {
            noojobJobEntity.setDescription(description.html());
        }

        if (responsibilities != null) {
            noojobJobEntity.setResponsibilities(responsibilities.html());
        }

        if (requirements != null) {
            noojobJobEntity.setRequirements(requirements.html());
        }

        return noojobJobEntity;
    }

}
