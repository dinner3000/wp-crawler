package com.ocbang.tools.crawler.wordpress.service;

import com.ocbang.tools.crawler.internships.entity.InternshipsJobDetailEntity;
import com.ocbang.tools.crawler.internships.entity.InternshipsJobLocationEntity;
import com.ocbang.tools.crawler.internships.entity.InternshipsJobSummaryEntity;
import com.ocbang.tools.crawler.wordpress.dao.PostmetaDao;
import com.ocbang.tools.crawler.wordpress.dao.PostsDao;
import com.ocbang.tools.crawler.wordpress.dao.TermRelationshipsDao;
import com.ocbang.tools.crawler.wordpress.dao.TermTaxonomyDao;
import com.ocbang.tools.crawler.wordpress.entity.WpPostmetaEntity;
import com.ocbang.tools.crawler.wordpress.entity.WpPostsEntity;
import com.ocbang.tools.crawler.wordpress.helper.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class JobService {

    Logger logger = LoggerFactory.getLogger(JobService.class);

    @Autowired
    private CompanyService companyService;
    @Autowired
    private JobCategoryService jobCategoryService;
    @Autowired
    private JobLocationService jobLocationService;
    @Autowired
    private JobTypeService jobTypeService;

    @Autowired
    PostsDao postsDao;
    @Autowired
    PostmetaDao postmetaDao;
    @Autowired
    protected TermRelationshipsDao termRelationshipsDao;
    @Autowired
    protected TermTaxonomyDao termTaxonomyDao;

    //0000-00-00 00:00:00
    private DateFormat dbDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    //April 04 2018
    private DateFormat postedDateFormat = new SimpleDateFormat("MMM dd yyyy");
    //May 4, 2018
    private DateFormat deadlineDateFormat = new SimpleDateFormat("MMM dd, yyyy");
    //04/05/18 — 07/05/18
    private DateFormat timeframeDateFormat = new SimpleDateFormat("MM/dd/yy");

    @Value("${wordpress.default-post-author}")
    private Long defaultPostAuthor;
    @Value("${wordpress.baseUrl}/?p=%d")
    private String guidTemplate;

    @Transactional
    public void addNewOne(InternshipsJobSummaryEntity jobSummaryEntity, InternshipsJobDetailEntity jobDetailEntity) {

        //Skip if job already exist
        if (postsDao.getPostCountByComboKeys(this.defaultPostAuthor,
                StringHelper.sanitizeTitle(jobDetailEntity.getTitle()),
                this.extractPostedDate(jobDetailEntity.getPosted())) > 0) {
            logger.info("Job already exists, skip ... ");
            return;
        }

        if(companyService.tryGetExistingId(jobSummaryEntity.getCompany()) <= 0){
            logger.info("Not in company list, skip ... ");
            return;
        }

        //Save job details
        Long id = this.addNewPost(jobSummaryEntity, jobDetailEntity);

        //Save job meta
        this.addNewPostmetas(id, jobSummaryEntity, jobDetailEntity);

    }

    @Transactional
    protected Long addNewPost(InternshipsJobSummaryEntity jobSummaryEntity, InternshipsJobDetailEntity jobDetailEntity) {

        //Find out category id or create a new one
        Long categoryId = 0L;
        if(!StringUtils.isEmpty(jobDetailEntity.getCategory())) {
            categoryId = jobCategoryService.tryGetExistingId(jobDetailEntity.getCategory());
            if (categoryId == 0) {
                categoryId = jobCategoryService.addNewOne(jobDetailEntity.getCategory());
            }
        }

        //Find out type id
        Long typeId = 0L;
        if(!StringUtils.isEmpty(jobSummaryEntity.getTimeType())) {
            typeId = jobTypeService.tryGetExistingId(jobSummaryEntity.getTimeType());
        }

        //Find out location id or create a new one
        InternshipsJobLocationEntity locationEntity =
                InternshipsJobLocationEntity.parseFromCrawledText(jobSummaryEntity.getLocation());
        Long locationId = 0L;
        if (locationEntity != null && StringUtils.isEmpty(locationEntity.getCity())) {
            locationId = jobLocationService.tryGetExistingId(locationEntity.getCity());
            if (locationId == 0L) {
                locationId = jobLocationService.addNewOne(locationEntity.getCity());
            }
        }

        //Create job details entity with default values
        WpPostsEntity postsEntity = this.buildDefaultPostsEntity();

        //Set other job details with crawled data
        postsEntity.setPostTitle(jobDetailEntity.getTitle());
        postsEntity.setPostName(StringHelper.sanitizeTitle(jobDetailEntity.getTitle()));

        Timestamp postedDate = this.extractPostedDate(jobDetailEntity.getPosted());
        postsEntity.setPostDate(postedDate); //Format: 2018-03-31 15:55:32
        postsEntity.setPostDateGmt(postedDate);
        postsEntity.setPostModified(postedDate);
        postsEntity.setPostModifiedGmt(postedDate);
        postsEntity.setPostContent(jobDetailEntity.getDescription() +
                jobDetailEntity.getResponsibilities() + jobDetailEntity.getRequirements());

        //Save job details
        Long id = postsDao.insertOne(postsEntity);
        postsDao.updateGuidById(this.generateGUID(id), id);

        //Save job company/category/type/location, update related statistics
        if(categoryId != 0){
            termRelationshipsDao.insertOne(id, categoryId);
            termTaxonomyDao.updateTaxonomyCountById(categoryId);
        }
        if(typeId != 0){
            termRelationshipsDao.insertOne(id, typeId);
            termTaxonomyDao.updateTaxonomyCountById(typeId);
        }
        if(locationId != 0){
            termRelationshipsDao.insertOne(id, locationId);
            termTaxonomyDao.updateTaxonomyCountById(locationId);
        }

        return id;
    }

    @Transactional
    protected List<WpPostmetaEntity> addNewPostmetas(Long id, InternshipsJobSummaryEntity jobSummaryEntity, InternshipsJobDetailEntity jobDetailEntity) {
        List<WpPostmetaEntity> postmetaEntities = new ArrayList<>();


        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("_vc_post_settings", "a:1:{s:10:\\\"vc_grid_id\\\";a:0:{}}");
        dataMap.put("_featured", "no");
        dataMap.put("slide_template", "default");

        dataMap.put("_company_name", jobSummaryEntity.getCompany());
        dataMap.put("_company_desc", jobDetailEntity.getCompany());
        Long companyId = companyService.tryGetExistingId(jobSummaryEntity.getCompany());
        if(companyId != 0) {
            dataMap.put("_company_id", String.valueOf(companyId));
            WpPostsEntity postsEntity = companyService.tryGetPostsEntityById(companyId);
            if(postsEntity != null) {
                dataMap.put("_company_name", postsEntity.getPostTitle());
                dataMap.put("_company_desc", postsEntity.getPostContent());
            }
        }
        dataMap.put("_location", jobSummaryEntity.getLocation());
        String expires = this.extractTimeframeDate(jobDetailEntity.getEndDate());
        if (expires != null) dataMap.put("_job_expires", expires);
        String deadline = this.extractDeadlineDate(jobDetailEntity.getDeadline());
        if (deadline != null) dataMap.put("_closing", jobDetailEntity.getDeadline());
//        dataMap.put("_edit_last", "");
//        dataMap.put("_company_logo", "");
//        dataMap.put("_company_website", "");
//        dataMap.put("_company_googleplus", "");
//        dataMap.put("_company_facebook", "");
//        dataMap.put("_company_linkedin", "");
//        dataMap.put("_company_twitter", "");
//        dataMap.put("_company_instagram", "");
//        dataMap.put("_application_email", "");
//        dataMap.put("_noo_views_count", "");
//        dataMap.put("_noo_job_applications_count", "");

        for (Map.Entry<String, String> entry : dataMap.entrySet()) {
            postmetaDao.insertOne(id, entry.getKey(), entry.getValue());
        }

        return postmetaEntities;
    }

    protected WpPostsEntity buildDefaultPostsEntity() {

        WpPostsEntity entity = new WpPostsEntity();
        entity.setPostAuthor(this.defaultPostAuthor);
        entity.setPostStatus("publish");
        entity.setPostType("noo_job");
        entity.setCommentStatus("closed");
        entity.setPingStatus("closed");
        entity.setPostParent(0);
        entity.setMenuOrder(0);

        entity.setPostContentFiltered("");
        entity.setPostExcerpt("");
        entity.setPostPassword("");
        entity.setToPing("");
        entity.setPinged("");
        entity.setPostMimeType("");

        entity.setPostTitle("");
        entity.setPostName("");
        entity.setPostContent("");
        entity.setGuid("");

        Timestamp timestamp = new Timestamp(0);
        entity.setPostDate(timestamp);
        entity.setPostDateGmt(timestamp);
        entity.setPostModified(timestamp);
        entity.setPostModifiedGmt(timestamp);

        return entity;
    }

    protected Timestamp extractPostedDate(String dateText) {
        if (StringUtils.isEmpty(dateText)) return null;

        Timestamp timestamp = new Timestamp(0);
        try {
            Date date = this.postedDateFormat.parse(dateText);
            timestamp = new Timestamp(date.getTime());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return timestamp;
    }

    protected String extractDeadlineDate(String dateText) {
        if (dateText == null) return null;

        String ret = null;
        try {
            Date date = this.deadlineDateFormat.parse(dateText);
            ret = this.dbDateFormat.format(date);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return ret;
    }

    protected String extractTimeframeDate(String dateText) {
        if (StringUtils.isEmpty(dateText)) return null;

        String ret = null;
        try {
            Date date = this.timeframeDateFormat.parse(dateText);
            ret = String.valueOf(date.getTime() / 1000);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return ret;
    }

    protected String generateGUID(Long id) {
        return String.format(this.guidTemplate, id);
    }

}
