package com.ocbang.tools.crawler.wordpress;

import com.ocbang.tools.crawler.internships.InternshipsJobEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapperResultSetExtractor;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Repository
public class WPTermsDAO {

    private Long id;

    private Map<String, Integer> jobCategoryMap = new HashMap<>();
    private Map<String, Integer> jobTypeMap = new HashMap<>();
    private Map<String, Integer> jobLocationMap = new HashMap<>();

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public WPTermsDAO(){
    }

    public void init(Long id, InternshipsJobEntity entity){
        this.id = id;
    }

    @PostConstruct
    protected void loadPredefinedTerms(){

        String sqlTemplate = "SELECT tm.slug, tm.term_id FROM wp_term_taxonomy tt " +
                "LEFT JOIN wp_terms tm ON tm.term_id= tt.term_id " +
                "WHERE tt.taxonomy = ?";

        Map<String, Integer> categoryMap = new HashMap<>();
         jdbcTemplate.query(sqlTemplate, new Object[]{"job_category"}, new RowCallbackHandler() {
             @Override
             public void processRow(ResultSet resultSet) throws SQLException {
                 categoryMap.put(resultSet.getString("slug"), resultSet.getInt("term_id"));
             }
         });
        jobCategoryMap = categoryMap;

        Map<String, Integer> typeMap = new HashMap<>();
        jdbcTemplate.query(sqlTemplate, new Object[]{"job_type"}, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet resultSet) throws SQLException {
                typeMap.put(resultSet.getString("slug"), resultSet.getInt("term_id"));
            }
        });
        jobTypeMap = typeMap;

        Map<String, Integer> locationMap = new HashMap<>();
        jdbcTemplate.query(sqlTemplate, new Object[]{"job_location"}, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet resultSet) throws SQLException {
                locationMap.put(resultSet.getString("slug"), resultSet.getInt("term_id"));
            }
        });
        jobLocationMap = locationMap;
    }

    protected Integer searchJobCategoryId(){
//        SELECT tm.*, tt.taxonomy
//        FROM wp_term_taxonomy tt
//        LEFT JOIN wp_terms tm ON tm.term_id= tt.term_id
//        WHERE tt.taxonomy='job_category';
        return 0;
    }

    protected Integer searchJobTypeId(){
//        SELECT tm.*, tt.taxonomy
//        FROM wp_term_taxonomy tt
//        LEFT JOIN wp_terms tm ON tm.term_id= tt.term_id
//        WHERE tt.taxonomy='job_type';
        return 0;
    }

    protected Integer searchJobLocationId(){
//        SELECT tm.*, tt.taxonomy
//        FROM wp_term_taxonomy tt
//        LEFT JOIN wp_terms tm ON tm.term_id= tt.term_id
//        WHERE tt.taxonomy='job_location';
        return 0;
    }

    protected void insertJobCategoryRecord(){

    }

    protected void insertJobTypeRecord(){

    }

    protected void insertJobLocationRecord(){

    }

    public void save(){

    }
}
