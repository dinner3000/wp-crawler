package com.ocbang.tools.crawler.wordpress.helper;

import org.springframework.util.StringUtils;

public class StringHelper {
    public static String sanitizeTerm(String input){
        if(StringUtils.isEmpty(input)) return input;

        return input.toLowerCase().replaceAll(" ", "-");
    }

    public static String sanitizeTitle(String input){
        if(StringUtils.isEmpty(input)) return input;

        return input.toLowerCase().replaceAll("[^a-z0-9_\\- ]", "")
                .replaceAll(" ", "-").replaceAll("--", "");
    }
}
