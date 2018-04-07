package com.ocbang.tools.crawler.internships.entity;

import org.springframework.util.StringUtils;

public class InternshipsJobLocationEntity {
    private String city;
    private String stat;
    private String country;

    public static InternshipsJobLocationEntity parseFromCrawledText(String text){
        InternshipsJobLocationEntity locationEntity = new InternshipsJobLocationEntity();

        if(!StringUtils.isEmpty(text)) {
            String[] buf = text.split(",");
            if (buf.length > 0) {
                locationEntity.city = buf[0];
            }
            if (buf.length > 1) {
                locationEntity.stat = buf[1];
            }
        }else {
            return null;
        }

        return locationEntity;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStat() {
        return stat;
    }

    public void setStat(String stat) {
        this.stat = stat;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
