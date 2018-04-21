package com.ocbang.tools.crawler.wordpress.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class TermmetaDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void insertOne(Long termId, String metaKey, String metaValue){
        String insertTemplate = "INSERT INTO `wp_termmeta` (`term_id`, `meta_key`, `meta_value`) " +
                "VALUES (?, ?, ?)";

        jdbcTemplate.update(insertTemplate, termId, metaKey, metaValue);
    }

}
