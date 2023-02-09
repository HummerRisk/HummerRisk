package com.hummer.common.core.utils;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

/**
 * @ClassName IDGenerator
 * @Author harris
 **/
public class IDGenerator {

    private final static String SEPARATOR = "-";

    private static String get(String prefix) {
        StringBuilder stringBuilder = new StringBuilder();
        String dateStr = DateFormatUtils.format(DateUtils.truncate(new Date(), Calendar.MILLISECOND), "MMddHHmm", TimeZone.getTimeZone("GMT+08:00"));
        stringBuilder.append(prefix).append(SEPARATOR).append(dateStr).append(SEPARATOR);
        stringBuilder.append(UUID.randomUUID().toString().substring(0, 8).toLowerCase());
        return stringBuilder.toString();
    }

    public static String newBusinessId(String prefix, String... workspaceId) {
        return get(prefix);
    }

}
