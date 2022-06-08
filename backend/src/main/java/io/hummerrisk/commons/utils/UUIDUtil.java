package io.hummerrisk.commons.utils;

import java.util.UUID;

public class UUIDUtil {
    private UUIDUtil() {
    }

    public static String newUUID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }
}
