package com.ocbang.tools.crawler.internships;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class InternshipsJobDetailEntityQualifier {
    public static boolean isQualified(InternshipsJobDetailEntity jobEntity){
        boolean ret = false;

        do {
            if(StringUtils.isEmpty(jobEntity.getCompany())) break;
            if(StringUtils.isEmpty(jobEntity.getDescription())) break;
            if(StringUtils.isEmpty(jobEntity.getTitle())) break;
            if(StringUtils.isEmpty(jobEntity.getPosted())) break;
            ret = true;
        }while (false);

        return ret;
    }
}
