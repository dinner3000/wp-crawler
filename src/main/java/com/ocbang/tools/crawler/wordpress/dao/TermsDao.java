package com.ocbang.tools.crawler.wordpress.dao;

import com.ocbang.tools.crawler.wordpress.helper.StringHelper;
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
public class TermsDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Long insertOne(String name){
        String insertTemplate = "INSERT INTO `wp_terms` (`name`, `slug`, `term_group`) " +
                "VALUES (?, ?, 0)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        PreparedStatementCreator preparedStatementCreator = new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(insertTemplate, Statement.RETURN_GENERATED_KEYS);
                ps.setObject(1, name);
                ps.setObject(2, StringHelper.sanitizeTerm(name));
                return ps;
            }
        };
        jdbcTemplate.update(preparedStatementCreator, keyHolder);
        Long id = keyHolder.getKey().longValue();
        return id;
    }

    public Map<String, Long> selectManyByTaxonomy(String taxonomy){
        String selectTemplate = "SELECT tm.slug, tm.term_id FROM wp_term_taxonomy tt " +
                "LEFT JOIN wp_terms tm ON tm.term_id= tt.term_id " +
                "WHERE tt.taxonomy = ?";

        Map<String, Long> map = new HashMap<>();
        jdbcTemplate.query(selectTemplate, new Object[]{taxonomy}, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet resultSet) throws SQLException {
                map.put(resultSet.getString("slug"), resultSet.getLong("term_id"));
            }
        });
        return map;
    }
}
