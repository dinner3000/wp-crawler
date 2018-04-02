package com.ocbang.tools.crawler.internships;

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

    public InternshipsJobEntity produceNoojobEntity() {
        InternshipsJobEntity internshipsJobEntity = new InternshipsJobEntity();

        Element title = this.document.select("div[class=internship-detail-header] h1[class]").first();
        Element company = this.document.select("div[class=company-info] td[class=company-detail] div[class=company-name]>span").first();
        Element location = this.document.select("div[class=company-info] td[class=company-detail] div[class=location]").first();
        Element posted = this.document.select("div[class=company-info] td[class=company-detail] div[class=location]+div").first();
        Element internInfo = this.document.select("div[class=i-info]").first();
        Element description = this.document.select("div[class=section description]").first();
        Element responsibilities = this.document.select("div[class=section responsibilities]").first();
        Element requirements = this.document.select("div[class=section requirements]").first();

        if (title != null) {
            internshipsJobEntity.setTitle(title.text());
        }

        if (company != null) {
            internshipsJobEntity.setCompany(company.text());
        }

        if (location != null) {
            internshipsJobEntity.setLocation(location.text());
        }

        if (posted != null) {
            internshipsJobEntity.setPosted(posted.text());
        }

        if (internInfo != null) {
            internshipsJobEntity.setInternInfo(internInfo.html());
        }

        if (description != null) {
            internshipsJobEntity.setDescription(description.html());
        }

        if (responsibilities != null) {
            internshipsJobEntity.setResponsibilities(responsibilities.html());
        }

        if (requirements != null) {
            internshipsJobEntity.setRequirements(requirements.html());
        }

        return internshipsJobEntity;
    }

}
