package com.ocbang.tools.crawler.wordpress;

import com.ocbang.tools.crawler.internships.InternshipsJobDetailEntity;
import com.ocbang.tools.crawler.internships.InternshipsJobSummaryEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Repository
public class WPPostsDAO {

    private static Logger logger = LoggerFactory.getLogger(WPPostsDAO.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private String timestamp = "0000-00-00 00:00:00";
    private Map<String, String> postsKeyValues = new HashMap<>();
    private Map<String, String> metaKeyValues = new HashMap<>();

    private DateFormat dbDatetimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private DateFormat postedDateFormat = new SimpleDateFormat("MMM dd yyyy");
    private DateFormat deadlineDateFormat = new SimpleDateFormat("MMM dd yyyy");

    @Value("${wordpress.default-author-id}")
    private String defaultAuthorId;
    @Value("${wordpress.posts-guid-template}")
    private String guidTemplate;

    private Long id;

    public Long getId() {
        return id;
    }

    public WPPostsDAO(){
    }

    public void init(InternshipsJobDetailEntity entity) throws ParseException {
        this.initPostsWithCrawledData(this.postsKeyValues, entity);
    }

    @PostConstruct
    protected void initPostsDefaultValues(Map<String, String> dataMap){

        //====== Init preset values
        //# ID, user_login, user_pass, user_nicename, user_email, user_url, user_registered, user_activation_key, user_status, display_name
        //'15', 'system', '$P$BvClslregpcE7ePnkEeZPAiRYjyeYa0', 'system', 'noreply@noreply.com', 'http://no-website.com', '2018-04-01 03:23:59', '', '0', 'system system'
        dataMap.put("post_author", this.defaultAuthorId);
        dataMap.put("post_status", "publish"); //Can be publish/draft/auto-draft
        dataMap.put("post_type", "noo_job");
        dataMap.put("post_status", "publish");
        dataMap.put("comment_status", "closed");
        dataMap.put("ping_status", "closed");

        dataMap.put("post_parent", "0");
        dataMap.put("menu_order", "0");

        //====== Empty value by default
        dataMap.put("post_content_filtered", "");
        dataMap.put("post_excerpt", "");
        dataMap.put("post_password", "");
        dataMap.put("to_ping", "");
        dataMap.put("pinged", "");
        dataMap.put("post_mime_type", "");

        //====== Need to set by crawled data later
        dataMap.put("post_title", "");
        dataMap.put("post_date", this.timestamp); //Format: 2018-03-31 15:55:32
        dataMap.put("post_date_gmt", this.timestamp);
        dataMap.put("post_modified", this.timestamp);
        dataMap.put("post_modified_gmt", this.timestamp);
        dataMap.put("post_name", "");
        dataMap.put("post_content", "");
        dataMap.put("guid", "");
    }

    protected void initPostsWithCrawledData(Map<String, String> dataMap, InternshipsJobDetailEntity entity) throws ParseException {
        dataMap.put("post_title", entity.getTitle());
        dataMap.put("post_name", entity.getTitle());

        String postedDate = this.extractPostedDate(entity.getPosted());
        dataMap.put("post_date", postedDate); //Format: 2018-03-31 15:55:32
        dataMap.put("post_date_gmt", postedDate);
        dataMap.put("post_modified", postedDate);
        dataMap.put("post_modified_gmt", postedDate);
        dataMap.put("post_content",
                entity.getDescription() + entity.getResponsibilities() + entity.getRequirements());
//        this.postsKeyValues.put("guid", "");
    }

    protected String extractPostedDate(String dateText) throws ParseException {
        //Posted: April 01 2018
//        postedDate = postedDate.replaceAll("Posted: ", "");
        Date date = this.postedDateFormat.parse(dateText);
        return this.dbDatetimeFormat.format(date);
    }

    protected String extractDeadlineDate(String dateText) throws ParseException {
        //Posted: April 01 2018
//        postedDate = postedDate.replaceAll("Posted: ", "");
        Date date = this.deadlineDateFormat.parse(dateText);
        return this.dbDatetimeFormat.format(date);
    }

    protected boolean postsRecordExists(){
        String sql = "SELECT COUNT(1) FROM wp_posts WHERE `post_author` = ? AND `post_title` = ? AND `post_date` = ?";
        Integer cnt = this.jdbcTemplate.queryForObject(sql, new Object[]{
                this.defaultAuthorId,
                this.postsKeyValues.get("post_title"),
                this.postsKeyValues.get("post_date")},
                Integer.class);

        return cnt > 0;
    }

    public boolean save(){
        boolean ret = false;
        if(!this.postsRecordExists()){
            this.insertPosts();
            ret = true;
        }else {
            logger.info("Record already exists, ignore");
        }
        return ret;
    }

    protected void insertPosts(){

        List<String> fields = new ArrayList<>();
        List<String> values = new ArrayList<>();
        List<String> placeholders = new ArrayList<>();

        for (String key:this.postsKeyValues.keySet()) {
            fields.add("`" + key + "`");
            values.add(this.postsKeyValues.get(key));
            placeholders.add("?");
        }

        final String sqlInsert = "INSERT INTO `wp_posts` (" + StringUtils.arrayToDelimitedString(fields.toArray(), ",") +
                ") VALUES (" + StringUtils.arrayToDelimitedString(placeholders.toArray(), ",") + ")";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        PreparedStatementCreator preparedStatementCreator = new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS);
                for (int i = 0; i < values.size(); i++) {
                    ps.setObject(i+1, values.get(i));
                }
                return ps;
            }
        };
        jdbcTemplate.update(preparedStatementCreator, keyHolder);
        Long id = keyHolder.getKey().longValue();
        this.id = id;

        final String sqlUpdate = "UPDATE `wp_posts` set `guid` = ? WHERE `id` = ?";
        jdbcTemplate.update(sqlUpdate, this.generateGUID(id), id);

    }

    protected String generateGUID(Long id){
        return String.format(this.guidTemplate, id);
    }

    protected void initPostmetaWithCrawledData(Map<String, String> dataMap, InternshipsJobSummaryEntity jobSummaryEntity,
                                               InternshipsJobDetailEntity jobDetailEntity){
        /*
INSERT INTO wp_postmeta (post_id, meta_key, meta_value)
SELECT 1237, '_vc_post_settings', 'a:1:{s:10:\"vc_grid_id\";a:0:{}}' UNION ALL
SELECT 1237, '_featured', 'no' UNION ALL
SELECT 1237, '_edit_last', '2' UNION ALL
SELECT 1237, 'slide_template', 'default' UNION ALL
SELECT 1237, '_location', 'San Jose, California' UNION ALL
SELECT 1237, '_company_name', 'eBay Inc.' UNION ALL
SELECT 1237, '_job_expires', '2015-08-26' UNION ALL
SELECT 1237, '_company_desc', 'eBay Inc., is an American multinational corporation and e-commerce company, providing consumer-to-consumer &amp; business-to-consumer sales services via Internet. It is headquartered in San Jose, California, United States.' UNION ALL
SELECT 1237, '_company_logo', '181' UNION ALL
SELECT 1237, '_company_website', 'http://www.ebay.com/' UNION ALL
SELECT 1237, '_company_googleplus', 'https://plus.google.com/+eBay' UNION ALL
SELECT 1237, '_company_facebook', 'https://www.facebook.com/eBayGlobal' UNION ALL
SELECT 1237, '_company_linkedin', 'https://linkedin.com/company/eBay' UNION ALL
SELECT 1237, '_company_twitter', 'https://twitter.com/eBay' UNION ALL
SELECT 1237, '_company_instagram', 'https://instagram.com/eBay' UNION ALL
SELECT 1237, '_company_id', '275' UNION ALL
SELECT 1237, '_application_email', '' UNION ALL
SELECT 1237, '_closing', '2016-03-17' UNION ALL
SELECT 1237, '_noo_views_count', '84' UNION ALL
SELECT 1237, '_noo_job_applications_count', '0'
         */
        dataMap.put("_vc_post_settings", "a:1:{s:10:\\\"vc_grid_id\\\";a:0:{}}");
        dataMap.put("_featured", "no");
//        dataMap.put("_edit_last", "");
        dataMap.put("slide_template", "default");
        dataMap.put("_location", jobSummaryEntity.getLocation());
        dataMap.put("_company_name", jobSummaryEntity.getCompany());
        dataMap.put("_job_expires", jobDetailEntity.getDeadline());
        dataMap.put("_company_desc", "");
//        dataMap.put("_company_logo", "");
//        dataMap.put("_company_website", "");
//        dataMap.put("_company_googleplus", "");
//        dataMap.put("_company_facebook", "");
//        dataMap.put("_company_linkedin", "");
//        dataMap.put("_company_twitter", "");
//        dataMap.put("_company_instagram", "");
//        dataMap.put("_company_id", "");
//        dataMap.put("_application_email", "");
        dataMap.put("_closing", jobDetailEntity.getDeadline());
//        dataMap.put("_noo_views_count", "");
//        dataMap.put("_noo_job_applications_count", "");

    }

    protected void insertPostmeta(Map<String, String> dataMap){
    }

}
