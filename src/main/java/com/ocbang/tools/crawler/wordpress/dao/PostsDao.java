package com.ocbang.tools.crawler.wordpress.dao;

import com.ocbang.tools.crawler.wordpress.entity.WpPostsEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class PostsDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Long insertOne(WpPostsEntity entity){
        final String sqlInsert = "INSERT INTO `wp_posts` (" +
                "`post_author`, `post_date`, `post_date_gmt`, `post_content`, `post_content_filtered`, " +
                "`post_title`, `post_excerpt`, `post_status`, `post_type`, `comment_status`, " +
                "`ping_status`, `post_password`, `post_name`, `to_ping`, `pinged`, " +
                "`post_modified`, `post_modified_gmt`, `post_parent`, `menu_order`, `post_mime_type`, " +
                "`guid`) VALUES (" +
                "?, ?, ?, ?, ?, " +
                "?, ?, ?, ?, ?, " +
                "?, ?, ?, ?, ?, " +
                "?, ?, ?, ?, ?, " +
                "?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        PreparedStatementCreator preparedStatementCreator = new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS);
                ps.setObject(1, entity.getPostAuthor());
                ps.setObject(2, entity.getPostDate());
                ps.setObject(3, entity.getPostDateGmt());
                ps.setObject(4, entity.getPostContent());
                ps.setObject(5, entity.getPostContentFiltered());
                ps.setObject(6, entity.getPostTitle());
                ps.setObject(7, entity.getPostExcerpt());
                ps.setObject(8, entity.getPostStatus());
                ps.setObject(9, entity.getPostType());
                ps.setObject(10, entity.getCommentStatus());
                ps.setObject(11, entity.getPingStatus());
                ps.setObject(12, entity.getPostPassword());
                ps.setObject(13, entity.getPostName());
                ps.setObject(14, entity.getToPing());
                ps.setObject(15, entity.getPinged());
                ps.setObject(16, entity.getPostModified());
                ps.setObject(17, entity.getPostModifiedGmt());
                ps.setObject(18, entity.getPostParent());
                ps.setObject(19, entity.getMenuOrder());
                ps.setObject(20, entity.getPostMimeType());
                ps.setObject(21, entity.getGuid());
                return ps;
            }
        };
        jdbcTemplate.update(preparedStatementCreator, keyHolder);
        Long id = keyHolder.getKey().longValue();

        return id;
    }

    public void updateGuidById(String guid, Long id){
        String sqlUpdate = "UPDATE `wp_posts` set `guid` = ? WHERE `id` = ?";
        jdbcTemplate.update(sqlUpdate, guid, id);
    }

    public List<WpPostsEntity> selectManyByPostType(String postType){
        String sqlSelect = "SELECT * FROM wp_posts WHERE post_type = ?";

        List<WpPostsEntity> postsEntities = new ArrayList<>();
        jdbcTemplate.query(sqlSelect, new Object[]{postType}, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet resultSet) throws SQLException {
                WpPostsEntity postsEntity = new WpPostsEntity();
                postsEntity.setId(resultSet.getLong("id"));
                postsEntity.setPostTitle(resultSet.getString("post_title"));
                postsEntity.setPostName(resultSet.getString("post_name"));
                postsEntity.setPostContent(resultSet.getString("post_content"));
                postsEntities.add(postsEntity);
            }
        });
        return postsEntities;
    }

    public Long getPostCountByComboKeys(Long postAuthor, String postName, Timestamp postDate){
        String sql = "SELECT COUNT(1) FROM wp_posts WHERE `post_author` = ? AND `post_name` = ? AND `post_date` = ?";
        Long cnt = this.jdbcTemplate.queryForObject(sql,
                new Object[]{postAuthor, postName, postDate},
                Long.class);

        return cnt;
    }
}
