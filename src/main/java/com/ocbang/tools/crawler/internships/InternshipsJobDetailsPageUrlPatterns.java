package com.ocbang.tools.crawler.internships;

import com.sun.org.apache.xerces.internal.xs.StringList;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InternshipsJobDetailsPageUrlPatterns {

    private static List<String> majors;

    static {
        majors = Arrays.asList(
                "Marketing",
                "Accounting",
                "Engineering",
                "Finance",
                "Psychology",
                "Law",
                "Biology",
                "Computer Science",
                "Art & Design",
                "Business");
    }

    public static boolean isMatch(String url) {
        boolean ret = getSlashCount(url) >= 4;
        return ret;
    }

    private static int getSlashCount(String url){
        return getSpecifiedStringCount(url,"/");
    }

    private static int getSpecifiedStringCount(String source, String target) {
        // 根据指定的字符构建正则
        Pattern pattern = Pattern.compile(target);
        // 构建字符串和正则的匹配
        Matcher matcher = pattern.matcher(source);
        int count = 0;
        // 循环依次往下匹配
        while (matcher.find()) { // 如果匹配,则数量+1
            count++;
        }
        return count;
    }
}
