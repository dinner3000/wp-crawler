package com.ocbang.tools.crawler.wordpress.entity;

import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
public class WpPostsEntityBuilder {
    public WpPostsEntity build(Long postAuthor, String postType){
        WpPostsEntity entity = new WpPostsEntity();

        entity.setPostAuthor(postAuthor);
        entity.setPostType(postType);

        entity.setPostTitle("");
        entity.setPostName("");
        entity.setPostContent("");
        entity.setGuid("");

        Timestamp timestamp = new Timestamp(0);
        entity.setPostDate(timestamp);
        entity.setPostDateGmt(timestamp);
        entity.setPostModified(timestamp);
        entity.setPostModifiedGmt(timestamp);

        entity.setPostStatus("publish");
        entity.setCommentStatus("closed");
        entity.setPingStatus("closed");
        entity.setPostParent(0);
        entity.setMenuOrder(0);

        entity.setPostContentFiltered("");
        entity.setPostExcerpt("");
        entity.setPostPassword("");
        entity.setToPing("");
        entity.setPinged("");
        entity.setPostMimeType("");

        return entity;
    }
}
