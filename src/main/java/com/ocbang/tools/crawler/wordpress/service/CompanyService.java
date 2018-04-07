package com.ocbang.tools.crawler.wordpress.service;

import com.ocbang.tools.crawler.wordpress.dao.PostsDao;
import com.ocbang.tools.crawler.wordpress.entity.WpPostsEntity;
import com.ocbang.tools.crawler.wordpress.entity.WpPostsEntityBuilder;
import com.ocbang.tools.crawler.wordpress.helper.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CompanyService extends CacheableService {

    protected List<WpPostsEntity> cache;

    @Autowired
    private WpPostsEntityBuilder postsEntityBuilder;
    @Autowired
    protected PostsDao postsDao;

    @Override
    public void reloadCache() {
        cache = postsDao.selectManyByPostType("noo_company");
    }

    @Override
    public Long tryGetExistingId(String name) {
        Long ret = 0L;

        if (!StringUtils.isEmpty(name)) {
            Optional<WpPostsEntity> postsEntity = cache.stream()
                    .filter(p -> (p.getPostName().equals(StringHelper.sanitizeTitle(name)))).findFirst();
            if (postsEntity.isPresent()) {
                ret = postsEntity.get().getId();
            }
        }

        return ret;
    }

    public Long addNewOne(Long postAuthor, String name, String desc){

        WpPostsEntity postsEntity = postsEntityBuilder.build(postAuthor, "noo_company");
        postsEntity.setPostTitle(name);
        postsEntity.setPostName(StringHelper.sanitizeTitle(name));
        postsEntity.setPostContent(desc);

        Long id = postsDao.insertOne(postsEntity);
        this.reloadCache();
        return id;
    }

    public WpPostsEntity tryGetPostsEntityById(Long id){
        Optional<WpPostsEntity> postsEntity = cache.stream()
                .filter(p -> (p.getId() == id)).findFirst();
        if (postsEntity.isPresent()) {
            return postsEntity.get();
        }else {
            return null;
        }
    }

    public List<String> getCompanyList(){
        return this.cache.stream().map(p -> p.getPostTitle()).collect(Collectors.toList());
    }
}
