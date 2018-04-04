package com.ocbang.tools.crawler.wordpress;

import com.ocbang.tools.crawler.internships.InternshipsJobSummaryEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Repository
public class WPTermsDAO {

    private Long id;

    private Long categoryId;
    private Long typeId;
    private Long locationId;

    private Map<String, Long> jobCategoryMap;
    private Map<String, Long> jobTypeMap;
    private Map<String, Long> jobLocationMap;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public WPTermsDAO() {
    }

    @PostConstruct
    protected void loadPredefinedTerms() {

        String sqlTemplate = "SELECT tm.slug, tm.term_id FROM wp_term_taxonomy tt " +
                "LEFT JOIN wp_terms tm ON tm.term_id= tt.term_id " +
                "WHERE tt.taxonomy = ?";

        Map<String, Long> categoryMap = new HashMap<>();
        jdbcTemplate.query(sqlTemplate, new Object[]{"job_category"}, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet resultSet) throws SQLException {
                categoryMap.put(resultSet.getString("slug"), resultSet.getLong("term_id"));
            }
        });
        jobCategoryMap = categoryMap;

        Map<String, Long> typeMap = new HashMap<>();
        jdbcTemplate.query(sqlTemplate, new Object[]{"job_type"}, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet resultSet) throws SQLException {
                typeMap.put(resultSet.getString("slug"), resultSet.getLong("term_id"));
            }
        });
        jobTypeMap = typeMap;

        Map<String, Long> locationMap = new HashMap<>();
        jdbcTemplate.query(sqlTemplate, new Object[]{"job_location"}, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet resultSet) throws SQLException {
                locationMap.put(resultSet.getString("slug"), resultSet.getLong("term_id"));
            }
        });
        jobLocationMap = locationMap;
    }

    public void init(Long id, InternshipsJobSummaryEntity jobListItem) {
        this.id = id;
        this.categoryId = this.searchJobCategoryId(jobListItem.getCategory());
        this.typeId = this.searchJobTypeId(jobListItem.getTimeType());
        this.locationId = this.searchJobLocationId(jobListItem.getLocation());
    }

    public Long searchJobCategoryId(String keyWord) {
        Long ret = 0L;

        if(!StringUtils.isEmpty(keyWord)) {
            keyWord = keyWord.toLowerCase();
            keyWord = keyWord.replaceAll(" ", "-");
            ret = this.jobCategoryMap.get(keyWord);
        }

        if (ret == 0L || ret == null) ret = 51L;

        return ret;
    }

    public Long searchJobTypeId(String keyWord) {
        Long ret = 0L;

        if(!StringUtils.isEmpty(keyWord)) {
            keyWord = keyWord.toLowerCase();
            keyWord = keyWord.replaceAll(" ", "-");
            ret = this.jobTypeMap.get(keyWord);
        }

        if (ret == null) ret = 0L;

        return ret;
    }

    public Long searchJobLocationId(String keyWord) {
        Long ret = 0L;

        if(!StringUtils.isEmpty(keyWord)) {
            String[] buf = keyWord.split(",");
            if (buf.length > 0) {
                keyWord = buf[0].toLowerCase();
                keyWord = keyWord.replaceAll(" ", "-");
                ret = this.jobLocationMap.get(keyWord);
            }
        }

        if (ret == null) ret = 0L;

        return ret;
    }

    protected void insertTermRelationships(Long termId) {
        //INSERT INTO `wp_term_relationships` (`objectid`, `term_taxonomy_id`)
        final String sqlInsert = "INSERT INTO `wp_term_relationships` (`object_id`, `term_taxonomy_id`) VALUES (?, ?)";

        jdbcTemplate.update(sqlInsert, this.id, termId);
    }

    public void save() {
        if(this.categoryId != 0) {
            this.insertTermRelationships(this.categoryId);
            this.updateTaxonomyCount(this.categoryId);
        }
        if(this.typeId != 0) {
            this.insertTermRelationships(this.typeId);
            this.updateTaxonomyCount(this.typeId);
        }
        if(this.locationId != 0) {
            this.insertTermRelationships(this.locationId);
            this.updateTaxonomyCount(this.locationId);
        }
    }

    protected void updateTaxonomyCount(Long termId) {
        /*
        UPDATE `wp_term_taxonomy` tt, (
          SELECT term_taxonomy_id, COUNT(*) cnt FROM wp_term_relationships, wp_posts
          WHERE wp_posts.ID = wp_term_relationships.object_id
                AND post_status = 'publish'
                AND post_type IN ('noo_job')
                AND term_taxonomy_id = 40) tc
          SET tt.`count` = tc.cnt
        WHERE tt.`term_taxonomy_id` = 40 AND tt.term_taxonomy_id = tc.term_taxonomy_id;
        */
        final String sqlInsert = "UPDATE `wp_term_taxonomy` tt, ( " +
                "SELECT term_taxonomy_id, COUNT(*) cnt FROM wp_term_relationships, wp_posts " +
                "WHERE wp_posts.ID = wp_term_relationships.object_id " +
                "AND post_status = 'publish' AND post_type IN ('noo_job') " +
                "AND term_taxonomy_id = ?) tc " +
                "SET tt.`count` = tc.cnt " +
                "WHERE tt.`term_taxonomy_id` = ? AND tt.term_taxonomy_id = tc.term_taxonomy_id";

        jdbcTemplate.update(sqlInsert, termId, termId);
    }
}
