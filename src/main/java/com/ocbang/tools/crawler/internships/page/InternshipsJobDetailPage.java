package com.ocbang.tools.crawler.internships.page;

import com.ocbang.tools.crawler.internships.entity.InternshipsJobDetailEntity;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Iterator;

public class InternshipsJobDetailPage {
    private static Logger logger = LoggerFactory.getLogger(InternshipsJobDetailPage.class);

    public static InternshipsJobDetailPage createFromEntity(HttpEntity entity) throws IOException {
        return new InternshipsJobDetailPage(entity);
    }

    private Document document;

    protected InternshipsJobDetailPage(HttpEntity entity) throws IOException {
        this.document = Jsoup.parse(EntityUtils.toString(entity));
    }

    public InternshipsJobDetailEntity produceJobDetailEntity() {
        InternshipsJobDetailEntity internshipsJobDetailEntity = new InternshipsJobDetailEntity();

        Element category = this.document.select("ul[class=breadcrumb] li:nth-child(2) a").first();
        Element title = this.document.select("div[class=internship-detail-header] h1[class]").first();
        Element company = this.document.select("div[class=company-info] td[class=company-detail] div[class=company-name]>span").first();
        Element location = this.document.select("div[class=company-info] td[class=company-detail] div[class=location]").first();
        Element posted = this.document.select("div[class=company-info] td[class=company-detail] div[class=location]+div").first();
        Element internInfo = this.document.select("div[class=i-info]").first();
        Element description = this.document.select("div[class=section description]").first();
        Element responsibilities = this.document.select("div[class=section responsibilities]").first();
        Element requirements = this.document.select("div[class=section requirements]").first();

        if(category != null){
            internshipsJobDetailEntity.setCategory(category.text());
        }

        if (title != null) {
            internshipsJobDetailEntity.setTitle(title.text());
        }

        if (company != null) {
            internshipsJobDetailEntity.setCompany(company.text());
        }

        if (location != null) {
            internshipsJobDetailEntity.setLocation(location.text());
        }

        if (posted != null) {
            internshipsJobDetailEntity.setPosted(posted.text().replaceAll("(?i)Posted:", "").trim());
        }

        if (internInfo != null) {
            internshipsJobDetailEntity.setInternInfo(internInfo.html());

            Elements eles = internInfo.select("div>div>strong,span");
            Element ele = null;
            Iterator it = eles.iterator();
            while (it.hasNext()){
                ele = (Element)it.next();
                if(ele.text().toLowerCase().contains("deadline")){
                    //Application Deadline: May 4, 2018
                    ele = (Element)it.next();
                    internshipsJobDetailEntity.setDeadline(ele.text().replaceAll("(?i)Application Deadline:", "").trim());
                } else if(ele.text().toLowerCase().contains("position")){
                    //Position: 10 Full-time, Unpaid
                    ele = (Element)it.next();
                    internshipsJobDetailEntity.setPosition(ele.text().replaceAll("(?i)Position:", "").trim());
                } else if(ele.text().toLowerCase().contains("timeframe")){
                    //Timeframe: 04/05/18 — 07/05/18
                    ele = (Element)it.next();
                    String text = ele.text().replaceAll("(?i)Timeframe:", "").trim();
                    internshipsJobDetailEntity.setTimeframe(text);
                    String[] buf = text.split("—");
                    if(buf.length>1){
                        internshipsJobDetailEntity.setStartDate(buf[0]);
                        internshipsJobDetailEntity.setEndDate(buf[1]);
                    }
                }
            }
        }

        if (description != null) {
            internshipsJobDetailEntity.setDescription(description.html());
        }

        if (responsibilities != null) {
            internshipsJobDetailEntity.setResponsibilities(responsibilities.html());
        }

        if (requirements != null) {
            internshipsJobDetailEntity.setRequirements(requirements.html());
        }

        return internshipsJobDetailEntity;
    }

}
