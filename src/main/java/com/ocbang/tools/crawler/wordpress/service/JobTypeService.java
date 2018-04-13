package com.ocbang.tools.crawler.wordpress.service;

import com.ocbang.tools.crawler.wordpress.dao.TermTaxonomyDao;
import com.ocbang.tools.crawler.wordpress.dao.TermsDao;
import com.ocbang.tools.crawler.wordpress.helper.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;

@Service
public class JobTypeService extends CacheableService {

    protected Map<String, Long> cache;

    @Autowired
    protected TermsDao termsDAO;
    @Autowired
    protected TermTaxonomyDao termTaxonomyDao;

    protected final String taxonomy = "job_type";

    @Override
    public void reloadCache(){
        cache = termsDAO.selectManyByTaxonomy(taxonomy);
    }

    public Long addNewOne(String name){
        Long id = termsDAO.insertOne(name);
        termTaxonomyDao.insertOne(taxonomy, "");
        this.reloadCache();
        return id;
    }

    @Override
    public Long tryGetExistingId(String name){
        Long ret = 0L;

        if(!StringUtils.isEmpty(name)) {
            ret = cache.get(StringHelper.sanitizeTerm(name));
        }
        if (ret == null) ret = 0L;

        return ret;
    }
}
