package utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class DateUtils {
    

    public static String dateBuilder(String date){
            
        Pattern DATETIME_PATTERN  = Pattern.compile(  "(\\d{4})(?:-(\\d{2}))?(?:-(\\d{2}))?(?:\\s+(\\d{2}))?(?::(\\d{2}))?(?::(\\d{2}))?");
        Matcher matcher = DATETIME_PATTERN.matcher(date);

        matcher.find();

        String year = matcher.group(1);
        String month = matcher.group(2);
        String day = matcher.group(3);
        String hour= matcher.group(4);
        String minute = matcher.group(5);
        String second = matcher.group(6);

        return String.format("%s-%s-%s %s:%s:%s",
            year,
            month != null ? month : "01",
            day != null ? day : "01",
            hour != null ? hour : "00",
            minute != null ? minute : "00",
            second != null ? second : "00");
    }
}
