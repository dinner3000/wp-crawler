package com.ocbang.tools.crawler.wordpress.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class PostmetaDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void insertOne(Long postId, String metaKey, String metaValue){
        String sqlTemplate = "INSERT INTO wp_postmeta (post_id, meta_key, meta_value) VALUES (?, ?, ?)";

        jdbcTemplate.update(sqlTemplate, postId, metaKey, metaValue);
    }

}
