package com.ocbang.tools.crawler.wordpress.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class TermRelationshipsDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void insertOne(Long postId, Long termId){
        String sqlInsert = "INSERT INTO `wp_term_relationships` " +
                "(`object_id`, `term_taxonomy_id`) VALUES (?, ?)";

        jdbcTemplate.update(sqlInsert, postId, termId);
    }
}
