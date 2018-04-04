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

    //0000-00-00 00:00:00
    private DateFormat dbDatetimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    //0000-00-00 00:00:00
    private DateFormat dbDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    //April 04 2018
    private DateFormat postedDateFormat = new SimpleDateFormat("MMM dd yyyy");
    //May 4, 2018
    private DateFormat deadlineDateFormat = new SimpleDateFormat("MMM dd, yyyy");
    //04/05/18 — 07/05/18
    private DateFormat timeframeDateFormat = new SimpleDateFormat("MM/dd/yy");

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

    protected void initPostsDefaultValues(Map<String, String> dataMap) {

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

    public void init(InternshipsJobSummaryEntity jobSummaryEntity, InternshipsJobDetailEntity jobDetailEntity) {
        this.initPostsDefaultValues(this.postsKeyValues);
        this.initPostsWithCrawledData(this.postsKeyValues, jobDetailEntity);
        this.initPostmetaWithCrawledData(this.metaKeyValues, jobSummaryEntity, jobDetailEntity);
    }

    protected String convertToPostName(String title){
        return title.toLowerCase().replaceAll("[^a-z0-9_\\- ]", "")
                .replaceAll(" ", "-").replaceAll("--", "");
    }

    protected void initPostsWithCrawledData(Map<String, String> dataMap, InternshipsJobDetailEntity entity) {
        dataMap.put("post_title", entity.getTitle());
        dataMap.put("post_name", this.convertToPostName(entity.getTitle()));

        String postedDate = this.extractPostedDate(entity.getPosted());
        dataMap.put("post_date", postedDate); //Format: 2018-03-31 15:55:32
        dataMap.put("post_date_gmt", postedDate);
        dataMap.put("post_modified", postedDate);
        dataMap.put("post_modified_gmt", postedDate);
        dataMap.put("post_content",
                entity.getDescription() + entity.getResponsibilities() + entity.getRequirements());
//        this.postsKeyValues.put("guid", "");
    }

    protected void initPostmetaWithCrawledData(Map<String, String> dataMap, InternshipsJobSummaryEntity jobSummaryEntity,
                                               InternshipsJobDetailEntity jobDetailEntity) {
        dataMap.put("_vc_post_settings", "a:1:{s:10:\\\"vc_grid_id\\\";a:0:{}}");
        dataMap.put("_featured", "no");
//        dataMap.put("_edit_last", "");
        dataMap.put("slide_template", "default");
        dataMap.put("_location", jobSummaryEntity.getLocation());
        dataMap.put("_company_name", jobSummaryEntity.getCompany());

        String expires = this.extractTimeframeDate(jobDetailEntity.getEndDate());
        if (expires != null) dataMap.put("_job_expires", expires);
        dataMap.put("_company_desc", jobDetailEntity.getCompany());
//        dataMap.put("_company_logo", "");
//        dataMap.put("_company_website", "");
//        dataMap.put("_company_googleplus", "");
//        dataMap.put("_company_facebook", "");
//        dataMap.put("_company_linkedin", "");
//        dataMap.put("_company_twitter", "");
//        dataMap.put("_company_instagram", "");
//        dataMap.put("_company_id", "");
//        dataMap.put("_application_email", "");
        String deadline = this.extractDeadlineDate(jobDetailEntity.getDeadline());
        if (deadline != null) dataMap.put("_closing", jobDetailEntity.getDeadline());
//        dataMap.put("_noo_views_count", "");
//        dataMap.put("_noo_job_applications_count", "");

    }

    protected boolean postsRecordExists() {
        String sql = "SELECT COUNT(1) FROM wp_posts WHERE `post_author` = ? AND `post_title` = ? AND `post_date` = ?";
        Integer cnt = this.jdbcTemplate.queryForObject(sql, new Object[]{
                        this.defaultAuthorId,
                        this.postsKeyValues.get("post_title"),
                        this.postsKeyValues.get("post_date")},
                Integer.class);

        return cnt > 0;
    }

    public boolean save() {
        boolean ret = false;
        if (!this.postsRecordExists()) {
            this.insertPosts();
            this.insertPostmeta();
            ret = true;
        } else {
            logger.info("Record already exists, ignore");
        }
        return ret;
    }

    protected void insertPosts() {

        List<String> fields = new ArrayList<>();
        List<String> values = new ArrayList<>();
        List<String> placeholders = new ArrayList<>();

        for (String key : this.postsKeyValues.keySet()) {
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
                    ps.setObject(i + 1, values.get(i));
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

    protected void insertPostmeta() {
        /*
        INSERT INTO wp_postmeta (post_id, meta_key, meta_value)
        SELECT 1237, '_vc_post_settings', 'a:1:{s:10:\"vc_grid_id\";a:0:{}}' UNION ALL
        SELECT 1237, '_featured', 'no' UNION ALL
        ...
         */
        String sqlTemplate = "INSERT INTO wp_postmeta (post_id, meta_key, meta_value) ";
        List<String> dataList = new ArrayList<>();
        for (Map.Entry<String, String> entry:this.metaKeyValues.entrySet()){
            dataList.add(String.format("SELECT %d, '%s', '%s'", this.id, entry.getKey(), entry.getValue()));
        }
        sqlTemplate += StringUtils.arrayToDelimitedString(dataList.toArray(), " UNION ALL ");

        jdbcTemplate.update(sqlTemplate);

    }

    protected String extractPostedDate(String dateText) {
        String ret = this.timestamp;
        try {
            Date date = this.postedDateFormat.parse(dateText);
            ret = this.dbDatetimeFormat.format(date);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return ret;
    }

    protected String extractDeadlineDate(String dateText) {
        if(dateText == null) return null;

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
        if(dateText == null) return null;

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
