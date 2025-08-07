package com.helga.lib.normalizer;

import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PhoneConverter {
    public String convertPhone(String phone) {

        var cleanedNumber = phone.replaceAll("[^\\d]", "");

        StringBuilder builder = new StringBuilder();

        Pattern pattern = Pattern.compile("(\\d{1})(\\d{3})(\\d{3})(\\d{2})(\\d{2})");
        Matcher matcher = pattern.matcher(cleanedNumber);

        if (matcher.matches()) {
            builder.append("плюс ").append(matcher.group(1)).append(", ");
            convertFromThreeParts(2, matcher, builder);
            convertFromThreeParts(3, matcher, builder);
            convertFromTwoParts(4, matcher, builder);
            convertFromTwoParts(5, matcher, builder);
        } else {
            pattern = Pattern.compile("(\\d{3})(\\d{3})(\\d{2})(\\d{2})");
            matcher = pattern.matcher(cleanedNumber);
            if (matcher.matches()) {
                convertFromThreeParts(1, matcher, builder);
                convertFromThreeParts(2, matcher, builder);
                convertFromTwoParts(3, matcher, builder);
                convertFromTwoParts(4, matcher, builder);
            } else {
                pattern = Pattern.compile("(\\d{3})(\\d{3})(\\d{2})(\\d{2})");
                matcher = pattern.matcher(cleanedNumber);
                if (matcher.matches()) {
                    builder.append(matcher.group(1)).append(", ");
                    builder.append(matcher.group(2)).append(", ");
                    builder.append(matcher.group(3)).append(", ");
                    builder.append(matcher.group(4));
                }
            }
        }
        return builder.toString().trim();
    }

    private void convertFromTwoParts(int group, Matcher matcher, StringBuilder builder) {
        if (matcher.group(group).startsWith("0")) {
            String end = String.valueOf(matcher.group(group).charAt(1));
            if (end.equals("0")) {
                end = "ноль ";
            }
            builder.append("ноль ").append(end).append(" ");
        } else {
            builder.append(matcher.group(group)).append(" ");
        }
    }

    private void convertFromThreeParts(int group, Matcher matcher, StringBuilder builder) {
        if (matcher.group(group).startsWith("0")) {
            String end = matcher.group(group).substring(1, 3);
            builder.append("ноль ");
            if (end.startsWith("0")) {
                builder.append("ноль ");
                String endPart = String.valueOf(matcher.group(group).charAt(2));
                if (end.startsWith("0") && endPart.equals("0")) {
                    builder.append("ноль ");
                } else {
                    builder.append(endPart).append(" ");
                }
            } else {
                builder.append(end).append(" ");
            }
        } else {
            builder.append(matcher.group(group)).append(" ");
        }
    }
}
