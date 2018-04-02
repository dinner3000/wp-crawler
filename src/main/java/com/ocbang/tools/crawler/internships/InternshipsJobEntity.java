package com.ocbang.tools.crawler.internships;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

public class InternshipsJobEntity {

    //Data come from crawler
    private String title;
    private String company;
    private String posted;
    private String location;
    private String internInfo;
    private String description;
    private String responsibilities;
    private String requirements;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getPosted() {
        return posted;
    }

    public void setPosted(String posted) {
        this.posted = posted;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getInternInfo() {
        return internInfo;
    }

    public void setInternInfo(String internInfo) {
        this.internInfo = internInfo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getResponsibilities() {
        return responsibilities;
    }

    public void setResponsibilities(String responsibilities) {
        this.responsibilities = responsibilities;
    }

    public String getRequirements() {
        return requirements;
    }

    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }

    @Override
    public String toString() {
        return "InternshipsJobEntity{" +
                "title='" + title + '\'' +
                ", company='" + company + '\'' +
                ", posted='" + posted + '\'' +
                ", location='" + location + '\'' +
                ", internInfo='" + internInfo + '\'' +
                ", description='" + description + '\'' +
                ", responsibilities='" + responsibilities + '\'' +
                ", requirements='" + requirements + '\'' +
                '}';
    }
}
