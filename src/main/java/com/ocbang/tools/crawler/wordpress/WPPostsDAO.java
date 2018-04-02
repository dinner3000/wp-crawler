package com.ocbang.tools.crawler.wordpress;

import com.ocbang.tools.crawler.internships.InternshipsJobEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.json.XMLTokener.entity;

@Repository
public class WPPostsDAO {

    private static Logger logger = LoggerFactory.getLogger(WPPostsDAO.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private String timestamp = "0000-00-00 00:00:00";
    private Map<String, String> dataKeyValues = new HashMap<>();

    private DateFormat dbDatetimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private DateFormat postedDataFormat = new SimpleDateFormat("MMM dd yyyy");

    private String defaultAuthorId = "1";

    private String guidTemplate = "http://ec2-184-73-133-229.compute-1.amazonaws.com/?p=%d";

    private Long id;

    public Long getId() {
        return id;
    }

    public WPPostsDAO(){
        this.initDefaultValues(this.dataKeyValues);
    }

    public void init(InternshipsJobEntity entity) throws ParseException {
        this.initWithCrawledData(this.dataKeyValues, entity);
    }

    protected void initDefaultValues(Map<String, String> dataMap){

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

    protected void initWithCrawledData(Map<String, String> dataMap, InternshipsJobEntity entity) throws ParseException {
        dataMap.put("post_title", entity.getTitle());
        dataMap.put("post_name", entity.getTitle());

        String postedDate = this.extractPostedDate(entity.getPosted());
        dataMap.put("post_date", postedDate); //Format: 2018-03-31 15:55:32
        dataMap.put("post_date_gmt", postedDate);
        dataMap.put("post_modified", postedDate);
        dataMap.put("post_modified_gmt", postedDate);
        dataMap.put("post_content",
                entity.getDescription() + entity.getResponsibilities() + entity.getRequirements());
//        this.dataKeyValues.put("guid", "");
    }

    protected String extractPostedDate(String postedDate) throws ParseException {
        //Posted: April 01 2018
        postedDate = postedDate.replaceAll("Posted: ", "");
        Date date = this.postedDataFormat.parse(postedDate);
        return this.dbDatetimeFormat.format(date);
    }

    protected boolean recordExists(){
        String sql = "SELECT COUNT(1) FROM wp_posts WHERE `post_author` = ? AND `post_title` = ? AND `post_date` = ?";
        Integer cnt = this.jdbcTemplate.queryForObject(sql, new Object[]{
                this.defaultAuthorId,
                this.dataKeyValues.get("post_title"),
                this.dataKeyValues.get("post_date")},
                Integer.class);

        return cnt > 0;
    }

    public void save(){
        if(!this.recordExists()){
            this.insertRecord();
        }else {
            logger.info("Record already exists, ignore");
        }
    }

    protected void insertRecord(){

        List<String> fields = new ArrayList<>();
        List<String> values = new ArrayList<>();
        List<String> placeholders = new ArrayList<>();

        for (String key:this.dataKeyValues.keySet()) {
            fields.add("`" + key + "`");
            values.add(this.dataKeyValues.get(key));
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
}
