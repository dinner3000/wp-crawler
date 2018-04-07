package com.ocbang.tools.crawler.wordpress.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.PostConstruct;

public abstract class CacheableService {

    Logger logger = LoggerFactory.getLogger(CacheableService.class);

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    public CacheableService(){
    }

    @PostConstruct
    public abstract void reloadCache();

    public abstract Long tryGetExistingId(String name);
}
