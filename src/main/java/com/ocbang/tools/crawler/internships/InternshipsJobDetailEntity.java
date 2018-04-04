package com.ocbang.tools.crawler.internships;

public class InternshipsJobDetailEntity {

    //Data come from crawler
    private String title;
    private String company;
    private String posted;
    private String location;
    private String description;
    private String responsibilities;
    private String requirements;

    private String internInfo;
    private String deadline;
    private String position;
    private String timeframe;
    private String startDate;
    private String endDate;

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

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getTimeframe() {
        return timeframe;
    }

    public void setTimeframe(String timeframe) {
        this.timeframe = timeframe;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "InternshipsJobDetailEntity{" +
                "title='" + title + '\'' +
                ", company='" + company + '\'' +
                ", posted='" + posted + '\'' +
                ", location='" + location + '\'' +
                ", description='" + description + '\'' +
                ", responsibilities='" + responsibilities + '\'' +
                ", requirements='" + requirements + '\'' +
                ", internInfo='" + internInfo + '\'' +
                ", deadline='" + deadline + '\'' +
                ", position='" + position + '\'' +
                ", timeframe='" + timeframe + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                '}';
    }
}
