package com.example.course.helpers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlHelper {
    public static String buildEmbedUrl(String url) {
        String pattern = "(?<=watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*";
        String id = "";
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(url);

        if (matcher.find()){
             id = matcher.group();
            return "https://www.youtube.com/embed/" + id;
        }
        else {
            return "";
        }
    }
}
