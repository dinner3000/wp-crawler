package com.ocbang.tools.crawler.wordpress.dao;

import com.ocbang.tools.crawler.wordpress.helper.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

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

}
