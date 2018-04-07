package com.ocbang.tools.crawler.wordpress.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class TermTaxonomyDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void insertOne(String taxonomy, String description){
        String insertTemplate = "INSERT INTO `wp_term_taxonomy` (`taxonomy`, `description`, `parent`, `count`) " +
                "VALUES (?, ?, 0, 0)";

        jdbcTemplate.update(insertTemplate, taxonomy, description);
    }

    public void updateTaxonomyCountById(Long termId){
        String updateSql = "UPDATE `wp_term_taxonomy` tt, ( " +
                "SELECT term_taxonomy_id, COUNT(*) cnt FROM wp_term_relationships, wp_posts " +
                "WHERE wp_posts.ID = wp_term_relationships.object_id " +
                "AND post_status = 'publish' AND post_type IN ('noo_job') " +
                "AND term_taxonomy_id = ?) tc " +
                "SET tt.`count` = tc.cnt " +
                "WHERE tt.`term_taxonomy_id` = ? AND tt.term_taxonomy_id = tc.term_taxonomy_id";

        jdbcTemplate.update(updateSql, termId, termId);
    }
}
