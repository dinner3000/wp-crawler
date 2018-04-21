package com.ocbang.tools.crawler.wordpress.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

@Repository
public class TermTaxonomyDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Long insertOne(Long termId, String taxonomy, String description){
        String insertTemplate = "INSERT INTO `wp_term_taxonomy` (`term_id`, `taxonomy`, `description`, `parent`, `count`) " +
                "VALUES (?, ?, ?, 0, 0)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        PreparedStatementCreator preparedStatementCreator = new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(insertTemplate, Statement.RETURN_GENERATED_KEYS);
                ps.setObject(1, termId);
                ps.setObject(2, taxonomy);
                ps.setObject(3, description);
                return ps;
            }
        };
//        jdbcTemplate.update(insertTemplate, termId, taxonomy, description);
        jdbcTemplate.update(preparedStatementCreator, keyHolder);
        Long id = keyHolder.getKey().longValue();
        return id;
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

    public Map<String, Long> selectManyByTaxonomy(String taxonomy){
        String selectTemplate = "SELECT tm.slug, tt.term_taxonomy_id FROM wp_term_taxonomy tt " +
                "LEFT JOIN wp_terms tm ON tm.term_id= tt.term_id " +
                "WHERE tt.taxonomy = ?";

        Map<String, Long> map = new HashMap<>();
        jdbcTemplate.query(selectTemplate, new Object[]{taxonomy}, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet resultSet) throws SQLException {
                map.put(resultSet.getString("slug"), resultSet.getLong("term_taxonomy_id"));
            }
        });
        return map;
    }
}
